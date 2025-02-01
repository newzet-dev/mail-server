package com.newzet.api.newsletter.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.newzet.api.newsletter.domain.Newsletter;
import com.newzet.api.newsletter.domain.NewsletterStatus;

@SpringBootTest
@Transactional
class NewsletterRepositoryImplTest {

	@Autowired
	private NewsletterRepositoryImpl newsletterRepository;

	@Test
	public void 뉴스레터_저장() {
		// Given
		String name = "test";
		String domain = "test@example.com";
		String mailingList = "test123";
		NewsletterStatus status = NewsletterStatus.REGISTERED;

		// When
		Newsletter savedNewsletter = newsletterRepository.save(name, domain, mailingList, status);

		// Then
		assertEquals(name, savedNewsletter.getName());
		assertEquals(domain, savedNewsletter.getDomain());
		assertEquals(mailingList, savedNewsletter.getMailingList());
		assertEquals(status, savedNewsletter.getStatus());
	}

	@Test
	public void domain과mailingList로_뉴스레터_조회_시_뉴스레터가_있는_경우_뉴스레터를_반환() {
		//Given
		Newsletter savedNewsletter = saveNewsletterOnDatabase();

		//When
		Newsletter foundNewsletter = newsletterRepository.findByDomainOrMailingList(
			savedNewsletter.getDomain(), savedNewsletter.getMailingList()).get();

		// Then
		verifyFindByDomainOrMailingList(foundNewsletter, savedNewsletter);
	}

	@Test
	public void domain으로_뉴스레터_조회_시_뉴스레터가_있는_경우_뉴스레터를_반환() {
		//Given
		Newsletter savedNewsletter = saveNewsletterOnDatabase();

		//When
		Newsletter foundNewsletter = newsletterRepository.findByDomainOrMailingList(
			savedNewsletter.getDomain(), null).get();

		// Then
		verifyFindByDomainOrMailingList(foundNewsletter, savedNewsletter);
	}

	@Test
	public void mailingList로_뉴스레터_조회_시_뉴스레터가_있는_경우_뉴스레터를_반환() {
		//Given
		Newsletter savedNewsletter = saveNewsletterOnDatabase();

		//When
		Newsletter foundNewsletter = newsletterRepository.findByDomainOrMailingList(
			"no_exist@example.com", savedNewsletter.getMailingList()).get();

		// Then
		verifyFindByDomainOrMailingList(foundNewsletter, savedNewsletter);
	}

	@Test
	public void domain과mailingList로_뉴스레터_조회_시_뉴스레터가_없는_경우_Optional_empty를_반환() {
		//Given
		Newsletter savedNewsletter = saveNewsletterOnDatabase();

		//When, Then
		assertEquals(Optional.empty(),
			newsletterRepository.findByDomainOrMailingList(" ", " "));
	}

	private Newsletter saveNewsletterOnDatabase() {
		String name = "test";
		String domain = "test@example.com";
		String mailingList = "test123";
		NewsletterStatus status = NewsletterStatus.REGISTERED;
		return newsletterRepository.save(name, domain, mailingList, status);
	}

	private static void verifyFindByDomainOrMailingList(Newsletter foundNewsletter,
		Newsletter savedNewsletter) {
		assertEquals(savedNewsletter.getDomain(), foundNewsletter.getDomain());
		assertEquals(savedNewsletter.getName(), foundNewsletter.getName());
		assertEquals(savedNewsletter.getMailingList(), foundNewsletter.getMailingList());
		assertEquals(savedNewsletter.getStatus(), foundNewsletter.getStatus());
	}
}
