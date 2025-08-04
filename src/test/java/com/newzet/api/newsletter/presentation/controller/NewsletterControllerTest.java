package com.newzet.api.newsletter.presentation.controller;

import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.newzet.api.common.auth.domain.AuthUser;
import com.newzet.api.newsletter.orchestrator.NewsletterOrchestrator;
import com.newzet.api.newsletter.presentation.dto.NewsletterInfoResponse;

@ExtendWith(MockitoExtension.class)
class NewsletterControllerTest {
	private final UUID TEST_NEWSLETTER_ID = UUID.randomUUID();
	private final UUID TEST_USER_ID = UUID.randomUUID();
	private final NewsletterInfoResponse response = new NewsletterInfoResponse(TEST_NEWSLETTER_ID, "test", "test.com",
		"test", "test", "test", "test.com", false, "test");

	@Mock
	private NewsletterOrchestrator newsletterOrchestrator;
	@InjectMocks
	private NewsletterController newsletterController;

	@Test
	@DisplayName("로그인하지 않은 상태로 뉴스레터 상세정보를 조회한다.")
	void getNewsletterInfoById_withoutLogin_shouldReturnInfo() {
		// Given
		when(newsletterOrchestrator.getNewsletterInfoWithoutLogin(TEST_NEWSLETTER_ID))
			.thenReturn(response);

		// When
		newsletterController.getNewsletterInfoById(Optional.empty(), TEST_NEWSLETTER_ID);

		// Then
		verify(newsletterOrchestrator, times(1)).getNewsletterInfoWithoutLogin(TEST_NEWSLETTER_ID);
		verify(newsletterOrchestrator, never()).getNewsLetterInfoWithLogin(any(), any());
	}

	@Test
	@DisplayName("로그인한 상태로 뉴스레터 상세정보를 조회한다.")
	void getNewsletterInfoById_withLogin_shouldReturnInfo() {
		// Given
		AuthUser authUser = new AuthUser(TEST_USER_ID);
		when(newsletterOrchestrator.getNewsLetterInfoWithLogin(TEST_USER_ID, TEST_NEWSLETTER_ID))
			.thenReturn(response);

		// When
		newsletterController.getNewsletterInfoById(Optional.of(authUser), TEST_NEWSLETTER_ID);

		// Then
		verify(newsletterOrchestrator, times(1)).getNewsLetterInfoWithLogin(TEST_USER_ID, TEST_NEWSLETTER_ID);
		verify(newsletterOrchestrator, never()).getNewsletterInfoWithoutLogin(any());
	}
}
