package com.newzet.api.newsletter.business;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class NewsletterCacheDto {
	private final Long id;
	private final String name;
	private final String domain;
	private final String mailingList;
	private final String status;

	public static NewsletterCacheDto create(Long id, String name, String domain, String mailingList,
		String status) {
		return NewsletterCacheDto.builder()
			.id(id)
			.name(name)
			.domain(domain)
			.mailingList(mailingList)
			.status(status)
			.build();
	}
}
