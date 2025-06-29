package com.newzet.api.welcome.business.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.newzet.api.welcome.business.repository.UserinfoRepository;
import com.newzet.api.welcome.domain.Userinfo;

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
		Userinfo userinfo = userinfoRepository.findUserinfoById(userId);
		return userinfo.getEmail() != null;
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
