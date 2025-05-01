package com.newzet.api.newsletter.business;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import com.newzet.api.advertise.business.AdvertiseRepository;
import com.newzet.api.advertise.business.dto.AdvertiseEntityDto;
import com.newzet.api.category.business.dto.CategoryEntityDto;
import com.newzet.api.category.repository.CategoryEntity;
import com.newzet.api.newsletter.business.exception.NotEnoughNewslettersException;
import com.newzet.api.newsletter.domain.Color;
import com.newzet.api.newsletter.domain.Newsletter;
import com.newzet.api.newsletter.repository.NewsletterEntity;
import com.newzet.api.newsletter.util.NewsletterRecommender;
import com.newzet.api.user.repository.entity.UserEntity;
import com.newzet.api.user.repository.entity.UserEntityStatus;
import com.newzet.api.usercategory.business.UserCategoryRepository;
import com.newzet.api.usercategory.business.dto.UserCategoryEntityDto;

@ExtendWith(MockitoExtension.class)
@Import(NewsletterRecommender.class)
class NewsletterRecommendationServiceTest {

	@Mock
	private NewsletterRepository newsletterRepository;
	@Mock
	private UserCategoryRepository userCategoryRepository;
	@Mock
	private AdvertiseRepository advertiseRepository;

	@InjectMocks
	private NewsletterRecommendationService newsletterRecommendationService;

	@Test
	public void newsletterList_has_low_number_of_elements_than_count_throw_exception() {
		// Given
		List<Newsletter> newsletterList = new ArrayList<>();
		List<Newsletter> advertiseNewsletterList = Collections.singletonList(
			createNewsletterWithName("advertise_newsletter",
				CategoryEntity.create(UUID.randomUUID(), "category", "imageurl",
					"emoji")).toEntityDto()
				.toDomain());

		// When Then
		assertThrows(NotEnoughNewslettersException.class,
			() -> newsletterRecommendationService.createRandomNewsletterList(UUID.randomUUID()));

	}

	@DisplayName("광고 뉴스레터 수가 1개일때(1번 카테고리의 다른 뉴스레터라 가정), 유저 카테고리에 해당되는 랜덤 뉴스레터 3개와 1개의 광고 뉴스레터가 전달되어야 한다.")
	@Test
	public void recommend_newsletter_list_expected_recommend_number() {
		// Given
		UserEntity user = UserEntity.create(UUID.randomUUID(), "email", "nickname",
			UserEntityStatus.ACTIVE.name());
		List<UUID> userCategoryIdList = Arrays.asList(UUID.randomUUID(), UUID.randomUUID(),
			UUID.randomUUID());
		List<UserCategoryEntityDto> userCategoryEntityDtoList = userCategoryIdList.stream()
			.map(id -> createUserCategoryDto(id, user))
			.toList();

		List<NewsletterEntity> newsletterList = new ArrayList<>();
		for (UUID id : userCategoryIdList) {
			createNewsletterByCategoryId(newsletterList, id);
		}

		NewsletterEntity advertiseNewsletter = createNewsletterWithName("advertise_newsletter",
			newsletterList.get(0).getCategory());
		AdvertiseEntityDto advertise = AdvertiseEntityDto.create(UUID.randomUUID(),
			advertiseNewsletter.getId());
		List<AdvertiseEntityDto> advertiseList = Collections.singletonList(advertise);

		when(userCategoryRepository.getUserCategoryListByUserId(any(UUID.class)))
			.thenReturn(userCategoryEntityDtoList);
		when(newsletterRepository.getNewsLetterListByCategoryIdList(
			anyList())).thenReturn(
			newsletterList.stream().map(NewsletterEntity::toEntityDto).toList());
		when(advertiseRepository.getAllAdvertise())
			.thenReturn(advertiseList);
		when(newsletterRepository.getById(any(UUID.class)))
			.thenReturn(advertiseNewsletter.toEntityDto());
		// When
		List<Newsletter> recommendNewsletterList = newsletterRecommendationService.createRandomNewsletterList(
			user.getId());

		// Then
		assertThat(recommendNewsletterList).hasSize(4);
		List<String> newsletterName = recommendNewsletterList.stream()
			.map(Newsletter::getName)
			.toList();
		assertThat(newsletterName)
			.filteredOn(name -> name.equals("advertise_newsletter"))
			.hasSize(1);
		assertThat(newsletterName)
			.filteredOn(name -> name.equals("usercategory_newsletter"))
			.hasSize(3);
	}

	private UserCategoryEntityDto createUserCategoryDto(UUID id, UserEntity user) {
		return UserCategoryEntityDto.create(UUID.randomUUID(), user.toEntityDto()
			, CategoryEntityDto.create(id, "category", "imageurl", "emoji"));
	}

	// 한 category 당 2개의 뉴스레터를 가진다.
	private void createNewsletterByCategoryId(List<NewsletterEntity> newsletterList,
		UUID categoryId) {
		CategoryEntity category = CategoryEntity.create(categoryId, "testCategory", "test", "test");
		NewsletterEntity newsletter = createNewsletterWithName("usercategory_newsletter", category);
		newsletterList.add(newsletter);
		NewsletterEntity newsletter2 = createNewsletterWithName("usercategory_newsletter",
			category);
		newsletterList.add(newsletter2);
	}

	private NewsletterEntity createNewsletterWithName(String newsletterName,
		CategoryEntity category) {
		return NewsletterEntity.create(UUID.randomUUID(), newsletterName, category,
			"domain not unique in mock",
			"malinglist", 1, "test", "test", "test", "test", "test", "test", Color.DEFAULT, null);
	}
}