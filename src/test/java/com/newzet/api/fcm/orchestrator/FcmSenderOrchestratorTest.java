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

import com.newzet.api.fcm.business.service.FcmSenderService;
import com.newzet.api.fcm.domain.FcmToken;

@ExtendWith(MockitoExtension.class)
public class FcmSenderOrchestratorTest {

	@Mock
	private FcmSenderService fcmSenderService;

	@InjectMocks
	private FcmSenderOrchestrator fcmSenderOrchestrator;

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
	void sendFcmWhenMailReceivedBatch_WhenCalled_ThenDelegateToService() {
		// Given
		UUID articleId = UUID.randomUUID();
		LocalDateTime articleCreatedAt = LocalDateTime.now();
		String articleTitle = "New Article";
		String newsletterName = "Newsletter";
		doNothing().when(fcmSenderService)
			.sendFcmWhenMailReceivedBatch(testUserId, articleId, articleCreatedAt, articleTitle,
				newsletterName);

		// When
		fcmSenderOrchestrator.sendFcmWhenMailReceivedBatch(testUserId, articleId, articleCreatedAt,
			articleTitle, newsletterName);

		// Then
		verify(fcmSenderService).sendFcmWhenMailReceivedBatch(testUserId, articleId,
			articleCreatedAt, articleTitle, newsletterName);
	}
}
