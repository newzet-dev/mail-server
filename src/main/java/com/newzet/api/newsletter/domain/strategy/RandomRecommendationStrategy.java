package com.newzet.api.newsletter.domain.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.newzet.api.newsletter.domain.model.Newsletter;

@Component
public class RandomRecommendationStrategy implements RecommendationStrategy {

	public List<Newsletter> createRecommendationList(List<Newsletter> candidateList,
		int count) {
		List<Newsletter> randomNewsletterList = new ArrayList<>(candidateList);
		Collections.shuffle(randomNewsletterList);
		return randomNewsletterList.subList(0, count);
	}
}
