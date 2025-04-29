package com.newzet.api.newsletter.business;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.Lock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.newzet.api.advertise.business.AdvertiseRepository;
import com.newzet.api.advertise.repository.entity.AdvertiseEntity;
import com.newzet.api.category.repository.CategoryEntity;
import com.newzet.api.common.cache.CacheUtil;
import com.newzet.api.common.exception.InternalErrorException;
import com.newzet.api.common.lock.LockFactory;
import com.newzet.api.common.lock.exception.LocalLockAcquisitionException;
import com.newzet.api.common.util.exception.UuidConvertFailException;
import com.newzet.api.newsletter.business.dto.NewsletterCacheDto;
import com.newzet.api.newsletter.business.dto.NewsletterEntityDto;
import com.newzet.api.newsletter.controller.dto.NewsletterInfoResponse;
import com.newzet.api.newsletter.controller.dto.NewsletterListResponse;
import com.newzet.api.newsletter.controller.dto.NewsletterRecommendResponse;
import com.newzet.api.newsletter.controller.dto.NewsletterResponse;
import com.newzet.api.newsletter.domain.Color;
import com.newzet.api.newsletter.domain.Newsletter;
import com.newzet.api.newsletter.fixture.NewsletterFixture;
import com.newzet.api.newsletter.repository.NewsletterEntity;
import com.newzet.api.user.repository.entity.UserEntity;
import com.newzet.api.user.repository.entity.UserEntityStatus;
import com.newzet.api.usercategory.business.UserCategoryRepository;
import com.newzet.api.usercategory.business.dto.UserCategoryEntityDto;

@ExtendWith(MockitoExtension.class)
class NewsletterServiceTest {

	private static final String CACHE_DOMAIN_PREFIX = "newsletter:domain";
	private final String name = "test";
	private final String domain = "test@example.com";
	private final String mailingList = "test123";
	private final String status = "UNREGISTERED";
	private final CategoryEntity categoryEntity = CategoryEntity.create(UUID.randomUUID(), "test",
		"test", "test");
	private final NewsletterEntityDto entityDto = NewsletterFixture.createDefaultEntity()
		.toEntityDto();
	@Mock
	private NewsletterRepository newsletterRepository;
	@Mock
	private UserCategoryRepository userCategoryRepository;
	@Mock
	private AdvertiseRepository advertiseRepository;
	@Mock
	private CacheUtil cacheUtil;
	@Mock
	private LockFactory lockFactory;
	@Mock
	private Lock lock;
	@InjectMocks
	private NewsletterService newsletterService;

	@Test
	void findOrCreateNewsletter_whenNewsletterExistsInCache_ReturnNewsletterInCache() {
		// Given
		NewsletterCacheDto cacheDto = NewsletterFixture.createDefaultCacheDto(categoryEntity);
		when(cacheUtil.get(CACHE_DOMAIN_PREFIX + domain, NewsletterCacheDto.class)).thenReturn(
			Optional.of(cacheDto));

		// When
		Newsletter newsletter = newsletterService.findOrCreateNewsletter(name, domain,
			mailingList);

		// Then
		verifyValue(newsletter);
		verify(lockFactory, never()).tryLock(any(), anyLong(), anyLong());
		verify(newsletterRepository, never()).findByDomainOrMailingList(any(), any());
	}

