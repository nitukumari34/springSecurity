package com.SecurityApp.controllers;

import com.SecurityApp.dto.LoginDTO;
import com.SecurityApp.dto.LoginResponseDTO;
import com.SecurityApp.dto.SignupDTO;
import com.SecurityApp.dto.UserDTO;
import com.SecurityApp.services.AuthService;
import com.SecurityApp.services.UserService;
import jakarta.persistence.GeneratedValue;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private  final UserService userService;
    private  final AuthService authService;

    @Value("${deploy.env}")
    private  String deployEnv;

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
    public  ResponseEntity<LoginResponseDTO>login(@RequestBody LoginDTO loginDTO, HttpServletRequest request, HttpServletResponse response){
//        String  token=authService.login(loginDTO);
        LoginResponseDTO loginResponseDTO=authService.login(loginDTO);

//        Cookie cookie=new Cookie("token",token);
        Cookie cookie=new Cookie("refreshToken",loginResponseDTO.getRefreshToken());
        //always set cookie in http so anyone can not access this  only found with this http method
        //so no other attacker can access website and access this token and can access http not javascript
        cookie.setHttpOnly(true);
        cookie.setSecure("production".equals(deployEnv));//only server can access this token thus no one can hold in the middle
        // to get this cookie  bcs they are httponly and httpSecure do secure in prod not in development mode
        response.addCookie(cookie);
        return  ResponseEntity.ok(loginResponseDTO);
    }

    @PostMapping("/refresh")
    public  ResponseEntity<LoginResponseDTO>refresh(HttpServletRequest request){
//       Cookie[]cookies=request.getCookies();
        String refreshToken=Arrays.stream(request.getCookies()).
                filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(()->new AuthenticationServiceException("Refresh token not found inside  the cookie"));
        LoginResponseDTO loginResponseDTO=  authService.refreshToken(refreshToken);
        return  ResponseEntity.ok(loginResponseDTO);
    }

}
