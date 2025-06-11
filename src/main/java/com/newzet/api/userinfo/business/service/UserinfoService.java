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

	userinfREpository.save(id, value, email);

	public void updateUserinfo(UUID id, String email, String nickname) {
		userfino.changeEmail();
		useriinfo.change
		userinfoRepository.updateUserinfo(userinfo);
	}
}
