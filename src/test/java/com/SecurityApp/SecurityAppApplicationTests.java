package com.SecurityApp;

import com.SecurityApp.entities.User;
import com.SecurityApp.services.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class SecurityAppApplicationTests {

	@Autowired
	private JwtService jwtService;

	@Autowired
	private com.SecurityApp.services.SessionService sessionService;

	@Autowired
	private com.SecurityApp.respositories.UserRepository userRepository;

	@Test
	void contextLoads() {

		User user = User.builder()
				.id(4L)
				.email("nituspj032001@gmail.com")
				.name("Nitu")
				.password("Nitu@123")
				.roles(List.of(com.SecurityApp.entities.enums.Role.USER)) // ✅ Added
				.build();


//		String token = jwtService.generateToken(user);
		String accessToken= jwtService.generateAccessToken(user);
		String refreshToken= jwtService.generateRefreshToken(user);

		System.out.println("access: " + accessToken);

		Long id = jwtService.getUserIdFromToken(accessToken);
		System.out.println("id: " + id);
	}

	@Autowired
	private com.SecurityApp.respositories.SessionRepository sessionRepository;

	@Test
	void testSessionLimit() throws InterruptedException {
		User user = userRepository.findAll().get(0);

		// Verify 2 sessions remain
		List<com.SecurityApp.entities.Session> sessions = sessionRepository.findByUser(user);
		System.out.println("Current active sessions in DB: " + sessions.size());
	}
}