package com.newzet.api.userinfo.jpa.mapper;

import com.newzet.api.userinfo.domain.UserRole;
import com.newzet.api.userinfo.domain.Userinfo;
import com.newzet.api.userinfo.jpa.entity.UserinfoEntity;

public class UserinfoEntityMapper {
	public static Userinfo toDomain(UserinfoEntity entity) {
		return new Userinfo(entity.getId(), entity.getEmail(), entity.getNickname(),
			UserRole.valueOf(entity.getRole().name()),
			entity.getCreatedAt(), entity.getDeletedAt());
	}

	public static UserinfoEntity toEntity(Userinfo domain) {
		return new UserinfoEntity(domain.getId(), domain.getEmail(), domain.getNickname(), domain.getRole(),
			domain.getCreatedAt(),
			domain.getDeletedAt());
	}
}
