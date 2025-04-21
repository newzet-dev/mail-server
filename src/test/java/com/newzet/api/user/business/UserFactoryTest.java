package com.newzet.api.user.business;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.newzet.api.user.business.service.UserFactory;
import com.newzet.api.user.domain.UserDomain;
import com.newzet.api.user.domain.User;

public class UserFactoryTest {

	@Test
	public void create_whenStatusIsActive_returnActiveUser() {
		//Given
		UUID id = UUID.randomUUID();
		String email = "test@example.com";
		String status = "ACTIVE";
		String nickname = "testName";

		//When
		User user = UserFactory.create(id, email, nickname, status);

		//Then
		assertInstanceOf(UserDomain.class, user);
		assertEquals(id, user.getId());
		assertEquals(email, user.getEmail());
	}
}
