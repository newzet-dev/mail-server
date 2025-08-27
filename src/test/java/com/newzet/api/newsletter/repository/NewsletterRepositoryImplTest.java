package com.newzet.api.newsletter.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.newzet.api.category.jpa.CategoryEntity;
import com.newzet.api.category.jpa.CategoryJpaRepository;
import com.newzet.api.config.PostgresTestContainerConfig;
import com.newzet.api.newsletter.business.dto.NewsletterImageUrlCacheDto;
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

	@Autowired
	private CategoryJpaRepository categoryJpaRepository;

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

	@Test
	@DisplayName("성공: domain으로 이미지 URL을 정확히 조회한다")
	void findNewsLetterImageUrlByDomainAndMailingList_success_byDomain() {
		// given (준비): 테스트 데이터를 DB에 저장
		CategoryEntity categoryEntity = categoryJpaRepository.save(
			new CategoryEntity(null, "testCategory", "test", "test"));
		String expectedUrl = "http://test.com/image.png";
		newsletterRepository.save(
			new Newsletter(null, "test", categoryEntity.getId(), "test.com", "hello-news", 1, expectedUrl, "", "", "",
				"",
				"", NewsletterColor.DEFAULT, null));
		// when (실행): domain은 일치, mailingList는 불일치하는 조건으로 조회
		NewsletterImageUrlCacheDto resultDto = newsletterRepository.findNewsLetterImageUrlByDomainAndMailingList(
			"test.com", "another-mailing-list");

		// then (검증): 예상된 URL이 포함된 DTO가 반환되었는지 확인
		assertNotEquals(null, resultDto);
		assertEquals(expectedUrl, resultDto.imageUrl());
	}

	@Test
	@DisplayName("성공: mailingList로 이미지 URL을 정확히 조회한다")
	void findNewsLetterImageUrlByDomainAndMailingList_success_byMailingList() {
		// given (준비)
		CategoryEntity categoryEntity = categoryJpaRepository.save(
			new CategoryEntity(null, "testCategory", "test", "test"));
		String expectedUrl = "http://hello.com/logo.jpg";
		newsletterRepository.save(
			new Newsletter(null, "test", categoryEntity.getId(), "hello.com", "hello-news", 1, expectedUrl, "", "", "",
				"",
				"", NewsletterColor.DEFAULT, null));
		// when (실행): domain은 불일치, mailingList는 일치하는 조건으로 조회
		NewsletterImageUrlCacheDto resultDto = newsletterRepository.findNewsLetterImageUrlByDomainAndMailingList(
			"another-domain.com", "hello-news");

		// then (검증)
		assertNotEquals(null, resultDto);
		assertEquals(expectedUrl, resultDto.imageUrl());
	}

	@Test
	@DisplayName("실패: 일치하는 domain이나 mailingList가 없으면 NoNewsletterException 예외를 던진다")
	void findNewsLetterImageUrlByDomainAndMailingList_failure_throwsException() {
		// given
		String nonExistingDomain = "non-existing.com";
		String nonExistingMailingList = "no-list";

		// when & then
		assertThrows(NoNewsletterException.class, () -> {
			newsletterRepository.findNewsLetterImageUrlByDomainAndMailingList(
				nonExistingDomain, nonExistingMailingList);
		});
	}
}
