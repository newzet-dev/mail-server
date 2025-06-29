package com.newzet.api.welcome.jpa.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.newzet.api.welcome.business.repository.UserinfoRepository;
import com.newzet.api.welcome.domain.Userinfo;
import com.newzet.api.welcome.exception.NoUserinfoException;
import com.newzet.api.welcome.jpa.entity.UserinfoEntity;
import com.newzet.api.welcome.jpa.mapper.UserinfoEntityMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserinfoRepositoryImpl implements UserinfoRepository {

	private final UserinfoJpaRepository userinfoJpaRepository;

	@Override
	public Userinfo findUserinfoById(UUID id) {
		return userinfoJpaRepository.findById(id)
			.map(UserinfoEntityMapper::toDomain)
			.orElseThrow(() -> new NoUserinfoException("사용자 정보를 찾을 수 없습니다."));
	}

	@Override
	public Userinfo save(Userinfo userinfo) {
		UserinfoEntity savedUserinfoEntity = userinfoJpaRepository.save(UserinfoEntityMapper.toEntity(userinfo));
		return UserinfoEntityMapper.toDomain(savedUserinfoEntity);
	}

	@Override
	public Optional<Userinfo> findOptionalUserinfoByEmail(String email) {
		return userinfoJpaRepository.findOptionalByEmail(email)
			.map(UserinfoEntityMapper::toDomain);
	}

	@Override
	public Userinfo findUserinfoByEmail(String email) {
		return userinfoJpaRepository.findOptionalByEmail(email)
			.map(UserinfoEntityMapper::toDomain)
			.orElseThrow(() -> new NoUserinfoException("사용자 정보를 찾을 수 없습니다."));
	}
}
