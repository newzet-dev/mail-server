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

	public Userinfo getUserinfoById(UUID id) {
		return userinfoRepository.getUserinfoById(id);
	}

	public void updateUserEmailAndNickname(UUID id, String email, String nickname) {
		Userinfo userinfo = userinfoRepository.getUserinfoById(id);
		userinfo.changeEmail(email);
		userinfo.changeNickname(nickname);
		userinfoRepository.save(userinfo);
	}
}
