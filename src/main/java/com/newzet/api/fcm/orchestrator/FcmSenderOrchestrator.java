package com.newzet.api.fcm.orchestrator;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.newzet.api.fcm.business.service.FcmSenderService;
import com.newzet.api.fcm.domain.FcmNotification;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class FcmSenderOrchestrator {

	private final FcmSenderService fcmSenderService;

	public void sendFcmWhenMailReceivedBatch(UUID userId, UUID articleId,
		LocalDateTime articleCreatedAt, String articleTitle, String newsletterName) {
		fcmSenderService.sendFcmWhenMailReceivedBatch(userId, articleId, articleCreatedAt,
			articleTitle, newsletterName);
	}

	public void sendFcmNotBatch(UUID userId, UUID articleId,
		LocalDateTime articleCreatedAt, String articleTitle, String newsletterName) {
		fcmSenderService.sendFcmNotBatch(userId, articleId, articleCreatedAt,
			articleTitle, newsletterName);
	}

	public void send(FcmNotification fcmNotification) { // Consumer 이외에서 사용 금지
		fcmSenderService.send(fcmNotification);
	}
}
