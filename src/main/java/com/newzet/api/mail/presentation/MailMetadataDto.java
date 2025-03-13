package com.newzet.api.mail.presentation;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MailMetadataDto {
	private final String fromName;
	private final String fromDomain;
	private final String toDomain;
	private final String mailingList;
	private final String htmlLink;
}
