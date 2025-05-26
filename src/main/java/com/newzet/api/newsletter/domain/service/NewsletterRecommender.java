package com.newzet.api.newsletter.domain.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.newzet.api.common.logging.LogExecution;
import com.newzet.api.newsletter.business.exception.NotEnoughNewslettersException;
import com.newzet.api.newsletter.domain.model.Newsletter;
import com.newzet.api.newsletter.domain.strategy.RecommendationStrategy;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NewsletterRecommender {
	public static final int RECOMMENDATION_QUARTER_SIZE = 4;
	public static final boolean ADVERTISE_INCLUDED_IN = true; // 정책 상 모든 Advertise Newsletter는 Recommendation List에 포함된다.
	private final RecommendationStrategy recommendationStrategy;

	public List<Newsletter> recommendNewsletterList(List<Newsletter> advertiseNewsletterList,
		List<Newsletter> userCategoryNewsletterList) {

		List<Newsletter> recommendedNewsletterList = new ArrayList<>();
		if (ADVERTISE_INCLUDED_IN) {
			recommendedNewsletterList.addAll(advertiseNewsletterList);
		}

		int resQuarter = getRemainingQuarter(advertiseNewsletterList.size(),
			userCategoryNewsletterList.size());

		List<Newsletter> userCategoryRecommendationNewsletterList = recommendationStrategy.createRecommendationList(
			userCategoryNewsletterList, resQuarter);
		recommendedNewsletterList.addAll(userCategoryRecommendationNewsletterList);

		return recommendedNewsletterList;
	}

	@LogExecution
	private int getRemainingQuarter(int filledQuarter, int size) {
		int resQuarter = RECOMMENDATION_QUARTER_SIZE - filledQuarter;
		if (size < resQuarter) {
			throw new NotEnoughNewslettersException("추천할 뉴스레터 count 수보다 존재하는 뉴스레터 수가 적습니다.");
		}
		return resQuarter;
	}


}
