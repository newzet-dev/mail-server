package com.newzet.api.fcm.business.service;

import static java.util.stream.Collectors.*;

import java.util.List;
import java.util.UUID;

import com.newzet.api.fcm.business.FcmTokenRepository;
import com.newzet.api.fcm.domain.FcmToken;
import org.springframework.stereotype.Service;

import com.newzet.api.category.business.dto.CategoryEntityDto;
import com.newzet.api.category.controller.dto.CategoryListResponse;
import com.newzet.api.category.controller.dto.CategoryResponse;
import com.newzet.api.category.domain.Category;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FcmTokenService {

	private final FcmTokenRepository fcmTokenRepository;

	public Boolean createFcmToken(UUID userId, String value) {
		fcmTokenRepository.create(userId, value);
		return true;
	}
}
