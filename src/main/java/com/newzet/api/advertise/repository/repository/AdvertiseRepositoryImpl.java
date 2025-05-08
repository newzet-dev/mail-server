package com.newzet.api.advertise.repository.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.newzet.api.advertise.business.AdvertiseRepository;
import com.newzet.api.advertise.business.dto.AdvertiseEntityDto;
import com.newzet.api.advertise.repository.entity.AdvertiseEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AdvertiseRepositoryImpl implements AdvertiseRepository {

	private final AdvertiseJpaRepository advertiseJpaRepository;

	@Override
	public List<AdvertiseEntityDto> getAllAdvertise() {
		return advertiseJpaRepository.findAll().stream()
			.map(AdvertiseEntity::toEntityDto)
			.toList();
	}
}