	@Test
	public void findOrCreateNewsletter_whenNewsletterNoExistInCache_returnNewsletterInDB() {
		// Given
		when(cacheUtil.get(CACHE_DOMAIN_PREFIX + domain, NewsletterCacheDto.class)).thenReturn(
			Optional.empty());
		when(lockFactory.tryLock(any(), anyLong(), anyLong())).thenReturn(lock);
		when(newsletterRepository.findByDomainOrMailingList(domain, mailingList)).thenReturn(
			Optional.of(entityDto));

		// When
		Newsletter newsletter = newsletterService.findOrCreateNewsletter(name, domain, mailingList);

		// Then
		verifyValue(newsletter);
		verify(lockFactory).tryLock(eq(CACHE_DOMAIN_PREFIX + ":" + domain), anyLong(), anyLong());
		verify(cacheUtil, times(2)).get(CACHE_DOMAIN_PREFIX + domain, NewsletterCacheDto.class);
		verify(newsletterRepository).findByDomainOrMailingList(domain, mailingList);
		verify(newsletterRepository, never()).save(any(), any(), any(), any());
		verify(cacheUtil).set(eq(CACHE_DOMAIN_PREFIX + domain), any(), anyLong());
		verify(lockFactory).unlock(lock);
	}

	@Test
	void findOrCreateNewsletter_whenNewsletterNoExist_createAndReturnNewsletter() {
		// Given
		when(cacheUtil.get(CACHE_DOMAIN_PREFIX + domain, NewsletterCacheDto.class)).thenReturn(
			Optional.empty());
		when(lockFactory.tryLock(any(), anyLong(), anyLong())).thenReturn(lock);
		when(newsletterRepository.findByDomainOrMailingList(domain, mailingList)).thenReturn(
			Optional.empty());
		when(newsletterRepository.save(any(), any(), any(), any())).thenReturn(entityDto);

		// When
		Newsletter newsletter = newsletterService.findOrCreateNewsletter(name, domain, mailingList);

		// Then
		verifyValue(newsletter);
		verify(lockFactory).tryLock(eq(CACHE_DOMAIN_PREFIX + ":" + domain), anyLong(), anyLong());
		verify(cacheUtil, times(2)).get(CACHE_DOMAIN_PREFIX + domain, NewsletterCacheDto.class);
		verify(newsletterRepository).findByDomainOrMailingList(domain, mailingList);
		verify(newsletterRepository).save(name, domain, mailingList, status);
		verify(cacheUtil).set(eq(CACHE_DOMAIN_PREFIX + domain), any(), anyLong());
		verify(lockFactory).unlock(lock);
	}

	@Test
	public void findOrCreateNewsletter_whenLockAcquisitionFails_throwException() {
		//Given
		when(cacheUtil.get(CACHE_DOMAIN_PREFIX + domain, NewsletterCacheDto.class)).thenReturn(
			Optional.empty());
		when(lockFactory.tryLock(any(), anyLong(), anyLong())).thenThrow(
			new LocalLockAcquisitionException("Local Lock 획득에 실패하였습니다."));

		// When
		assertThrows(InternalErrorException.class,
			() -> newsletterService.findOrCreateNewsletter(name, domain, mailingList));

		// Then
		verify(cacheUtil, times(1)).get(CACHE_DOMAIN_PREFIX + domain, NewsletterCacheDto.class);
		verify(newsletterRepository, never()).findByDomainOrMailingList(domain, mailingList);
		verify(cacheUtil, never()).set(eq(CACHE_DOMAIN_PREFIX + domain), any(), anyLong());
		verify(lockFactory, never()).unlock(lock);
	}

	@Test
	public void searchNewsletterListByNameOrCategoryId() {
		// Given
		NewsletterEntityDto newsletter = NewsletterFixture.createEntityWithId().toEntityDto();
		List<NewsletterEntityDto> newsletterList = new ArrayList<>();
		newsletterList.add(newsletter);
		when(newsletterRepository.findNewsLetterListByNameOrCategoryId(any(String.class),
			any(UUID.class)))
			.thenReturn(newsletterList);

		// When
		NewsletterListResponse searchList = newsletterService.searchNewsletterListByNameOrCategoryId(
			newsletter.getName(), String.valueOf(categoryEntity.getId()));

		// Then
		verify(newsletterRepository, times(1)).findNewsLetterListByNameOrCategoryId(
			any(String.class), any(UUID.class));
		assertEquals(1, searchList.newsletterList().size());
	}

