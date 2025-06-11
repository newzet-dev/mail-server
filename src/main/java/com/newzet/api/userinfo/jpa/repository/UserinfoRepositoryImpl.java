package com.newzet.api.userinfo.jpa.repository;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.newzet.api.userinfo.business.repository.UserinfoRepository;
import com.newzet.api.userinfo.domain.Userinfo;
import com.newzet.api.userinfo.exception.NoUserinfoException;
import com.newzet.api.userinfo.jpa.entity.UserinfoEntity;
import com.newzet.api.userinfo.jpa.entity.UserinfoEntityRole;
import com.newzet.api.userinfo.jpa.mapper.UserinfoEntityMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserinfoRepositoryImpl implements UserinfoRepository {

	private final UserinfoJpaRepository userinfoJpaRepository;

	@Override
	public Userinfo getUserinfoById(UUID id) {
		return userinfoJpaRepository.findById(id)
			.map(UserinfoEntityMapper::toDomain)
			.orElseThrow(() -> new NoUserinfoException("사용자 정보를 찾을 수 없습니다."));
	}

	@Override
	public void updateEmailAndNickname(UUID id, String email, String nickname) {
		UserinfoEntity userinfoEntity = new UserinfoEntity(null, email, nickname, UserinfoEntityRole.MEMBER,
			LocalDateTime.now(), null);
		userinfoJpaRepository.save(userinfoEntity);
	}
}
