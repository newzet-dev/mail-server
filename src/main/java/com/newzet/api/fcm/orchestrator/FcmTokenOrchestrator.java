package com.newzet.api.fcm.orchestrator;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.newzet.api.fcm.business.service.FcmTokenService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class FcmTokenOrchestrator {

	private final FcmTokenService fcmTokenService;

	public void upsertFcmToken(UUID userId, String value) {
		fcmTokenService.upsertFcmToken(userId, value);
	}

	public void deleteFcmToken(UUID userId, String value) {
		fcmTokenService.deleteFcmToken(userId, value);
	}
}
