package com.newzet.api.usercategory.repository.entity;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.newzet.api.category.repository.CategoryEntity;
import com.newzet.api.user.repository.entity.UserEntity;
import com.newzet.api.usercategory.business.dto.UserCategoryEntityDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="user_category")
public class UserCategoryEntity {
	@Id
	@UuidGenerator
	@Column(columnDefinition = "uuid", updatable = false, nullable = false)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private UserEntity user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	private CategoryEntity category;

	public static UserCategoryEntity create(UserEntity user, CategoryEntity category) {
		return new UserCategoryEntity(null, user, category);
	}

	public UserCategoryEntityDto toEntityDto() {
		return UserCategoryEntityDto.create(id, user.toEntityDto(), category.toEntityDto());
	}

}
