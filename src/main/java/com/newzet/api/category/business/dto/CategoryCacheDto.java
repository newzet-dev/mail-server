package com.newzet.api.category.business.dto;

import java.util.UUID;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PROTECTED)
@Getter
public class CategoryCacheDto {
	private final UUID id;
	private final String name;
	private final String imageUrl;
	private final String emoji;

	public static CategoryCacheDto create(UUID id, String name, String imageUrl, String emoji) {
		return CategoryCacheDto.builder()
			.id(id)
			.name(name)
			.imageUrl(imageUrl)
			.emoji(emoji)
			.build();
	}
}
