package com.newzet.api.fcm.business.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.newzet.api.fcm.business.batch.FcmBatchProducer;
import com.newzet.api.fcm.business.repository.FcmTokenRepository;
import com.newzet.api.fcm.domain.FcmNotification;
import com.newzet.api.fcm.domain.FcmToken;

@ExtendWith(MockitoExtension.class)
public class FcmSenderServiceTest {

	@Mock
	private FcmBatchProducer batchProducer;

	@Mock
	private FcmTokenRepository fcmTokenRepository;

	@Mock
	private FirebaseMessaging firebaseMessaging;

	@InjectMocks
	private FcmSenderService fcmSenderService;

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
		fcmSenderService.sendFcmWhenMailReceivedBatch(testUserId, fromName, title);

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
		fcmSenderService.sendFcmWhenMailReceivedBatch(testUserId, fromName, title);

		// Then
		verify(fcmTokenRepository).findAllByUserId(testUserId);
		verify(batchProducer, never()).addToBatch(any(FcmNotification.class));
	}

	@Test
	void sendFcmWhenMailReceivedBatch_WhenTokenIsNull_ThenSkipInvalidNotification() {
		// Given
		String fromName = "Newsletter";
		String title = "New Article";

		FcmToken tokenWithNullValue = new FcmToken(UUID.randomUUID(), LocalDateTime.now(),
			testUserId,
			null);
		List<FcmToken> tokens = List.of(tokenWithNullValue);

		when(fcmTokenRepository.findAllByUserId(testUserId)).thenReturn(tokens);

		// When
		fcmSenderService.sendFcmWhenMailReceivedBatch(testUserId, fromName, title);

		// Then
		verify(fcmTokenRepository).findAllByUserId(testUserId);
		verify(batchProducer, never()).addToBatch(any(FcmNotification.class));
	}

	@Test
	void sendFcmWhenMailReceivedBatch_WhenTokenIsEmpty_ThenSkipInvalidNotification() {
		// Given
		String fromName = "Newsletter";
		String title = "New Article";

		FcmToken tokenWithEmptyValue = new FcmToken(UUID.randomUUID(), LocalDateTime.now(),
			testUserId,
			"");
		List<FcmToken> tokens = List.of(tokenWithEmptyValue);

		when(fcmTokenRepository.findAllByUserId(testUserId)).thenReturn(tokens);

		// When
		fcmSenderService.sendFcmWhenMailReceivedBatch(testUserId, fromName, title);

		// Then
		verify(fcmTokenRepository).findAllByUserId(testUserId);
		verify(batchProducer, never()).addToBatch(any(FcmNotification.class));
	}

	@Test
	void sendFcmWhenMailReceivedBatch_WhenMixedValidAndInvalidTokens_ThenProcessOnlyValidOnes() {
		// Given
		String fromName = "Newsletter";
		String title = "New Article";

		FcmToken validToken = new FcmToken(UUID.randomUUID(), LocalDateTime.now(), testUserId,
			"valid-token");
		FcmToken invalidToken = new FcmToken(UUID.randomUUID(), LocalDateTime.now(), testUserId,
			null);
		List<FcmToken> tokens = List.of(validToken, invalidToken);

		when(fcmTokenRepository.findAllByUserId(testUserId)).thenReturn(tokens);

		// When
		fcmSenderService.sendFcmWhenMailReceivedBatch(testUserId, fromName, title);

		// Then
		verify(fcmTokenRepository).findAllByUserId(testUserId);
		verify(batchProducer, times(1)).addToBatch(any(FcmNotification.class));
	}

	@Test
	void sendFcmWhenMailReceivedBatch_WhenInvalidTokenFirst_ThenReturnEarly() {
		// Given
		String fromName = "Newsletter";
		String title = "New Article";

		FcmToken invalidToken = new FcmToken(UUID.randomUUID(), LocalDateTime.now(), testUserId,
			null);
		FcmToken validToken = new FcmToken(UUID.randomUUID(), LocalDateTime.now(), testUserId,
			"valid-token");
		List<FcmToken> tokens = List.of(invalidToken, validToken);

		when(fcmTokenRepository.findAllByUserId(testUserId)).thenReturn(tokens);

		// When
		fcmSenderService.sendFcmWhenMailReceivedBatch(testUserId, fromName, title);

		// Then
		verify(fcmTokenRepository).findAllByUserId(testUserId);
		verify(batchProducer, never()).addToBatch(any(FcmNotification.class));
	}

	@Test
	void sendFcmNotBatch_WhenUserHasTokens_ThenSendDirectly() throws Exception {
		// Given
		String fromName = "Newsletter";
		String title = "New Article";

		FcmToken token1 = new FcmToken(UUID.randomUUID(), LocalDateTime.now(), testUserId,
			"token1");
		List<FcmToken> tokens = List.of(token1);

		when(fcmTokenRepository.findAllByUserId(testUserId)).thenReturn(tokens);
		when(firebaseMessaging.send(any(Message.class))).thenReturn("success-response");

		// When
		fcmSenderService.sendFcmNotBatch(testUserId, fromName, title);

		// Then
		verify(fcmTokenRepository).findAllByUserId(testUserId);
		verify(firebaseMessaging).send(any(Message.class));
		verify(batchProducer, never()).addToBatch(any(FcmNotification.class));
	}

	@Test
	void sendFcmNotBatch_WhenUserHasNoTokens_ThenNoNotificationsSent() throws
		FirebaseMessagingException {
		// Given
		String fromName = "Newsletter";
		String title = "New Article";
		when(fcmTokenRepository.findAllByUserId(testUserId)).thenReturn(List.of());

		// When
		fcmSenderService.sendFcmNotBatch(testUserId, fromName, title);

		// Then
		verify(fcmTokenRepository).findAllByUserId(testUserId);
		verify(firebaseMessaging, never()).send(any(Message.class));
	}

	@Test
	void sendFcmNotBatch_WhenTokenIsInvalid_ThenSkipInvalidNotification() throws
		FirebaseMessagingException {
		// Given
		String fromName = "Newsletter";
		String title = "New Article";

		FcmToken invalidToken = new FcmToken(UUID.randomUUID(), LocalDateTime.now(), testUserId,
			null);
		List<FcmToken> tokens = List.of(invalidToken);

		when(fcmTokenRepository.findAllByUserId(testUserId)).thenReturn(tokens);

		// When
		fcmSenderService.sendFcmNotBatch(testUserId, fromName, title);

		// Then
		verify(fcmTokenRepository).findAllByUserId(testUserId);
		verify(firebaseMessaging, never()).send(any(Message.class));
	}

	@Test
	void sendFcmNotBatch_WhenInvalidTokenFirst_ThenReturnEarly() throws FirebaseMessagingException {
		// Given
		String fromName = "Newsletter";
		String title = "New Article";

		FcmToken invalidToken = new FcmToken(UUID.randomUUID(), LocalDateTime.now(), testUserId,
			null);
		FcmToken validToken = new FcmToken(UUID.randomUUID(), LocalDateTime.now(), testUserId,
			"valid-token");
		List<FcmToken> tokens = List.of(invalidToken, validToken);

		when(fcmTokenRepository.findAllByUserId(testUserId)).thenReturn(tokens);

		// When
		fcmSenderService.sendFcmNotBatch(testUserId, fromName, title);

		// Then
		verify(fcmTokenRepository).findAllByUserId(testUserId);
		verify(firebaseMessaging, never()).send(any(Message.class));
	}

	@Test
	void send_WhenFcmNotificationIsValid_ThenSendSuccessfully() throws Exception {
		// Given
		FcmNotification notification = FcmNotification.create(testUserId, testFcmTokenValue,
			"Newsletter", "New Article", null);
		when(firebaseMessaging.send(any(Message.class))).thenReturn("success-response");

		// When
		fcmSenderService.send(notification);

		// Then
		verify(firebaseMessaging).send(any(Message.class));
	}

	@Test
	void send_WhenFcmNotificationWithData_ThenSendSuccessfully() throws Exception {
		// Given
		FcmNotification notification = FcmNotification.create(testUserId, testFcmTokenValue,
			"Newsletter", "New Article", "custom-data");
		when(firebaseMessaging.send(any(Message.class))).thenReturn("success-response");

		// When
		fcmSenderService.send(notification);

		// Then
		verify(firebaseMessaging).send(any(Message.class));
	}

	@Test
	void send_WhenInvalidTokenError_ThenDeleteToken() throws Exception {
		// Given
		FcmNotification notification = FcmNotification.create(testUserId, testFcmTokenValue,
			"Newsletter", "New Article", null);
		RuntimeException invalidTokenException = new RuntimeException("Invalid registration token");

		when(firebaseMessaging.send(any(Message.class))).thenThrow(invalidTokenException);
		when(fcmTokenRepository.findByUserIdAndValue(testUserId, testFcmTokenValue))
			.thenReturn(testFcmToken);

		// When
		fcmSenderService.send(notification);

		// Then
		verify(firebaseMessaging).send(any(Message.class));
		verify(fcmTokenRepository).findByUserIdAndValue(testUserId, testFcmTokenValue);
		verify(fcmTokenRepository).deleteFcmToken(testFcmToken);
	}

	@Test
	void send_WhenRegistrationTokenNotRegistered_ThenDeleteToken() throws Exception {
		// Given
		FcmNotification notification = FcmNotification.create(testUserId, testFcmTokenValue,
			"Newsletter", "New Article", null);
		RuntimeException notRegisteredError = new RuntimeException(
			"Registration token not registered");

		when(firebaseMessaging.send(any(Message.class))).thenThrow(notRegisteredError);
		when(fcmTokenRepository.findByUserIdAndValue(testUserId, testFcmTokenValue))
			.thenReturn(testFcmToken);

		// When
		fcmSenderService.send(notification);

		// Then
		verify(firebaseMessaging).send(any(Message.class));
		verify(fcmTokenRepository).deleteFcmToken(testFcmToken);
	}

	@Test
	void send_WhenInvalidArgumentError_ThenDeleteToken() throws Exception {
		// Given
		FcmNotification notification = FcmNotification.create(testUserId, testFcmTokenValue,
			"Newsletter", "New Article", null);
		RuntimeException invalidArgumentError = new RuntimeException("Invalid argument");

		when(firebaseMessaging.send(any(Message.class))).thenThrow(invalidArgumentError);
		when(fcmTokenRepository.findByUserIdAndValue(testUserId, testFcmTokenValue))
			.thenReturn(testFcmToken);

		// When
		fcmSenderService.send(notification);

		// Then
		verify(firebaseMessaging).send(any(Message.class));
		verify(fcmTokenRepository).deleteFcmToken(testFcmToken);
	}

	@Test
	void send_WhenEntityNotFoundError_ThenDeleteToken() throws Exception {
		// Given
		FcmNotification notification = FcmNotification.create(testUserId, testFcmTokenValue,
			"Newsletter", "New Article", null);
		RuntimeException entityNotFoundError = new RuntimeException(
			"Requested entity was not found");

		when(firebaseMessaging.send(any(Message.class))).thenThrow(entityNotFoundError);
		when(fcmTokenRepository.findByUserIdAndValue(testUserId, testFcmTokenValue))
			.thenReturn(testFcmToken);

		// When
		fcmSenderService.send(notification);

		// Then
		verify(firebaseMessaging).send(any(Message.class));
		verify(fcmTokenRepository).deleteFcmToken(testFcmToken);
	}

	@Test
	void send_WhenOtherException_ThenDoNotDeleteToken() throws Exception {
		// Given
		FcmNotification notification = FcmNotification.create(testUserId, testFcmTokenValue,
			"Newsletter", "New Article", null);
		RuntimeException otherException = new RuntimeException("Network error");

		when(firebaseMessaging.send(any(Message.class))).thenThrow(otherException);

		// When
		fcmSenderService.send(notification);

		// Then
		verify(firebaseMessaging).send(any(Message.class));
		verify(fcmTokenRepository, never()).findByUserIdAndValue(any(), any());
		verify(fcmTokenRepository, never()).deleteFcmToken(any());
	}

	@Test
	void send_WhenDeleteTokenFails_ThenHandleGracefully() throws Exception {
		// Given
		FcmNotification notification = FcmNotification.create(testUserId, testFcmTokenValue,
			"Newsletter", "New Article", null);
		RuntimeException invalidTokenException = new RuntimeException("Invalid registration token");
		RuntimeException deleteException = new RuntimeException("Database error");

		when(firebaseMessaging.send(any(Message.class))).thenThrow(invalidTokenException);
		when(fcmTokenRepository.findByUserIdAndValue(testUserId, testFcmTokenValue))
			.thenReturn(testFcmToken);
		doThrow(deleteException).when(fcmTokenRepository).deleteFcmToken(testFcmToken);

		// When
		fcmSenderService.send(notification);

		// Then
		verify(firebaseMessaging).send(any(Message.class));
		verify(fcmTokenRepository).findByUserIdAndValue(testUserId, testFcmTokenValue);
		verify(fcmTokenRepository).deleteFcmToken(testFcmToken);
	}
}
