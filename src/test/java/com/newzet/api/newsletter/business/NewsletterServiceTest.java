package com.newzet.api.newsletter.business;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.newzet.api.newsletter.domain.Newsletter;
import com.newzet.api.newsletter.domain.NewsletterStatus;

@ExtendWith(MockitoExtension.class)
class NewsletterServiceTest {

	@Mock
	private NewsletterRepository newsletterRepository;

	@InjectMocks
	private NewsletterService newsletterService;

	@Test
	void findOrCreateNewsletter_WhenNewsletterExists_ReturnExistingNewsletter() {
		// Given
		String name = "test";
		String domain = "test@example.com";
		String maillingList = "test123";
		Newsletter existingNewsletter = Newsletter.create(1L, name, domain, maillingList,
			NewsletterStatus.REGISTERED);
		when(newsletterRepository.findByDomainOrMaillingList(domain, maillingList))
			.thenReturn(Optional.of(existingNewsletter));

		// When
		Newsletter newsletter = newsletterService.findOrCreateNewsletter(name, domain,
			maillingList);

		// Then
		assertEquals(existingNewsletter, newsletter);
		verify(newsletterRepository, times(1))
			.findByDomainOrMaillingList(domain, maillingList);
		verify(newsletterRepository, never()).save(any(), any(), any(), any());
	}
}
