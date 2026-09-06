package com.SecurityApp;

import com.SecurityApp.entities.User;
import com.SecurityApp.services.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SecurityAppApplicationTests {

	@Autowired
	private JwtService jwtService;

	@Test
	void contextLoads() {

		User user = User.builder()
				.id(4L)
				.email("nituspj032001@gmail.com")
				.name("Nitu")
				.password("Nitu@123")
				.build();

		String token = jwtService.generateToken(user);

		System.out.println("token: " + token);

		Long id = jwtService.getUserIdFromToken(token);

		System.out.println("id: " + id);
	}
}