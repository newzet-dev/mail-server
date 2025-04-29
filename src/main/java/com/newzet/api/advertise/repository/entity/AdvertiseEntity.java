package com.newzet.api.advertise.repository.entity;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.newzet.api.advertise.business.dto.AdvertiseEntityDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdvertiseEntity {
	@Id
	@UuidGenerator
	@Column(columnDefinition = "uuid", updatable = false, nullable = false)
	private UUID id;

	private UUID newsletterId;

	public static AdvertiseEntity create(UUID newsletterId) {
		return new AdvertiseEntity(null, newsletterId);
	}

	public static AdvertiseEntity create(UUID id, UUID newsletterId) {
		return new AdvertiseEntity(id, newsletterId);
	}

	public AdvertiseEntityDto toEntityDto() {
		return AdvertiseEntityDto.create(id, newsletterId);
	}
}
