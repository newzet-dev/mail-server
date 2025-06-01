package com.newzet.api.fcm.business.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import com.newzet.api.fcm.business.FcmTokenRepository;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

@ExtendWith(MockitoExtension.class)
class FcmTokenServiceTest {

	private final UUID userId = UUID.randomUUID();
	private final String token = "token";
	@Mock
	private FcmTokenRepository fcmTokenRepository;
	@InjectMocks
	private FcmTokenService fcmTokenService;

	@Test
	public void deleteFcmToken_whenDeleted_doNothing() {
		// Given
		when(fcmTokenRepository.deleteFcmToken(userId, token)).thenReturn(true);
		ListAppender<ILoggingEvent> logAppender = getListAppenderForClass(FcmTokenService.class);

		// When
		fcmTokenService.deleteFcmToken(userId, token);

		// Then
		List<ILoggingEvent> logs = logAppender.list;
		assertTrue(logs.isEmpty());
	}

	@Test
	public void deleteFcmToken_whenDeleted_logging() {
		// Given
		when(fcmTokenRepository.deleteFcmToken(userId, token)).thenReturn(false);
		ListAppender<ILoggingEvent> logAppender = getListAppenderForClass(FcmTokenService.class);

		// When
		fcmTokenService.deleteFcmToken(userId, token);

		// Then
		List<ILoggingEvent> logs = logAppender.list;
		assertFalse(logs.isEmpty());
		assertEquals("WARN", logs.get(0).getLevel().toString());
		assertTrue(logs.get(0).getFormattedMessage().contains("비정상 흐름"));
		assertTrue(logs.get(0).getFormattedMessage().contains(userId.toString()));
		assertTrue(logs.get(0).getFormattedMessage().contains(token));
	}

	private ListAppender<ILoggingEvent> getListAppenderForClass(Class clazz) {
		Logger logger = (Logger)LoggerFactory.getLogger(clazz);

		ListAppender<ILoggingEvent> loggingEventListAppender = new ListAppender<>();
		loggingEventListAppender.start();

		logger.addAppender(loggingEventListAppender);

		return loggingEventListAppender;
	}
}
