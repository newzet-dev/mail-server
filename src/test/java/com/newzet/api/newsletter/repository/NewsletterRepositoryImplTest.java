package com.newzet.api.newsletter.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.assertj.core.api.Assertions;
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
import com.newzet.api.newsletter.fixture.NewsletterFixture;
import com.newzet.api.newsletter.repository.exception.NoNewsletterException;

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
	public void findNewsletterListByNameOrCategoryId_WithName() {
		//Given
		NewsletterEntityDto savedNewsletter = saveNewsletter();

		//When
		List<NewsletterEntityDto> newsletterList = newsletterRepository.findNewsLetterListByNameOrCategoryId(
			savedNewsletter.getName(), null);

		//Then
		assertEquals(1, newsletterList.size());
		assertEquals(newsletterList.get(0).getName(), savedNewsletter.getName());
	}

	@Test
	public void findNewsletterListByNameOrCategoryId_WithCategoryId() {
		//Given
		CategoryEntity category = categoryJpaRepository.save(
			CategoryEntity.create("testCategory", "test", "test"));
		NewsletterEntity newsletter = newsletterJpaRepository.save(
			NewsletterFixture.createDefaultEntity(category));

		//When
		List<NewsletterEntityDto> newsletterList = newsletterRepository.findNewsLetterListByNameOrCategoryId(
			null, category.getId());

		//Then
		assertEquals(1, newsletterList.size());
		assertEquals(newsletterList.get(0).getName(), newsletter.getName());
		assertEquals(category.getId(), newsletter.getCategory().getId());
	}

	@Test
	public void findNewsletterListByNameOrCategoryId_WithName_andCategoryId() {
		//Given
		CategoryEntity category = categoryJpaRepository.save(
			CategoryEntity.create("testCategory", "test", "test"));
		NewsletterEntity newsletter = newsletterJpaRepository.save(
			NewsletterFixture.createDefaultEntity(category));

		//When
		List<NewsletterEntityDto> newsletterList = newsletterRepository.findNewsLetterListByNameOrCategoryId(
			newsletter.getName(), category.getId());

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
		for (int i=0;i<5;i++) {
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
		for (int i=0;i<5;i++) {
			Assertions.assertThat(newsLetterListByCategoryIdList.get(i).getId()).isEqualTo(newsletterEntityList.get(i).getId());
		}

	}

	private NewsletterEntityDto saveNewsletter() {
		String name = "test";
		String domain = "test@example.com";
		String mailingList = "test123";
		String status = "REGISTERED";
		return newsletterRepository.save(name, domain, mailingList, status);
	}
}
