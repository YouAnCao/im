package com.c2u.controller;

import com.c2u.dto.Result;
import com.c2u.entity.IMServerInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/router")
public class RouterController {

    @GetMapping
    public Result getIMServerInfo(HttpServletRequest request) {
        String token = request.getHeader("x-token");
        if (token == null) {
            return Result.fail(1, "the token can not be null.");
        }
        return Result.ok(new IMServerInfo("127.0.0.1", 8080, System.currentTimeMillis()));
    }
}
