package com.newzet.api.fcm.business.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.newzet.api.fcm.business.batch.FcmBatchProducer;
import com.newzet.api.fcm.business.repository.FcmTokenRepository;
import com.newzet.api.fcm.domain.FcmNotification;
import com.newzet.api.fcm.domain.FcmToken;

@ExtendWith(MockitoExtension.class)
class FcmTokenServiceTest {

	@Mock
	private FcmTokenRepository fcmTokenRepository;

	@Mock
	private FcmBatchProducer batchProducer;

	@InjectMocks
	private FcmTokenService fcmTokenService;

	private UUID testUserId;
	private String testFcmTokenValue;
	private FcmToken testFcmToken;

	@BeforeEach
	void setUp() {
		testUserId = UUID.randomUUID();
		testFcmTokenValue = "test-fcm-token-value";
		testFcmToken = new FcmToken(UUID.randomUUID(), LocalDateTime.now(), testUserId,
			testFcmTokenValue);
	}

	@Test
	void upsertFcmToken_WhenTokenExists_ThenUpdateUserId() {
		// Given
		UUID existingUserId = UUID.randomUUID();
		FcmToken existingToken = new FcmToken(UUID.randomUUID(), LocalDateTime.now(),
			existingUserId, testFcmTokenValue);
		FcmToken updatedToken = existingToken.changeUserId(testUserId);

		when(fcmTokenRepository.findIfExistByValue(testFcmTokenValue)).thenReturn(
			Optional.of(existingToken));
		when(fcmTokenRepository.save(any(FcmToken.class))).thenReturn(updatedToken);

		// When
		FcmToken result = fcmTokenService.upsertFcmToken(testUserId, testFcmTokenValue);

		// Then
		assertThat(result.userId()).isEqualTo(testUserId);
		assertThat(result.value()).isEqualTo(testFcmTokenValue);
		verify(fcmTokenRepository).save(any(FcmToken.class));
	}

	@Test
	void upsertFcmToken_WhenTokenNotExists_ThenCreateNew() {
		// Given
		when(fcmTokenRepository.findIfExistByValue(testFcmTokenValue)).thenReturn(Optional.empty());
		when(fcmTokenRepository.save(any(FcmToken.class))).thenReturn(testFcmToken);

		// When
		FcmToken result = fcmTokenService.upsertFcmToken(testUserId, testFcmTokenValue);

		// Then
		assertThat(result.userId()).isEqualTo(testUserId);
		assertThat(result.value()).isEqualTo(testFcmTokenValue);
		verify(fcmTokenRepository).save(any(FcmToken.class));
	}

	@Test
	void deleteFcmToken_WhenCalled_ThenDeleteToken() {
		// Given
		when(fcmTokenRepository.findByUserIdAndValue(testUserId, testFcmTokenValue)).thenReturn(
			testFcmToken);
		when(fcmTokenRepository.deleteFcmToken(testFcmToken)).thenReturn(true);

		// When
		fcmTokenService.deleteFcmToken(testUserId, testFcmTokenValue);

		// Then
		verify(fcmTokenRepository).findByUserIdAndValue(testUserId, testFcmTokenValue);
		verify(fcmTokenRepository).deleteFcmToken(testFcmToken);
	}

	@Test
	void sendFcmWhenMailReceivedBatch_WhenUserHasTokens_ThenSendNotifications() {
		// Given
		String fromName = "Newsletter";
		String title = "New Article";

		FcmToken token1 = new FcmToken(UUID.randomUUID(), LocalDateTime.now(), testUserId,
			"token1");
		FcmToken token2 = new FcmToken(UUID.randomUUID(), LocalDateTime.now(), testUserId,
			"token2");
		List<FcmToken> tokens = List.of(token1, token2);

		when(fcmTokenRepository.findAllByUserId(testUserId)).thenReturn(tokens);

		// When
		fcmTokenService.sendFcmWhenMailReceivedBatch(testUserId, fromName, title);

		// Then
		verify(fcmTokenRepository).findAllByUserId(testUserId);
		verify(batchProducer, times(2)).addToBatch(any(FcmNotification.class));
	}

	@Test
	void sendFcmWhenMailReceivedBatch_WhenUserHasNoTokens_ThenNoNotificationsSent() {
		// Given
		String fromName = "Newsletter";
		String title = "New Article";
		when(fcmTokenRepository.findAllByUserId(testUserId)).thenReturn(List.of());

		// When
		fcmTokenService.sendFcmWhenMailReceivedBatch(testUserId, fromName, title);

		// Then
		verify(fcmTokenRepository).findAllByUserId(testUserId);
		verify(batchProducer, never()).addToBatch(any(FcmNotification.class));
	}
}
