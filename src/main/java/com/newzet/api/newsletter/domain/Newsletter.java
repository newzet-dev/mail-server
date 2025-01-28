package com.newzet.api.newsletter.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Newsletter {
	private final Long id;
	private final String name;
	private final String domain;
	private final String maillingList;
	private final NewsletterStatus status;

	public static Newsletter create(Long id, String name, String domain, String maillingList,
		NewsletterStatus status) {
		return new Newsletter(id, name, domain, maillingList, status);
	}
}
