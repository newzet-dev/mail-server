package com.newzet.api.welcome.jpa.mapper;

import com.newzet.api.welcome.domain.UserRole;
import com.newzet.api.welcome.domain.Userinfo;
import com.newzet.api.welcome.jpa.entity.UserinfoEntity;

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
