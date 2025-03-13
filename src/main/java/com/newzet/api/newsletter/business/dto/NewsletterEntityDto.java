package com.newzet.api.newsletter.business.dto;

import com.newzet.api.newsletter.domain.Newsletter;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class NewsletterEntityDto {
	private final Long id;
	private final String name;
	private final String domain;
	private final String mailingList;
	private final String status;

	public static NewsletterEntityDto create(Long id, String name, String domain,
		String mailingList, String status) {
		return NewsletterEntityDto.builder()
			.id(id)
			.name(name)
			.domain(domain)
			.mailingList(mailingList)
			.status(status)
			.build();
	}

	public Newsletter toDomain() {
		return Newsletter.create(id, name, domain, mailingList, status);
	}
}
