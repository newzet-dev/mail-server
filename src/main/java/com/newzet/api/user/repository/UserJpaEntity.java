package com.newzet.api.user.repository;

import com.newzet.api.user.domain.UserStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserJpaEntity {
	@Id
	@GeneratedValue
	private Long id;

	@Column(unique = true)
	private String email;

	@Enumerated(EnumType.STRING)
	private UserStatus status;

	public UserJpaEntity(String email, UserStatus status) {
		this.email = email;
		this.status = status;
	}
}
