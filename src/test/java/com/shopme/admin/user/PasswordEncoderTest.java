package com.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderTest {
	
	@Test
	public void testEncoderPassword() {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String pasword = "123123123";
		String encodePassword = passwordEncoder.encode(pasword);
		System.out.println(encodePassword);
		boolean matches = passwordEncoder.matches(pasword, encodePassword);
		assertThat(matches).isTrue();
	}

}
