package com.SecurityApp.controllers;

import com.SecurityApp.dto.LoginDTO;
import com.SecurityApp.dto.SignupDTO;
import com.SecurityApp.dto.UserDTO;
import com.SecurityApp.services.AuthService;
import com.SecurityApp.services.UserService;
import jakarta.persistence.GeneratedValue;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private  final UserService userService;
    private  final AuthService authService;

    public AuthController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService=authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDTO>signup(@RequestBody SignupDTO signupDTO){
        UserDTO userDTO=userService.signup(signupDTO);

        return  ResponseEntity.ok(userDTO);

    }

//    login
    @PostMapping("/login")
    public  ResponseEntity<String>login(@RequestBody LoginDTO loginDTO, HttpServletRequest request, HttpServletResponse response){
        String  token=authService.login(loginDTO);

        Cookie cookie=new Cookie("token",token);
        //always set cookie in http so anyone can not access this  only found with this http method
        //so no other attacker can access website and access this token and can access http not javascript
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return  ResponseEntity.ok(token);
    }
}
