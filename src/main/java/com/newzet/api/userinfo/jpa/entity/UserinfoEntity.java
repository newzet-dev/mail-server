package com.newzet.api.userinfo.jpa.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "userinfo")
public class UserinfoEntity {
	@Id
	@UuidGenerator
	@Column(columnDefinition = "UUID", updatable = false, nullable = false)
	private UUID id;

	@Column(nullable = false)
	private String email;

	@Column(nullable = false)
	private String nickname;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserinfoEntityRole role;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	@Column
	private LocalDateTime deletedAt;
}
