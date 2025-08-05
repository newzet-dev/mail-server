package com.newzet.api.advertise.repository.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.newzet.api.advertise.business.repository.AdvertiseRepository;
import com.newzet.api.advertise.domain.Advertise;
import com.newzet.api.advertise.repository.mapper.AdvertiseEntityMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AdvertiseRepositoryImpl implements AdvertiseRepository {

	private final AdvertiseJpaRepository advertiseJpaRepository;

	@Override
	public List<Advertise> getAllAdvertise() {
		return advertiseJpaRepository.findAll().stream()
			.map(AdvertiseEntityMapper::toDomain)
			.toList();
	}
}
