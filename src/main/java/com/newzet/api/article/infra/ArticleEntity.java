package com.newzet.api.article.infra;

import java.time.LocalDateTime;
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
@Table(name = "article")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ArticleEntity {

	@Id
	@UuidGenerator
	@Column(columnDefinition = "UUID", updatable = false, nullable = false)
	private UUID id;

	@Column(name = "to_user_id")
	private UUID toUserId;

	@Column(name = "from_name")
	private String fromName;

	@Column(name = "from_domain")
	private String fromDomain;

	@Column(name = "mailing_list")
	private String mailingList;

	@Column(name = "title")
	private String title;

	@Column(name = "content_url")
	private String contentUrl;

	@Column(name = "is_read")
	private boolean isRead;

	@Column(name = "is_like")
	private boolean isLike;

	@Column(name = "is_share")
	private boolean isShare;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "deleted_at")

	private LocalDateTime deletedAt;



}