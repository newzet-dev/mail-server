package com.newzet.api.fcm.business.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.newzet.api.fcm.business.batch.FcmBatchProducer;
import com.newzet.api.fcm.business.repository.FcmTokenRepository;
import com.newzet.api.fcm.domain.FcmNotification;
import com.newzet.api.fcm.domain.FcmToken;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmSenderService {

	private final FirebaseMessaging firebaseMessaging;
	private final FcmTokenRepository fcmTokenRepository;
	private final FcmBatchProducer batchProducer;

	public void sendFcmWhenMailReceivedBatch(UUID userId, String fromName, String title) {
		List<FcmToken> fcmTokens = fcmTokenRepository.findAllByUserId(userId);
		for (FcmToken fcmToken : fcmTokens) {
			FcmNotification fcmNotification = FcmNotification.create(userId, fcmToken.getValue(),
				fromName, title, null);
			if (!fcmNotification.isValid()) {
				log.warn("Invalid FCM notification, skipping: userId={}, token={}",
					fcmNotification.getUserId(), fcmNotification.getToken());
				return;
			}
			batchProducer.addToBatch(fcmNotification);
		}
	}

	public void sendFcmNotBatch(UUID userId, String fromName, String title) {
		List<FcmToken> fcmTokens = fcmTokenRepository.findAllByUserId(userId);
		for (FcmToken fcmToken : fcmTokens) {
			FcmNotification fcmNotification = FcmNotification.create(userId, fcmToken.getValue(),
				fromName, title, null);
			if (!fcmNotification.isValid()) {
				log.warn("Invalid FCM notification, skipping: userId={}, token={}",
					fcmNotification.getUserId(), fcmNotification.getToken());
				return;
			}
			send(fcmNotification);
		}
	}

	public void send(FcmNotification fcmNotification) {
		try {
			Message message = buildFcmMessage(fcmNotification);
			firebaseMessaging.send(message);
		} catch (Exception e) {
			handleSendFailure(fcmNotification, e);
		}
	}

	private Message buildFcmMessage(FcmNotification fcmNotification) {
		return Message.builder()
			.setToken(fcmNotification.getToken())
			.setNotification(Notification.builder()
				.setTitle(fcmNotification.getTitle())
				.setBody(fcmNotification.getBody())
				.build())
			.putData("data", fcmNotification.getData() != null ? fcmNotification.getData() : "")
			.build();
	}

	private void handleSendFailure(FcmNotification fcmNotification, Exception e) {
		if (isInvalidTokenError(e)) {
			try {
				FcmToken fcmToken = fcmTokenRepository.findByUserIdAndValue(
					fcmNotification.getUserId(), fcmNotification.getToken());
				fcmTokenRepository.deleteFcmToken(fcmToken);
				log.info("Deleted invalid FCM token: userId={}, token={}",
					fcmNotification.getUserId(), fcmNotification.getToken());
			} catch (Exception deleteError) {
				log.error("Failed to delete invalid FCM token: {}", deleteError.getMessage(),
					deleteError);
			}
		}
	}

	private boolean isInvalidTokenError(Exception e) {
		String errorMessage = e.getMessage().toLowerCase();
		return errorMessage.contains("invalid registration token") ||
			errorMessage.contains("registration token not registered") ||
			errorMessage.contains("invalid argument") ||
			errorMessage.contains("requested entity was not found");
	}
}
