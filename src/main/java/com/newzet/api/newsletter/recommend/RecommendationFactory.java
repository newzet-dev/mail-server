package com.newzet.api.newsletter.recommend;

import java.util.List;

import com.newzet.api.newsletter.domain.Newsletter;

public interface RecommendationFactory {

	List<Newsletter> createRecommendationList(List<Newsletter> candidateList, int count);
}
