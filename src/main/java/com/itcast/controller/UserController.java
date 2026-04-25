package com.itcast.controller;

import com.itcast.constant.JwtClaimsConstant;
import com.itcast.dto.LoginDTO;
import com.itcast.entity.User;
import com.itcast.mapper.UserMapper;
import com.itcast.properties.JwtProperties;
import com.itcast.result.Result;
import com.itcast.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user/user")
@Tag(name = "用户接口")
@Slf4j
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result login(@RequestBody LoginDTO loginDTO) {

        User user = userMapper.getByName(loginDTO.getUsername());

        if (user == null) {
            return Result.error("用户不存在");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());

        String token = JwtUtil.createJWT(
            jwtProperties.getUserSecretKey(),
            jwtProperties.getUserTtl(),
            claims
        );

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("id", user.getId());
        data.put("name", user.getName());

        return Result.success(data);
    }
}