package com.newzet.api.category.repository;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.newzet.api.category.business.dto.CategoryEntityDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PROTECTED)
@Table(name = "CATEGORY")
@Getter
public class CategoryEntity {
	@Id
	@UuidGenerator
	@Column(columnDefinition = "uuid", updatable = false, nullable = false)
	private UUID id;

	private String name;

	private String imageUrl;

	private String emoji;

	public static CategoryEntity create(String name, String imageUrl, String emoji) {
		return CategoryEntity.builder()
			.name(name)
			.imageUrl(imageUrl)
			.emoji(emoji)
			.build();
	}

	public static CategoryEntity create(UUID id, String name, String imageUrl, String emoji) {
		return CategoryEntity.builder()
			.id(id)
			.name(name)
			.imageUrl(imageUrl)
			.emoji(emoji)
			.build();
	}

	public CategoryEntityDto toEntityDto() {
		return CategoryEntityDto.create(id, name, imageUrl, emoji);
	}

}
