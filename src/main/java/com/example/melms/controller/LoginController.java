package com.example.melms.controller;

import com.example.melms.mapper.LoginMapper;
import com.example.melms.pojo.Result;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import jakarta.annotation.Resource;

@RestController
public class LoginController {
    @Resource
    private LoginMapper loginMapper;
    @PostMapping("/req/account/login")
    public Result login(@RequestBody Map<String, String> params) {
        String account = params.get("account");
        String password = params.get("password");
        String accountId = loginMapper.login(account, password);
        if (accountId != null) {
            return Result.success("ok", accountId);
        }
        return Result.fail("401","Invalid credentials",null);
    }

    @PostMapping("/req/account/resetPwd")
    public Result resetPassword(@RequestBody Map<String, String> params) {
        return Result.fail("","",null);
    }

    @GetMapping("/req/getVCode")
    public Result getVerificationCode(@RequestParam String email) {
        return Result.fail("","",null);
    }
}
