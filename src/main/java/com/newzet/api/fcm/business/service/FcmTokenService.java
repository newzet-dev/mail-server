package com.newzet.api.fcm.business.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newzet.api.fcm.business.FcmTokenRepository;
import com.newzet.api.fcm.domain.FcmToken;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmTokenService {

	private final FcmTokenRepository fcmTokenRepository;

	@Transactional
	public FcmToken createFcmToken(UUID userId, String value) {
		return fcmTokenRepository.createIfAbsent(userId, value).toDomain();
	}

	@Transactional
	public void deleteFcmToken(UUID userId, String value) {
		boolean deleted = fcmTokenRepository.deleteFcmToken(userId, value);
		if (!deleted) {
			log.warn("비정상 흐름: 존재하지 않는 FCM 토큰 삭제 시도. userId: {}, token: {}", userId, value);
		}
	}
}
