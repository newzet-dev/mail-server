package com.newzet.api.fcm.orchestrator;

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

	public void sendFcmWhenMailReceivedBatch(UUID userId, String fromName, String title) {
		fcmSenderService.sendFcmWhenMailReceivedBatch(userId, fromName, title);
	}

	public void sendFcmNotBatch(UUID userId, String fromName, String title) {
		fcmSenderService.sendFcmNotBatch(userId, fromName, title);
	}

	public void send(FcmNotification fcmNotification) { // Consumer 이외에서 사용 금지
		fcmSenderService.send(fcmNotification);
	}
}
