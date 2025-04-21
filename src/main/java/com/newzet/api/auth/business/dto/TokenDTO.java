package com.newzet.api.auth.business.dto;

import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record TokenDTO(
	String value
) {
	public static TokenDTO of(String value) {
		return TokenDTO.builder().value(value).build();
	}
}
