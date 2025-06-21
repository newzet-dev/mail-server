package com.newzet.api.userinfo.jpa.entity;

import com.newzet.api.userinfo.domain.UserRole;
import com.newzet.api.userinfo.domain.Userinfo;

public class UserinfoEntityMapper {
	public static Userinfo toDomain(UserinfoEntity entity) {
		return new Userinfo(entity.getId(), entity.getEmail(), entity.getNickname(),
			UserRole.valueOf(entity.getRole().name()),
			entity.getCreatedAt(), entity.getDeletedAt());
	}

	public static UserinfoEntity toEntity(Userinfo domain) {
		return new UserinfoEntity(domain.id(), domain.email(), domain.nickname(), domain.role(), domain.createdAt(),
			domain.deletedAt());
	}
}
