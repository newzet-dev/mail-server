package com.newzet.api.userinfo.business.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.newzet.api.userinfo.business.repository.UserinfoRepository;
import com.newzet.api.userinfo.domain.Userinfo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserinfoService {

	private final UserinfoRepository userinfoRepository;

	public Userinfo findUserinfoById(UUID id) {
		return userinfoRepository.findUserinfoById(id);
	}

	public void updateUserEmailAndNickname(UUID id, String email, String nickname) {
		Userinfo userinfo = userinfoRepository.findUserinfoById(id);
		userinfo.changeEmail(email);
		userinfo.changeNickname(nickname);
		userinfoRepository.save(userinfo);
	}

	public boolean isUniqueEmailInUserinfo(String email) {
		return userinfoRepository.findOptionalUserinfoByEmail(email).isEmpty();
	}

	public boolean isInitialized(UUID userId) {
		return userinfoRepository.findOptionalUserinfoById(userId)
			.map(Userinfo::getEmail)
			.isPresent();
	}

	public Userinfo findUserinfoByEmail(String email) {
		return userinfoRepository.findUserinfoByEmail(email);
	}

	public void deleteUserinfoById(UUID userId) {
		Userinfo userinfo = userinfoRepository.findUserinfoById(userId);
		userinfo.delete();
		userinfoRepository.save(userinfo);
	}
}
