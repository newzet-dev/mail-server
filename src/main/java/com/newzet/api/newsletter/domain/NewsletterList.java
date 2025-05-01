package com.newzet.api.newsletter.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class NewsletterList {
	private final List<Newsletter> newsletterList;

	public static NewsletterList create(List<Newsletter> newsletterList) {
		return new NewsletterList(newsletterList);
	}

	public NewsletterList getNewsletterSubListByRandom(int count) {
		List<Newsletter> randomNewsletterList = new ArrayList<>(newsletterList);
		Collections.shuffle(randomNewsletterList);
		return NewsletterList.create(randomNewsletterList.subList(0, count));
	}



}
