package com.fitguard.controller;

import com.fitguard.Security.jwt.JwtService;
import com.fitguard.dto.AppUserDto;
import com.fitguard.dto.JwtResponse;
import com.fitguard.dto.LoginRequestDto;
import com.fitguard.entity.UserDetailsImpl;
import com.fitguard.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);


    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private boolean isPasswordMatches(String rawPassword, String encodedPassword){
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    @GetMapping("/public")
    public String sayHello(){
        return "Hello... This page is for public";
    }



    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody AppUserDto userDto){
        if(userService.findExistingUser(userDto.getEmail())){
            return ResponseEntity.badRequest().body("Email is already exist!!");
        }

        if(isPasswordMoreThanFiveChar(userDto.getPassword())){
            return ResponseEntity.badRequest().body("Your password characters are less than five!");
        }
        userService.registration(AppUserDto.toUser(userDto));
        return new ResponseEntity<>("Registration is success", HttpStatus.CREATED);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequest, HttpServletRequest request, HttpServletResponse response){
        log.info("Email is: {}", loginRequest.getEmail());

        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        log.info("UserDetailsImpl: {}", userDetails);

        var user = request.getUserPrincipal().getName();
        log.info("User info using HttpServlet Request: {}", user);


        List<String> role = userDetails.getAuthorities()
                .stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        log.info("role: {}", role);

        var token = jwtService.generateToken(userDetails);

        log.info("Jwt token: {}", token);

        var jwtResponse = new JwtResponse(token, userDetails.getUsername(), userDetails.getId(), role);
        return new ResponseEntity<>(jwtResponse, HttpStatus.OK);
    }


    private boolean isPasswordMoreThanFiveChar(String password){
        return password.length() > 5;
    }


}
