package com.newzet.api.fcm.jpa.mapper;

import com.newzet.api.fcm.domain.FcmToken;
import com.newzet.api.fcm.jpa.entity.FcmTokenEntity;

public class FcmTokenMapper {
	public static FcmToken toDomain(FcmTokenEntity entity) {
		return new FcmToken(
			entity.getId(),
			entity.getCreatedAt(),
			entity.getUserId(),
			entity.getFcmToken()
		);
	}

	public static FcmTokenEntity toEntity(FcmToken domain) {
		return new FcmTokenEntity(
			domain.id(),
			domain.userId(),
			domain.value(),
			domain.createdAt()
		);
	}
}