	@Test
	public void searchNewsletterListByNameOrCategoryId_whenCategoryIdIsNull() {
		// Given
		NewsletterEntityDto newsletter = NewsletterFixture.createEntityWithId().toEntityDto();
		List<NewsletterEntityDto> newsletterList = new ArrayList<>();
		newsletterList.add(newsletter);
		when(newsletterRepository.findNewsLetterListByNameOrCategoryId(any(String.class),
			nullable(UUID.class)))
			.thenReturn(newsletterList);

		// When
		NewsletterListResponse searchList = newsletterService.searchNewsletterListByNameOrCategoryId(
			newsletter.getName(), null);

		// Then
		verify(newsletterRepository, times(1)).findNewsLetterListByNameOrCategoryId(
			any(String.class), nullable(UUID.class));
		assertEquals(1, searchList.newsletterList().size());
	}

	@Test
	public void searchNewsletterListByNameOrCategoryId_whenCategoryIdEmpty() {
		// Given
		NewsletterEntityDto newsletter = NewsletterFixture.createEntityWithId().toEntityDto();
		List<NewsletterEntityDto> newsletterList = new ArrayList<>();
		newsletterList.add(newsletter);
		when(newsletterRepository.findNewsLetterListByNameOrCategoryId(any(String.class),
			nullable(UUID.class)))
			.thenReturn(newsletterList);

		// When
		NewsletterListResponse searchList = newsletterService.searchNewsletterListByNameOrCategoryId(
			newsletter.getName(), "");

		// Then
		verify(newsletterRepository, times(1)).findNewsLetterListByNameOrCategoryId(
			any(String.class), nullable(UUID.class));
		assertEquals(1, searchList.newsletterList().size());
	}

	@Test
	public void searchNewsletterListByNameOrCategoryId_Invalid_CategoryId_throwException() {
		// When Then
		assertThrows(UuidConvertFailException.class,
			() -> newsletterService.searchNewsletterListByNameOrCategoryId(
				"test", "wrong uuid"));
	}

	@Test
	public void getNewsletterById() {
		// Given
		NewsletterEntityDto newsletter = NewsletterFixture.createEntityWithId().toEntityDto();
		when(newsletterRepository.getById(any(UUID.class)))
			.thenReturn(newsletter);

		// When
		NewsletterInfoResponse newsletterInfoResponse = newsletterService.getNewsLetterById(
			String.valueOf(newsletter.getId()));

		// Then
		verify(newsletterRepository, times(1)).getById(any(UUID.class));
		assertEquals(newsletterInfoResponse.id(), newsletter.getId().toString());
	}

	@DisplayName("뉴스레터 목록에서 특정 개수만큼 랜덤 추출하는 메서드 단위 테스트, 특정 퍼센트 이하로 평균 일치율을 달성하는지 검증")
	@Test
	public void shuffled_newsletterList_has_low_equals() {
		//Given
		UUID categoryId1 = UUID.randomUUID();
		UUID categoryId2 = UUID.randomUUID();
		UUID categoryId3 = UUID.randomUUID();
		List<UUID> userCategoryIdList = Arrays.asList(categoryId1, categoryId2, categoryId3);
		List<NewsletterEntity> newsletterEntityList = new ArrayList<>();
		// 한 카테고리 당 10개의 뉴스레터 * 3 => 총 30개의 뉴스레터가 존재한다고 가정
		for (UUID id : userCategoryIdList) {
			for (int i=0; i<5; i++) {
				createNewsletterByCategoryId(newsletterEntityList, id);
			}
		}
		List<Newsletter> newsletterList = newsletterEntityList.stream()
			.map(NewsletterEntity::toEntityDto)
			.map(NewsletterEntityDto::toDomain)
			.toList();

		int iterations = 10; // 랜덤 시행 10번
		int count = 4;
		List<Newsletter> prev = null;
		List<Double> overlapRatios = new ArrayList<>();

		for (int i = 0; i < iterations; i++) {
			List<Newsletter> current = newsletterService.getRandomNewsletterList(newsletterList,
				count);

			if (prev != null) {
				long overlap = current.stream()
					.filter(prev::contains)
					.count();

				double overlapRatio = (double)overlap / count;
				overlapRatios.add(overlapRatio);
			}
			prev = current;
		}

		double average = overlapRatios.stream().mapToDouble(d -> d).average().orElse(0);
		assertThat(average).isLessThan(0.3);
	}

