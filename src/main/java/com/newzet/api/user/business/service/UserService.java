package com.newzet.api.user.business.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newzet.api.auth.business.dto.JwtResponse;
import com.newzet.api.user.business.dto.SignupRequest;
import com.newzet.api.user.business.dto.UniqueMailResponse;
import com.newzet.api.user.business.dto.UserEntityDto;
import com.newzet.api.user.domain.User;
import com.newzet.api.user.domain.UserStatus;
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

		UserEntityDto userEntityDto = optionalUser.get();
		User userDomain = userEntityDto.toDomain();

		if (userDomain.isWithdrawn()) {
			return UniqueMailResponse.ofWithDrawn();
		} else if (userDomain.isInactive()) {
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

		UserEntityDto userEntityDto = userRepository.save(request.email(), request.nickname(),
			UserStatus.ACTIVE.name());
		User userDomain = userEntityDto.toDomain();

		//TODO: Oauth 비즈니스 로직 도입

		return null;
	}
}
