package com.itcast.controller;

import com.itcast.constant.JwtClaimsConstant;
import com.itcast.dto.LoginDTO;
import com.itcast.entity.Employee;
import com.itcast.mapper.EmployeeMapper;
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
@RequestMapping("/admin/employee")
@Tag(name = "管理员接口")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("/login")
    @Operation(summary = "管理员登录")
    public Result login(@RequestBody LoginDTO loginDTO) {
        Employee employee = employeeMapper.getByUsername(loginDTO.getUsername());

        if (employee == null) {
            return Result.error("账号不存在");
        }

        if (employee.getStatus() == 0) {
            return Result.error("账号已禁用");
        }

        if (!loginDTO.getPassword().equals("123456")) {
            return Result.error("密码错误");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());

        String token = JwtUtil.createJWT(
            jwtProperties.getAdminSecretKey(),
            jwtProperties.getAdminTtl(),
            claims
        );

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("id", employee.getId());
        data.put("username", employee.getUsername());
        data.put("name", employee.getName());

        return Result.success(data);
    }
}