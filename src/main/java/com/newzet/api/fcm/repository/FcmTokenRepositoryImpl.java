package com.newzet.api.fcm.repository;

import com.newzet.api.fcm.business.FcmTokenRepository;
import com.newzet.api.fcm.domain.FcmToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FcmTokenRepositoryImpl implements FcmTokenRepository {

    private final FcmTokenJpaRepository fcmTokenJpaRepository;

    @Override
    public Optional<FcmToken> findByTokenValue(String value) {
        return fcmTokenJpaRepository.findByFcmToken(value)
                .map(entity -> {
                    return FcmToken.fromEntity(
                            entity.getId(),
                            entity.getUserId(),
                            entity.getFcmToken(),
                            entity.getCreatedAt()
                    );
                });
    }

    @Override
    public boolean deleteFcmToken(UUID userId, String value) {
        return fcmTokenJpaRepository.findByUserIdAndFcmToken(userId, value)
                .map(entity -> {
                    fcmTokenJpaRepository.delete(entity);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public FcmToken save(FcmToken fcmToken) {
        FcmTokenEntity fcmTokenEntity = FcmTokenEntity.create(
                fcmToken.getId(),
                fcmToken.getUserId(),
                fcmToken.getValue(),
                fcmToken.getCreatedAt()
        );
        FcmTokenEntity savedFcmTokenEntity = fcmTokenJpaRepository.save(fcmTokenEntity);
        return FcmToken.fromEntity(
                savedFcmTokenEntity.getId(),
                savedFcmTokenEntity.getUserId(),
                savedFcmTokenEntity.getFcmToken(),
                savedFcmTokenEntity.getCreatedAt()
        );
    }
}
