package com.newzet.api.fcm.orchestrator;

import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.newzet.api.fcm.business.service.FcmTokenService;
import com.newzet.api.fcm.domain.FcmToken;

@ExtendWith(MockitoExtension.class)
class FcmTokenOrchestratorTest {

	@Mock
	private FcmTokenService fcmTokenService;

	@InjectMocks
	private FcmTokenOrchestrator fcmTokenOrchestrator;

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
	void upsertFcmToken_WhenCalled_ThenDelegateToService() {
		// Given
		when(fcmTokenService.upsertFcmToken(testUserId, testFcmTokenValue)).thenReturn(
			testFcmToken);

		// When
		fcmTokenOrchestrator.upsertFcmToken(testUserId, testFcmTokenValue);

		// Then
		verify(fcmTokenService).upsertFcmToken(testUserId, testFcmTokenValue);
	}

	@Test
	void deleteFcmToken_WhenCalled_ThenDelegateToService() {
		// Given
		doNothing().when(fcmTokenService).deleteFcmToken(testUserId, testFcmTokenValue);

		// When
		fcmTokenOrchestrator.deleteFcmToken(testUserId, testFcmTokenValue);

		// Then
		verify(fcmTokenService).deleteFcmToken(testUserId, testFcmTokenValue);
	}

	@Test
	void sendFcmWhenMailReceivedBatch_WhenCalled_ThenDelegateToService() {
		// Given
		String fromName = "Newsletter";
		String title = "New Article";
		doNothing().when(fcmTokenService).sendFcmWhenMailReceivedBatch(testUserId, fromName, title);

		// When
		fcmTokenOrchestrator.sendFcmWhenMailReceivedBatch(testUserId, fromName, title);

		// Then
		verify(fcmTokenService).sendFcmWhenMailReceivedBatch(testUserId, fromName, title);
	}
}
