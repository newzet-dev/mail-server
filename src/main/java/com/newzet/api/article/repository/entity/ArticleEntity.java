package com.newzet.api.article.repository.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.newzet.api.article.business.dto.ArticleEntityDto;

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
@Table(name = "article")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ArticleEntity {

	@Id
	@UuidGenerator
	@Column(columnDefinition = "UUID", updatable = false, nullable = false)
	private UUID id;

	@Column(name = "to_user_id", nullable = false)
	private UUID toUserId;

	@Column(name = "from_name", nullable = false)
	private String fromName;

	@Column(name = "from_domain", nullable = false)
	private String fromDomain;

	@Column(name = "mailing_list")
	private String mailingList;

	@Column(name = "title", nullable = false)
	private String title;

	@Column(name = "image_url", nullable = false)
	private String imageUrl;

	@Column(name = "content_url", nullable = false)
	private String contentUrl;

	@Column(name = "is_read", nullable = false)
	private boolean isRead;

	@Column(name = "is_like", nullable = false)
	private boolean isLike;

	@Column(name = "is_share", nullable = false)
	private boolean isShare;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	public static ArticleEntity fromEntityDto(ArticleEntityDto dto) {
		return ArticleEntity.builder()
			.id(dto.getId())
			.toUserId(dto.getToUserId())
			.fromName(dto.getFromName())
			.fromDomain(dto.getFromDomain())
			.mailingList(dto.getMailingList())
			.title(dto.getTitle())
			.imageUrl(dto.getImageUrl())
			.contentUrl(dto.getContentUrl())
			.isRead(dto.isRead())
			.isLike(dto.isLike())
			.isShare(dto.isShare())
			.createdAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : LocalDateTime.now())
			.deletedAt(dto.getDeletedAt())
			.build();
	}

	public ArticleEntityDto toEntityDto() {
		return ArticleEntityDto.builder()
			.id(id)
			.toUserId(toUserId)
			.fromName(fromName)
			.fromDomain(fromDomain)
			.mailingList(mailingList)
			.title(title)
			.contentUrl(contentUrl)
			.isRead(isRead)
			.isLike(isLike)
			.isShare(isShare)
			.createdAt(createdAt)
			.deletedAt(deletedAt)
			.build();
	}

	public void addImageUrlForSave(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public void readArticle() {
		this.isRead = true;
	}

	public void updateLike(boolean newLike) {
		this.isLike = newLike;
	}
}
