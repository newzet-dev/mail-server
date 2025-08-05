package com.newzet.api.newsletter.business.service;

import java.util.List;

import com.newzet.api.newsletter.domain.Newsletter;

public interface RecommendationStrategy {

	List<Newsletter> createRecommendationList(List<Newsletter> candidateList, int count);
}
