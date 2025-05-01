package com.newzet.api.newsletter.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.newzet.api.newsletter.business.exception.NotEnoughNewslettersException;
import com.newzet.api.newsletter.domain.Newsletter;
import com.newzet.api.newsletter.domain.RecommendationPolicy;

@Component
public class NewsletterRecommender {

	public List<Newsletter> recommendNewsletterList(List<Newsletter> advertiseNewsletterList,
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

	private int getRemainingQuarter(int filledQuarter, int size) {
		int resQuarter = RecommendationPolicy.RECOMMENDATION_QUARTER_SIZE - filledQuarter;
		if (size < resQuarter) {
			throw new NotEnoughNewslettersException("추천할 뉴스레터 count 수보다 존재하는 뉴스레터 수가 적습니다.");
		}
		return resQuarter;
	}

	private List<Newsletter> createRecommendationList(List<Newsletter> candidateList,
		int count) {
		List<Newsletter> randomNewsletterList = new ArrayList<>(candidateList);
		Collections.shuffle(randomNewsletterList);
		return randomNewsletterList.subList(0, count);
	}

}
