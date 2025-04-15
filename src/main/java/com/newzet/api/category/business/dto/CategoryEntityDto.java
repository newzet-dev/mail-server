package com.newzet.api.category.business.dto;

import java.util.UUID;

import com.newzet.api.category.domain.Category;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PROTECTED)
@Getter
public class CategoryEntityDto {
	private final UUID id;
	private final String name;
	private final String imageUrl;
	private final String emoji;

	public static CategoryEntityDto create(UUID id, String name, String imageUrl, String emoji) {
		return CategoryEntityDto.builder()
			.id(id)
			.name(name)
			.imageUrl(imageUrl)
			.emoji(emoji)
			.build();
	}

	public Category toDomain() {
		return Category.create(id, name, imageUrl, emoji);
	}

}
