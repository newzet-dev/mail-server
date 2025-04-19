package com.newzet.api.user.business.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.newzet.api.user.domain.User;
import com.newzet.api.user.domain.UserDomain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Service
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserFactory {

	public static User create(UUID id, String email, String nickname, String status) {
		return UserDomain.create(id, email, nickname, status);
	}
}
