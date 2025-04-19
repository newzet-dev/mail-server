package com.newzet.api.user.business.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newzet.api.auth.business.dto.JwtResponse;
import com.newzet.api.user.business.dto.SignupRequest;
import com.newzet.api.user.business.dto.UniqueMailResponse;
import com.newzet.api.user.business.dto.UpdateNicknameRequest;
import com.newzet.api.user.business.dto.UserEntityDto;
import com.newzet.api.user.domain.User;
import com.newzet.api.user.exception.UserEmailDuplicateException;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	public User getUserByEmail(String email) {
		UserEntityDto userEntityDto = userRepository.getByEmail(email);
		return userEntityDto.toDomain();
	}

	public UniqueMailResponse checkEmailUniqueness(String email) {
		Optional<UserEntityDto> optionalUser = userRepository.findOptionalByEmail(email);

		if (optionalUser.isEmpty()) {
			return UniqueMailResponse.ofUnique();
		}

		UserEntityDto user = optionalUser.get();

		if (user.isWithdrawn()) {
			return UniqueMailResponse.ofWithDrawn();
		} else if (user.isInactive()) {
			return UniqueMailResponse.ofInActive();
		} else {
			return UniqueMailResponse.ofDuplicate();
		}
	}

	@Transactional
	public JwtResponse signUp(SignupRequest request) {
		UniqueMailResponse uniqueCheck = checkEmailUniqueness(request.email());
		if (!uniqueCheck.isUnique()) {
			throw new UserEmailDuplicateException(uniqueCheck.message());
		}

		UserEntityDto newUser = userRepository.save(request.email(), request.nickname(),
			"ACTIVE");
		User user = newUser.toDomain();

		//TODO: Oauth 비즈니스 로직 도입

		return null;
	}

	@Transactional
	public void updateNickname(UUID userId, UpdateNicknameRequest request) {
		UserEntityDto userEntityDto = userRepository.getById(userId);
		UserEntityDto updatedUserEntityDto = UserEntityDto.create(
			userEntityDto.getId(),
			userEntityDto.getEmail(),
			request.nickname(),
			userEntityDto.getStatus()
		);

		userRepository.update(updatedUserEntityDto);
	}
}
