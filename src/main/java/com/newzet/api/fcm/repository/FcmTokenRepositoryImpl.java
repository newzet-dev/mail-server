package com.newzet.api.fcm.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.newzet.api.fcm.business.FcmTokenRepository;
import com.newzet.api.fcm.business.dto.FcmTokenEntityResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FcmTokenRepositoryImpl implements FcmTokenRepository {

	private final FcmTokenJpaRepository fcmTokenJpaRepository;

	@Override
	public FcmTokenEntityResponse createIfAbsent(UUID userId, String value) {
		return fcmTokenJpaRepository.findByUserIdAndFcmToken(userId, value)
			.map(FcmTokenEntity::toResponseDto)
			.orElseGet(() -> {
				FcmTokenEntity fcmToken = FcmTokenEntity.create(userId, value);
				return fcmTokenJpaRepository.save(fcmToken).toResponseDto();
			});
	}

	@Override
	public Boolean deleteFcmToken(UUID userId, String value) {
		return fcmTokenJpaRepository.findByUserIdAndFcmToken(userId, value)
			.map(entity -> {
				fcmTokenJpaRepository.delete(entity);
				return true;
			})
			.orElse(false);
	}
}
