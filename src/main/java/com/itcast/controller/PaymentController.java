package com.itcast.controller;

import com.itcast.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user/payment")
@Tag(name = "支付接口", description = "微信支付相关接口")
@Slf4j
public class PaymentController {

    @PostMapping("/pay")
    @Operation(summary = "创建支付")
    public Result<Map<String, Object>> pay(@RequestBody Map<String, Object> params) {
        String orderNumber = (String) params.get("orderNumber");
        Double amount = Double.valueOf(params.get("amount").toString());

        Map<String, Object> data = new HashMap<>();
        data.put("orderNumber", orderNumber);
        data.put("amount", amount);
        data.put("qrcode", "https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=" + orderNumber);

        return Result.success(data);
    }

    @PostMapping("/notify")
    @Operation(summary = "支付回调")
    public Result<String> notify(@RequestBody Map<String, Object> params) {
        log.info("支付回调: {}", params);
        return Result.success("SUCCESS");
    }
}