	@DisplayName("광고 뉴스레터 수가 1개일때(1번 카테고리의 다른 뉴스레터라 가정), 유저 카테고리에 해당되는 랜덤 뉴스레터 3개와 1개의 광고 뉴스레터가 전달되어야 한다.")
	@Test
	public void recommend_newsletter_list_expected_recommend_number() {
		// Given
		UserEntity user = UserEntity.create(UUID.randomUUID(), "email", "nickname",
			UserEntityStatus.ACTIVE.name());
		UUID categoryId1 = UUID.randomUUID();
		UUID categoryId2 = UUID.randomUUID();
		UUID categoryId3 = UUID.randomUUID();
		List<UUID> userCategoryIdList = Arrays.asList(categoryId1, categoryId2, categoryId3);
		List<NewsletterEntity> newsletterList = new ArrayList<>();
		for (UUID id : userCategoryIdList) {
			createNewsletterByCategoryId(newsletterList, id);
		}
		List<CategoryEntity> categoryList = newsletterList.stream()
			.map(NewsletterEntity::getCategory)
			.toList();
		List<UserCategoryEntityDto> userCategoryList = createUserCategoryList(categoryList, user);
		NewsletterEntity advertiseNewsletter = createNewsletterWithName("advertise_newsletter",
			categoryList.get(0));
		AdvertiseEntity advertise = AdvertiseEntity.create(UUID.randomUUID(), advertiseNewsletter.getId());
		List<AdvertiseEntity> advertiseList = Collections.singletonList(advertise);

		when(userCategoryRepository.getUserCategoryListByUserId(any(UUID.class)))
			.thenReturn(userCategoryList);
		when(newsletterRepository.getNewsLetterListByCategoryIdList(
			any(List.class))).thenReturn(
			newsletterList.stream().map(NewsletterEntity::toEntityDto).toList());
		when(advertiseRepository.getAdvertiseNewsletterIdList())
			.thenReturn(advertiseList);
		when(newsletterRepository.getById(any(UUID.class)))
			.thenReturn(advertiseNewsletter.toEntityDto());
		// When
		NewsletterRecommendResponse newsletterRecommendResponse = newsletterService.recommendNewsletterList(
			user.getId());

		// Then
		assertThat(newsletterRecommendResponse.newletterRecommendList()).hasSize(4);
		List<String> newsletterName = newsletterRecommendResponse.newletterRecommendList().stream()
			.map(NewsletterResponse::name)
			.toList();
		assertThat(newsletterName)
			.filteredOn(name -> name.equals("advertise_newsletter"))
			.hasSize(1);
		assertThat(newsletterName)
			.filteredOn(name -> name.equals("usercategory_newsletter"))
			.hasSize(3);
	}

	private List<UserCategoryEntityDto> createUserCategoryList(List<CategoryEntity> categoryList,
		UserEntity user) {
		List<UserCategoryEntityDto> userCategoryList = new ArrayList<>();
		for (CategoryEntity category : categoryList) {
			userCategoryList.add(UserCategoryEntityDto.create(UUID.randomUUID(), user.toEntityDto(),
				category.toEntityDto()));
		}
		return userCategoryList;
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

	private void verifyValue(Newsletter newsletter) {
		assertEquals(name, newsletter.getName());
		assertEquals(domain, newsletter.getDomain());
		assertEquals(mailingList, newsletter.getMailingList());
	}
}
