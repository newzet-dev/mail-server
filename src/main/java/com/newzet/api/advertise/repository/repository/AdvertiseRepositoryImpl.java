package com.newzet.api.advertise.repository.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.newzet.api.advertise.business.AdvertiseRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AdvertiseRepositoryImpl implements AdvertiseRepository {

	private final AdvertiseJpaRepository advertiseJpaRepository;

	@Override
	public List<UUID> getAdvertiseNewsletterIdList() {
		return advertiseJpaRepository.findAll().stream()
			.map(advertiseEntity -> advertiseEntity.getNewsletter().getId())
			.toList();
	}
}
