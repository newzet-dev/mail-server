package com.newzet.api.newsletter.recommend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.newzet.api.newsletter.domain.Newsletter;

@Component
public class RandomRecommendationFactory implements RecommendationFactory {

	public List<Newsletter> createRecommendationList(List<Newsletter> candidateList,
		int count) {
		List<Newsletter> randomNewsletterList = new ArrayList<>(candidateList);
		Collections.shuffle(randomNewsletterList);
		return randomNewsletterList.subList(0, count);
	}
}
