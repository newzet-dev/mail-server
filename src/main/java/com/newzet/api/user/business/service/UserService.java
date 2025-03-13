package com.newzet.api.user.business.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newzet.api.user.business.dto.UserEntityDto;
import com.newzet.api.user.domain.User;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	public User getUserByEmail(String email) {
		UserEntityDto userEntityDto = userRepository.getByEmail(email);
		return UserFactory.create(userEntityDto.getId(), userEntityDto.getEmail(),
			userEntityDto.getStatus());
	}
}
