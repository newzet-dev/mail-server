package com.newzet.api.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
public class UserJpaEntity {
	@Column(unique = true)
	private String email;

	@Enumerated(EnumType.STRING)
	private UserStatus status;
}
