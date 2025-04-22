package com.newzet.api.newsletter.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.newzet.api.config.PostgresTestContainerConfig;

@DataJpaTest
@Import(NewsletterRepositoryImpl.class)
@ExtendWith(PostgresTestContainerConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class NewsletterRepositoryImplTest {

	@Autowired
	private NewsletterRepositoryImpl newsletterRepository;

	private static void verifyFindByDomainOrMailingList(NewsletterEntity n1,
		NewsletterEntity n2) {
		assertEquals((n1.getId()), n2.getId());
		assertEquals(n1.getDomain(), n2.getDomain());
		assertEquals(n1.getName(), n2.getName());
		assertEquals(n1.getMailingList(), n2.getMailingList());
		assertEquals(n1.getStatus(), n2.getStatus());
	}

	@Test
	public void save_returnNewsletterEntityDto() {
		// Given
		String name = "test";
		String domain = "test@example.com";
		String mailingList = "test123";
		String status = "REGISTERED";

		// When
		NewsletterEntity savedNewsletter = newsletterRepository
			.save(name, domain, mailingList, status);

		// Then
		assertEquals(name, savedNewsletter.getName());
		assertEquals(domain, savedNewsletter.getDomain());
		assertEquals(mailingList, savedNewsletter.getMailingList());
	}

	@Test
	public void findByDomainOrMailingList_whenNewsletterExist_returnNewsletterEntity() {
		//Given
		NewsletterEntity savedNewsletter = saveNewsletter();

		//When
		Optional<NewsletterEntity> foundNewsletter = newsletterRepository.findByDomainOrMailingList(
			savedNewsletter.getDomain(), savedNewsletter.getMailingList());

		// Then
		assertTrue(foundNewsletter.isPresent());
		verifyFindByDomainOrMailingList(foundNewsletter.get(), savedNewsletter);
	}

	@Test
	public void findByDomainOrMailingList_whenNewsletterExistByDomain_returnNewsletterEntity() {
		//Given
		NewsletterEntity savedNewsletter = saveNewsletter();

		//When
		Optional<NewsletterEntity> foundNewsletter = newsletterRepository.findByDomainOrMailingList(
			savedNewsletter.getDomain(), null);

		// Then
		assertTrue(foundNewsletter.isPresent());
		verifyFindByDomainOrMailingList(foundNewsletter.get(), savedNewsletter);
	}

	@Test
	public void findByDomainOrMailingList_whenNewsletterExistByMailingList_returnNewsletterEntity() {
		//Given
		NewsletterEntity savedNewsletter = saveNewsletter();

		//When
		Optional<NewsletterEntity> foundNewsletter = newsletterRepository.findByDomainOrMailingList(
			"noexist@domain.com",
			savedNewsletter.getMailingList());

		// Then
		assertTrue(foundNewsletter.isPresent());
		verifyFindByDomainOrMailingList(foundNewsletter.get(), savedNewsletter);
	}

	@Test
	public void findByDomainOrMailingList_whenNewsletterNoExist_returnEmpty() {
		//When
		Optional<NewsletterEntity> foundNewsletter = newsletterRepository.findByDomainOrMailingList(
			"test@example.com", "test123");

		// Then
		assertEquals(Optional.empty(), foundNewsletter);
	}

	private NewsletterEntity saveNewsletter() {
		String name = "test";
		String domain = "test@example.com";
		String mailingList = "test123";
		String status = "REGISTERED";
		return newsletterRepository.save(name, domain, mailingList, status);
	}
}
