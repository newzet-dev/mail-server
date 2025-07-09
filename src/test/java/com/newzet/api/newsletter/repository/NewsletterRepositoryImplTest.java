package com.newzet.api.newsletter.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.newzet.api.config.PostgresTestContainerConfig;
import com.newzet.api.newsletter.domain.Newsletter;
import com.newzet.api.newsletter.domain.NewsletterColor;
import com.newzet.api.newsletter.exception.NoNewsletterException;
import com.newzet.api.newsletter.jpa.repository.NewsletterRepositoryImpl;

@DataJpaTest
@Import(NewsletterRepositoryImpl.class)
@ExtendWith(PostgresTestContainerConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class NewsletterRepositoryImplTest {

	@Autowired
	private NewsletterRepositoryImpl newsletterRepository;

	@Test
	public void findById_whenNewsletterExists_shouldReturnNewsletter() {
		//Given
		Newsletter newsletter = new Newsletter(
			null,
			"test",
			UUID.randomUUID(),
			"test",
			"test",
			1,
			"test",
			"test",
			"test",
			"test",
			"test",
			"test",
			NewsletterColor.DEFAULT,
			LocalDateTime.now()
		);
		Newsletter saved = newsletterRepository.save(newsletter);

		//When
		Newsletter founded = newsletterRepository.findById(saved.getId());

		// Then
		assertEquals((saved.getId()), founded.getId());
		assertEquals(saved.getName(), founded.getName());
		assertEquals(saved.getCategoryId(), founded.getCategoryId());
		assertEquals(saved.getDomain(), founded.getDomain());
		assertEquals(saved.getMailingList(), founded.getMailingList());
		assertEquals(saved.getPriority(), founded.getPriority());
		assertEquals(saved.getImageUrl(), founded.getImageUrl());
		assertEquals(saved.getDescription(), founded.getDescription());
		assertEquals(saved.getDetail(), founded.getDetail());
		assertEquals(saved.getStatus(), founded.getStatus());
		assertEquals(saved.getDayOfWeek(), founded.getDayOfWeek());
		assertEquals(saved.getSubscriptionUrl(), founded.getSubscriptionUrl());
		assertEquals(saved.getColor(), founded.getColor());
		assertEquals(saved.getDeletedAt(), founded.getDeletedAt());

	}

	@Test
	public void findById_whenNewsletterNotExists_shouldThrowNoNewsletterException() {
		// When + Then
		assertThrows(NoNewsletterException.class, () -> newsletterRepository.findById(UUID.randomUUID()));
	}
}
