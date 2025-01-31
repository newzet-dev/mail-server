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
		String maillingList = "test123";
		NewsletterStatus status = NewsletterStatus.REGISTERED;

		// When
		Newsletter savedNewsletter = newsletterRepository.save(name, domain, maillingList, status);

		// Then
		assertEquals(1L, savedNewsletter.getId());
		assertEquals(name, savedNewsletter.getName());
		assertEquals(domain, savedNewsletter.getDomain());
		assertEquals(maillingList, savedNewsletter.getMaillingList());
		assertEquals(status, savedNewsletter.getStatus());
	}

	@Test
	public void domain과maillingList로_뉴스레터_조회_시_뉴스레터가_있는_경우_뉴스레터를_반환() {
		//Given
		Newsletter savedNewsletter = saveNewsletter();

		//When
		Newsletter foundNewsletter = newsletterRepository.findByDomainOrMaillingList(
			savedNewsletter.getDomain(), savedNewsletter.getMaillingList()).get();

		// Then
		verifyFindByDomainOrMaillingList(foundNewsletter, savedNewsletter);
	}

	@Test
	public void domain으로_뉴스레터_조회_시_뉴스레터가_있는_경우_뉴스레터를_반환() {
		//Given
		Newsletter savedNewsletter = saveNewsletter();

		//When
		Newsletter foundNewsletter = newsletterRepository.findByDomainOrMaillingList(
			savedNewsletter.getDomain(), " ").get();

		// Then
		verifyFindByDomainOrMaillingList(foundNewsletter, savedNewsletter);
	}

	@Test
	public void maillingList로_뉴스레터_조회_시_뉴스레터가_있는_경우_뉴스레터를_반환() {
		//Given
		Newsletter savedNewsletter = saveNewsletter();

		//When
		Newsletter foundNewsletter = newsletterRepository.findByDomainOrMaillingList(
			savedNewsletter.getDomain(), " ").get();

		// Then
		verifyFindByDomainOrMaillingList(foundNewsletter, savedNewsletter);
	}

	@Test
	public void domain과maillingList로_뉴스레터_조회_시_뉴스레터가_없는_경우_Optional_empty를_반환() {
		//Given
		Newsletter savedNewsletter = saveNewsletter();

		//When, Then
		assertEquals(Optional.empty(),
			newsletterRepository.findByDomainOrMaillingList(" ", " "));
	}

	private Newsletter saveNewsletter() {
		String name = "test";
		String domain = "test@example.com";
		String maillingList = "test123";
		NewsletterStatus status = NewsletterStatus.REGISTERED;
		return newsletterRepository.save(name, domain, maillingList, status);
	}

	private static void verifyFindByDomainOrMaillingList(Newsletter foundNewsletter,
		Newsletter savedNewsletter) {
		assertEquals(savedNewsletter.getDomain(), foundNewsletter.getDomain());
		assertEquals(savedNewsletter.getName(), foundNewsletter.getName());
		assertEquals(savedNewsletter.getMaillingList(), foundNewsletter.getMaillingList());
		assertEquals(savedNewsletter.getStatus(), foundNewsletter.getStatus());
	}
}
