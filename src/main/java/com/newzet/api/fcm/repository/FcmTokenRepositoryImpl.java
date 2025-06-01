package com.newzet.api.fcm.repository;

import com.newzet.api.fcm.business.FcmTokenRepository;
import com.newzet.api.fcm.business.dto.FcmTokenEntityResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class FcmTokenRepositoryImpl implements FcmTokenRepository {

    private final FcmTokenJpaRepository fcmTokenJpaRepository;

    @Override
    public FcmTokenEntityResponse create(UUID userId, String value) {
        FcmTokenEntity fcmToken = FcmTokenEntity.create(userId, value);
        return fcmTokenJpaRepository.save(fcmToken).toResponseDto();
    }
}
