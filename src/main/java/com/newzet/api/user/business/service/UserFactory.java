package com.newzet.api.user.business.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.newzet.api.user.domain.ActiveUser;
import com.newzet.api.user.domain.User;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Service
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserFactory {

	public static User create(UUID id, String email, String status) {
		return ActiveUser.create(id, email);
	}
}
