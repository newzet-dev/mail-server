package com.newzet.api.newsletter.repository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.newzet.api.category.repository.CategoryEntity;
import com.newzet.api.category.repository.CategoryJpaRepository;
import com.newzet.api.config.PostgresTestContainerConfig;
import com.newzet.api.newsletter.business.dto.NewsletterEntityDto;
import com.newzet.api.newsletter.business.dto.NewsletterImageUrlCacheDto;
import com.newzet.api.newsletter.exception.NoNewsletterException;
import com.newzet.api.newsletter.fixture.NewsletterFixture;

@DataJpaTest
@Import(NewsletterRepositoryImpl.class)
@ExtendWith(PostgresTestContainerConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class NewsletterRepositoryImplTest {

	@Autowired
	private NewsletterRepositoryImpl newsletterRepository;
	@Autowired
	private CategoryJpaRepository categoryJpaRepository;
	@Autowired
	private NewsletterJpaRepository newsletterJpaRepository;

	private static void verifyFindByDomainOrMailingList(NewsletterEntityDto n1,
		NewsletterEntityDto n2) {
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
		NewsletterEntityDto savedNewsletter = newsletterRepository
			.save(name, domain, mailingList, status);

		// Then
		assertEquals(name, savedNewsletter.getName());
		assertEquals(domain, savedNewsletter.getDomain());
		assertEquals(mailingList, savedNewsletter.getMailingList());
	}

	@Test
	public void findByDomainOrMailingList_whenNewsletterExist_returnNewsletterEntity() {
		//Given
		NewsletterEntityDto savedNewsletter = saveNewsletter();

		//When
		Optional<NewsletterEntityDto> foundNewsletter = newsletterRepository.findByDomainOrMailingList(
			savedNewsletter.getDomain(), savedNewsletter.getMailingList());

		// Then
		assertTrue(foundNewsletter.isPresent());
		verifyFindByDomainOrMailingList(foundNewsletter.get(), savedNewsletter);
	}

	@Test
	public void findByDomainOrMailingList_whenNewsletterExistByDomain_returnNewsletterEntity() {
		//Given
		NewsletterEntityDto savedNewsletter = saveNewsletter();

		//When
		Optional<NewsletterEntityDto> foundNewsletter = newsletterRepository.findByDomainOrMailingList(
			savedNewsletter.getDomain(), null);

		// Then
		assertTrue(foundNewsletter.isPresent());
		verifyFindByDomainOrMailingList(foundNewsletter.get(), savedNewsletter);
	}

	@Test
	public void findByDomainOrMailingList_whenNewsletterExistByMailingList_returnNewsletterEntity() {
		//Given
		NewsletterEntityDto savedNewsletter = saveNewsletter();

		//When
		Optional<NewsletterEntityDto> foundNewsletter = newsletterRepository.findByDomainOrMailingList(
			"noexist@domain.com",
			savedNewsletter.getMailingList());

		// Then
		assertTrue(foundNewsletter.isPresent());
		verifyFindByDomainOrMailingList(foundNewsletter.get(), savedNewsletter);
	}

	@Test
	public void findByDomainOrMailingList_whenNewsletterNoExist_returnEmpty() {
		//When
		Optional<NewsletterEntityDto> foundNewsletter = newsletterRepository.findByDomainOrMailingList(
			"test@example.com", "test123");

		// Then
		assertEquals(Optional.empty(), foundNewsletter);
	}

	@Test
	public void findNewsletterListByName_WithName() {
		//Given
		NewsletterEntityDto savedNewsletter = saveNewsletter();

		//When
		List<NewsletterEntityDto> newsletterList = newsletterRepository.findNewsLetterListByName(
			savedNewsletter.getName());

		//Then
		assertEquals(1, newsletterList.size());
		assertEquals(newsletterList.get(0).getName(), savedNewsletter.getName());
	}

	@Test
	public void findNewsletterListByCategoryId_WithCategoryId() {
		//Given
		CategoryEntity category = categoryJpaRepository.save(
			CategoryEntity.create("testCategory", "test", "test"));
		NewsletterEntity newsletter = newsletterJpaRepository.save(
			NewsletterFixture.createDefaultEntity(category));

		//When
		List<NewsletterEntityDto> newsletterList = newsletterRepository.findNewsLetterListByCategoryId(
			category.getId());

		//Then
		assertEquals(1, newsletterList.size());
		assertEquals(newsletterList.get(0).getName(), newsletter.getName());
		assertEquals(category.getId(), newsletter.getCategory().getId());
	}

	@Test
	public void getNewsletterById() {
		//Given
		NewsletterEntity newsletter = newsletterJpaRepository.save(
			NewsletterFixture.createDefaultEntity());

		//When
		NewsletterEntityDto getNewsletterEntity = newsletterRepository.getById(newsletter.getId());

		//Then
		assertEquals(newsletter.getName(), getNewsletterEntity.getName());
		assertEquals(newsletter.getDomain(), getNewsletterEntity.getDomain());
		assertEquals(newsletter.getMailingList(), getNewsletterEntity.getMailingList());
	}

	@Test
	public void getNewsletterById_With_NonExistentId() {
		//Given
		UUID nonExistentId = UUID.randomUUID();

		//When Then
		assertThrows(NoNewsletterException.class,
			() -> newsletterRepository.getById(nonExistentId));
	}

	@Test
	public void getNewsletterList_by_category_list() {
		//Given
		List<NewsletterEntity> newsletterEntityList = new ArrayList<>();
		List<UUID> categoryIdList = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			CategoryEntity category = categoryJpaRepository.save(
				CategoryEntity.create("testCategory" + i, "test", "test"));
			NewsletterEntity newsletter = newsletterJpaRepository.save(
				NewsletterFixture.createEntityUnique("domain#" + i, category));
			newsletterEntityList.add(newsletter);
			categoryIdList.add(category.getId());
		}

		//When
		List<NewsletterEntityDto> newsLetterListByCategoryIdList = newsletterRepository.getNewsLetterListByCategoryIdList(
			categoryIdList);

		//Then
		for (int i = 0; i < 5; i++) {
			assertThat(newsLetterListByCategoryIdList.get(i).getId())
				.isEqualTo(newsletterEntityList.get(i).getId());
		}

	}

	@Test
	@DisplayName("성공: domain으로 이미지 URL을 정확히 조회한다")
	void findNewsLetterImageUrlByDomainAndMailingList_success_byDomain() {
		// given (준비): 테스트 데이터를 DB에 저장
		CategoryEntity categoryEntity = categoryJpaRepository.save(CategoryEntity.create("testCategory", "test", "test"));
		String expectedUrl = "http://test.com/image.png";
		newsletterJpaRepository.save(
			NewsletterFixture.createEntityWithImageUrlAndDomainAndMailingList(categoryEntity, expectedUrl,
				"test.com", "hello-news"));
		// when (실행): domain은 일치, mailingList는 불일치하는 조건으로 조회
		NewsletterImageUrlCacheDto resultDto = newsletterRepository.findNewsLetterImageUrlByDomainAndMailingList(
			"test.com", "another-mailing-list");

		// then (검증): 예상된 URL이 포함된 DTO가 반환되었는지 확인
		assertThat(resultDto).isNotNull();
		assertThat(resultDto.imageUrl()).isEqualTo(expectedUrl);
	}

	@Test
	@DisplayName("성공: mailingList로 이미지 URL을 정확히 조회한다")
	void findNewsLetterImageUrlByDomainAndMailingList_success_byMailingList() {
		// given (준비)
		CategoryEntity categoryEntity = categoryJpaRepository.save(CategoryEntity.create("testCategory", "test", "test"));
		String expectedUrl = "http://hello.com/logo.jpg";
		newsletterJpaRepository.save(
			NewsletterFixture.createEntityWithImageUrlAndDomainAndMailingList(categoryEntity, expectedUrl,
				"hello.com", "hello-news"));

		// when (실행): domain은 불일치, mailingList는 일치하는 조건으로 조회
		NewsletterImageUrlCacheDto resultDto = newsletterRepository.findNewsLetterImageUrlByDomainAndMailingList(
			"another-domain.com", "hello-news");

		// then (검증)
		assertThat(resultDto).isNotNull();
		assertThat(resultDto.imageUrl()).isEqualTo(expectedUrl);
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

	private NewsletterEntityDto saveNewsletter() {
		String name = "test";
		String domain = "test@example.com";
		String mailingList = "test123";
		String status = "REGISTERED";
		return newsletterRepository.save(name, domain, mailingList, status);
	}
}
