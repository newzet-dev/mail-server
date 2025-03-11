package com.newzet.api.user.business;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.newzet.api.user.business.service.UserFactory;
import com.newzet.api.user.domain.ActiveUser;
import com.newzet.api.user.domain.User;

public class UserFactoryTest {

	@Test
	public void create_whenStatusIsActive_returnActiveUser() {
		//Given
		Long id = 1L;
		String email = "test@example.com";
		String status = "ACTIVE";

		//When
		User user = UserFactory.create(id, email, status);

		//Then
		assertInstanceOf(ActiveUser.class, user);
		assertEquals(id, user.getId());
	}
}
