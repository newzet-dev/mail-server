package com.newzet.api.userinfo.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public record Userinfo(
	UUID id,
	String email,
	LocalDateTime createdAt,
	String nickname,
	UserRole role,
	LocalDateTime deletedAt
) {
}
