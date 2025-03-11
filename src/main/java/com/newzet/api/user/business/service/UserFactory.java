package com.newzet.api.user.business.service;

import org.springframework.stereotype.Service;

import com.newzet.api.user.domain.ActiveUser;
import com.newzet.api.user.domain.User;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Service
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserFactory {

	public static User create(Long id, String email, String status) {
		return ActiveUser.create(id, email);
	}
}
