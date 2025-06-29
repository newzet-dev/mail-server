package com.newzet.api.fcm.jpa.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "fcm_tokens")
@Getter
public class FcmTokenEntity {
	@Id
	@UuidGenerator
	@Column(columnDefinition = "uuid", updatable = false, nullable = false)
	private UUID id;

	@Column(nullable = false)
	private UUID userId;

	@Column(nullable = false)
	private String fcmToken;

	private LocalDateTime createdAt;
}
