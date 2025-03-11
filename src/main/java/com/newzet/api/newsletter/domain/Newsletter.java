package com.newzet.api.newsletter.domain;

import com.newzet.api.newsletter.business.dto.NewsletterCacheDto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Newsletter {
	private final Long id;
	private final String name;
	private final String domain;
	private final String mailingList;
	private final NewsletterStatus status;

	public static Newsletter create(Long id, String name, String domain, String mailingList,
		String status) {
		return new Newsletter(id, name, domain, mailingList, NewsletterStatus.valueOf(status));
	}

	public NewsletterCacheDto toCacheDto() {
		return NewsletterCacheDto.create(id, name, domain, mailingList, status.name());
	}
}
