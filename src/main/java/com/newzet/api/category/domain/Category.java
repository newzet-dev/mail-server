package com.newzet.api.category.domain;

import java.util.UUID;

import com.newzet.api.category.business.dto.CategoryCacheDto;
import com.newzet.api.category.business.dto.CategoryEntityDto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Category {
	private final UUID id;
	private final String name;
	private final String imageUrl;
	private final String emoji;

	public static Category create(UUID id, String name, String imageUrl, String emoji) {
		return new Category(id, name, imageUrl, emoji);
	}

	public CategoryCacheDto toCacheDto() {
		return CategoryCacheDto.create(id, name, imageUrl, emoji);
	}

	public CategoryEntityDto toEntityDto() {
		return CategoryEntityDto.create(id, name, imageUrl, emoji);
	}

}
