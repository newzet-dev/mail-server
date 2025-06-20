package com.newzet.api.fcm.jpa.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.newzet.api.fcm.business.repository.FcmTokenRepository;
import com.newzet.api.fcm.domain.FcmToken;
import com.newzet.api.fcm.exception.NoFcmTokenException;
import com.newzet.api.fcm.jpa.entity.FcmTokenEntity;
import com.newzet.api.fcm.jpa.mapper.FcmTokenMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FcmTokenRepositoryImpl implements FcmTokenRepository {

	private final FcmTokenJpaRepository fcmTokenJpaRepository;

	@Override
	public Optional<FcmToken> findIfExistByValue(String value) {
		return fcmTokenJpaRepository.findByFcmToken(value)
			.map(FcmTokenMapper::toDomain);
	}

	@Override
	public boolean deleteFcmToken(FcmToken fcmToken) {
		fcmTokenJpaRepository.delete(FcmTokenMapper.toEntity(fcmToken));
		return true;
	}

	@Override
	public FcmToken save(FcmToken fcmToken) {
		FcmTokenEntity savedFcmTokenEntity = fcmTokenJpaRepository.save(
			FcmTokenMapper.toEntity(fcmToken));
		return FcmTokenMapper.toDomain(savedFcmTokenEntity);
	}

	@Override
	public FcmToken findByUserIdAndValue(UUID userId, String value) {
		return fcmTokenJpaRepository.findByUserIdAndFcmToken(userId, value)
			.map(FcmTokenMapper::toDomain)
			.orElseThrow(
				() -> new NoFcmTokenException(
					"FCM Token이 존재하지 않습니다. id = " + userId + ", value = " + value));
	}

	@Override
	public List<FcmToken> findAllByUserId(UUID userId) {
		return fcmTokenJpaRepository.findAllByUserId(userId)
			.stream().map(FcmTokenMapper::toDomain)
			.toList();
	}
}
