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
        String name = params.get("name");
        String password = params.get("password");
        Map<String,Object> res = loginMapper.login(name, password);
        if (res != null && res.get("account_id") != null) {
            return Result.success("ok", res);
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
