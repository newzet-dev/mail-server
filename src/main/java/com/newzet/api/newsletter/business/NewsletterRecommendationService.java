package com.newzet.api.newsletter.business;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.newzet.api.advertise.business.AdvertiseRepository;
import com.newzet.api.newsletter.business.dto.NewsletterEntityDto;
import com.newzet.api.newsletter.domain.Newsletter;
import com.newzet.api.usercategory.business.UserCategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NewsletterRecommendationService {

	private final NewsletterRepository newsletterRepository;
	private final UserCategoryRepository userCategoryRepository;
	private final AdvertiseRepository advertiseRepository;
	private final NewsletterRecommender newsletterRecommender;

	public List<Newsletter> createRandomNewsletterList(UUID userId) {
		List<Newsletter> advertiseNewsletterList = prepareAdvertiseNewsletterList();
		List<Newsletter> userCategoryNewsletterList = prepareUserCategoryNewsletterList(userId);
		return newsletterRecommender.recommendNewsletterList(advertiseNewsletterList,
			userCategoryNewsletterList);
	}

	private List<Newsletter> prepareUserCategoryNewsletterList(UUID userId) {
		List<UUID> userCategoryIdList = userCategoryRepository.getUserCategoryListByUserId(
				userId).stream()
			.map(userCategoryEntityDto -> userCategoryEntityDto.getCategory().getId())
			.toList();

		return newsletterRepository.getNewsLetterListByCategoryIdList(
				userCategoryIdList).stream()
			.map(NewsletterEntityDto::toDomain)
			.toList();
	}

	private List<Newsletter> prepareAdvertiseNewsletterList() {
		return advertiseRepository.getAllAdvertise()
			.stream()
			.map(advertiseEntityDto -> newsletterRepository.getById(
				advertiseEntityDto.getNewsletterId()))
			.map(NewsletterEntityDto::toDomain)
			.toList();
	}

}
