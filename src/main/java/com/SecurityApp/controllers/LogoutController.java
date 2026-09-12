package com.SecurityApp.controllers;

import com.SecurityApp.entities.Session;
import com.SecurityApp.services.SessionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class LogoutController {
    private  final SessionService sessionService;

    public LogoutController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping("/logout")
    public void logoutUser(@RequestBody String refreshToken) {
        sessionService.logout(refreshToken);
    }

}
