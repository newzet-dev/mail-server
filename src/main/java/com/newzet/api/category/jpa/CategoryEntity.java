package com.newzet.api.category.jpa;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

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
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PROTECTED)
@Table(name = "CATEGORY")
@Getter
public class CategoryEntity {
	@Id
	@UuidGenerator
	@Column(columnDefinition = "uuid", updatable = false, nullable = false)
	private UUID id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String imageUrl;

	@Column(nullable = false)
	private String emoji;
}
