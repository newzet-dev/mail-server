package com.newzet.api.advertise.business.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.newzet.api.advertise.business.repository.AdvertiseRepository;
import com.newzet.api.advertise.domain.Advertise;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdvertiseService {

	private final AdvertiseRepository advertiseRepository;

	public List<Advertise> getAdvertiseList() {
		return advertiseRepository.getAllAdvertise();
	}
}
