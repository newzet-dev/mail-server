package com.newzet.api.newsletter.domain.strategy;

import java.util.List;

import com.newzet.api.newsletter.domain.model.Newsletter;

public interface RecommendationStrategy {

	List<Newsletter> createRecommendationList(List<Newsletter> candidateList, int count);
}
