package com.fitguard.controller;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/protected")
public class ProtectedController {

    @GetMapping()
    public String testProtectedController(HttpServletRequest request){
        String userName = request.getUserPrincipal().getName();
        return "Hello " + userName + "!";
    }
}
