package com.newzet.api.newsletter.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.newzet.api.newsletter.business.exception.NotEnoughNewslettersException;
import com.newzet.api.newsletter.domain.Newsletter;
import com.newzet.api.newsletter.domain.RecommendationPolicy;

public class NewsletterRecommender {

	public static List<Newsletter> recommendNewsletterList(List<Newsletter> advertiseNewsletterList,
		List<Newsletter> userCategoryNewsletterList) {

		List<Newsletter> recommendedNewsletterList = new ArrayList<>();
		if (RecommendationPolicy.ADVERTISE_INCLUDES_ALL) {
			recommendedNewsletterList.addAll(advertiseNewsletterList);
		}

		int resQuarter = getRemainingQuarter(advertiseNewsletterList.size(),
			userCategoryNewsletterList.size());

		List<Newsletter> userCategoryRecommendationNewsletterList = createRecommendationList(
			userCategoryNewsletterList, resQuarter);
		recommendedNewsletterList.addAll(userCategoryRecommendationNewsletterList);

		return recommendedNewsletterList;
	}

	private static int getRemainingQuarter(int filledQuarter, int size) {
		int resQuarter = RecommendationPolicy.RECOMMENDATION_QUARTER_SIZE - filledQuarter;
		if (size < resQuarter) {
			throw new NotEnoughNewslettersException("추천할 뉴스레터 count 수보다 존재하는 뉴스레터 수가 적습니다.");
		}
		return resQuarter;
	}

	private static List<Newsletter> createRecommendationList(List<Newsletter> candidateList,
		int count) {
		List<Newsletter> randomNewsletterList = new ArrayList<>(candidateList);
		Collections.shuffle(randomNewsletterList);
		return randomNewsletterList.subList(0, count);
	}

}
