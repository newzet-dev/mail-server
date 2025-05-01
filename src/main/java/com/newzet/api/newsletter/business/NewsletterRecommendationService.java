package com.newzet.api.newsletter.business;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.newzet.api.advertise.business.AdvertiseRepository;
import com.newzet.api.newsletter.business.dto.NewsletterEntityDto;
import com.newzet.api.newsletter.business.exception.NotEnoughNewslettersException;
import com.newzet.api.newsletter.domain.Newsletter;
import com.newzet.api.newsletter.domain.NewsletterList;
import com.newzet.api.usercategory.business.UserCategoryRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class NewsletterRecommendationService {
	public static int RECOMMENDATION_QUARTER_SIZE = 4;

	private final NewsletterRepository newsletterRepository;
	private final UserCategoryRepository userCategoryRepository;
	private final AdvertiseRepository advertiseRepository;

	public NewsletterList createRandomNewsletterList(UUID userId) {
		NewsletterList advertiseRecommendNewsletterList = prepareAdvertiseNewsletterList();
		NewsletterList userCategoryNewsletterList = prepareUserCategoryNewsletterList(userId);

		int resRecommendCount = RECOMMENDATION_QUARTER_SIZE - advertiseRecommendNewsletterList.getNewsletterList().size();
		if (userCategoryNewsletterList.getNewsletterList().size() < resRecommendCount) {
			throw new NotEnoughNewslettersException("추천할 뉴스레터 count 수보다 존재하는 뉴스레터 수가 적습니다.");
		}

		NewsletterList userCategoryRecommendNewsletterList = userCategoryNewsletterList.getNewsletterSubListByRandom(
			resRecommendCount);
		return combineRecommendNewsletterList(advertiseRecommendNewsletterList,
			userCategoryRecommendNewsletterList);
	}

	private NewsletterList prepareUserCategoryNewsletterList(UUID userId) {
		List<UUID> userCategoryIdList = userCategoryRepository.getUserCategoryListByUserId(
				userId).stream()
			.map(userCategoryEntityDto -> userCategoryEntityDto.getCategory().getId())
			.toList();

		List<Newsletter> newsletterListInUserCategories = newsletterRepository.getNewsLetterListByCategoryIdList(
				userCategoryIdList).stream()
			.map(NewsletterEntityDto::toDomain)
			.toList();

		return NewsletterList.create(newsletterListInUserCategories);
	}

	private NewsletterList prepareAdvertiseNewsletterList() {
		List<Newsletter> advertiseNewsletterList = advertiseRepository.getAllAdvertise()
			.stream()
			.map(advertiseEntityDto -> newsletterRepository.getById(
				advertiseEntityDto.getNewsletterId()))
			.map(NewsletterEntityDto::toDomain)
			.toList();

		return NewsletterList.create(advertiseNewsletterList);
	}

	private NewsletterList combineRecommendNewsletterList(NewsletterList randomNewsletterList,
		NewsletterList advertiseNewsletterList) {
		return NewsletterList.create(Stream.of(advertiseNewsletterList.getNewsletterList(),
				randomNewsletterList.getNewsletterList())
			.flatMap(List::stream)
			.toList());
	}
}
