package com.SecurityApp.services;

import com.SecurityApp.dto.LoginDTO;
import com.SecurityApp.dto.LoginResponseDTO;
import com.SecurityApp.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private  final AuthenticationManager authenticationManager;
    private  final  JwtService jwtService;
    private  final  UserService userService;
    private  final  SessionService sessionService;
    public LoginResponseDTO login(LoginDTO loginDTO){
        Authentication authentication= authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(),loginDTO.getPassword())
        );
        User user=(User)authentication.getPrincipal();
//        return jwtService.generateToken(user);
        String accessToken= jwtService.generateAccessToken(user);
        String refreshToken= jwtService.generateRefreshToken(user);

        //generate session
        sessionService.generateNewSession(user,refreshToken);


      return  new LoginResponseDTO(user.getId(),accessToken,refreshToken);
    }

    //refresh token
    public LoginResponseDTO refreshToken(String refreshToken) {
      Long userId=jwtService.getUserIdFromToken(refreshToken);

      //check refresh token is valid or not
        sessionService.validateSession(refreshToken);
      User user= userService.getUserById(userId);
       String accessToken= jwtService.generateAccessToken(user);

       //if user try to refresh token to get new Access token
       return  new LoginResponseDTO(user.getId(),accessToken,refreshToken);

    }
}
