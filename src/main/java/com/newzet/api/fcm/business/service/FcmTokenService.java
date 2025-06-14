package com.newzet.api.fcm.business.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.newzet.api.fcm.business.repository.FcmTokenRepository;
import com.newzet.api.fcm.domain.FcmToken;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FcmTokenService {

	private final FcmTokenRepository fcmTokenRepository;
    
	public FcmToken upsertFcmToken(UUID userId, String value) {
		FcmToken fcmToken = fcmTokenRepository.findIfExistByValue(value)
			.map(existingToken -> existingToken.changeUserId(userId))
			.orElseGet(() -> FcmToken.create(userId, value));
		return fcmTokenRepository.save(fcmToken);
	}

	public void deleteFcmToken(UUID userId, String value) {
		FcmToken fcmToken = fcmTokenRepository.findByUserIdAndValue(userId, value);
		fcmTokenRepository.deleteFcmToken(fcmToken);
	}
}
