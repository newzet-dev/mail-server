package com.newzet.api.user.business.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newzet.api.user.business.dto.UniqueMailResponse;
import com.newzet.api.user.business.dto.UserEntityDto;
import com.newzet.api.user.domain.User;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	@Transactional(readOnly = true)
	public UUID getUserIdByEmail(String email) {
		return userRepository.getByEmail(email).getId();
	}

	public UniqueMailResponse checkEmailUniqueness(String email) {
		Optional<UserEntityDto> optionalUser = userRepository.findOptionalByEmail(email);

		if (optionalUser.isEmpty()) {
			return UniqueMailResponse.ofUnique();
		}

		UserEntityDto userEntityDto = optionalUser.get();
		User user = userEntityDto.toDomain();

		if (user.isWithdrawn()) {
			return UniqueMailResponse.ofWithDrawn();
		} else if (user.isInactive()) {
			return UniqueMailResponse.ofInActive();
		} else {
			return UniqueMailResponse.ofDuplicate();
		}
	}
}
