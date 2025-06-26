package com.newzet.api.fcm.business.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.newzet.api.fcm.business.batch.FcmBatchProducer;
import com.newzet.api.fcm.business.repository.FcmTokenRepository;
import com.newzet.api.fcm.domain.FcmNotification;
import com.newzet.api.fcm.domain.FcmToken;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmTokenService {

	private final FcmTokenRepository fcmTokenRepository;
	private final FcmBatchProducer batchProducer;

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

	public void sendFcmWhenMailReceivedBatch(UUID userId, String fromName, String title) {
		List<FcmToken> fcmTokens = fcmTokenRepository.findAllByUserId(userId);
		for (FcmToken fcmToken : fcmTokens) {
			FcmNotification fcmNotification = FcmNotification.create(userId, fcmToken.value(),
				fromName, title, null);
			if (!fcmNotification.isValid()) {
				log.warn("Invalid FCM notification, skipping: userId={}, token={}",
					fcmNotification.getUserId(), fcmNotification.getToken());
				return;
			}
			batchProducer.addToBatch(fcmNotification);
		}
	}
}
