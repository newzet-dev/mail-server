package com.newzet.api.fcm.jpa.repository;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.newzet.api.fcm.domain.FcmToken;
import com.newzet.api.fcm.exception.NoFcmTokenException;
import com.newzet.api.fcm.jpa.entity.FcmTokenEntity;

@ExtendWith(MockitoExtension.class)
class FcmTokenRepositoryTest {

	@Mock
	private FcmTokenJpaRepository fcmTokenJpaRepository;

	@InjectMocks
	private FcmTokenRepositoryImpl fcmTokenRepository;

	private UUID testUserId;
	private String testFcmToken;
	private FcmTokenEntity testEntity;
	private FcmToken testDomain;

	@BeforeEach
	void setUp() {
		testUserId = UUID.randomUUID();
		testFcmToken = "test-fcm-token-value";
		testEntity = new FcmTokenEntity(UUID.randomUUID(), testUserId, testFcmToken,
			LocalDateTime.now());
		testDomain = new FcmToken(testEntity.getId(), testEntity.getCreatedAt(), testUserId,
			testFcmToken);
	}

	@Test
	void findIfExistByValue_WhenTokenExists_ThenReturnFcmToken() {
		// Given
		when(fcmTokenJpaRepository.findByFcmToken(testFcmToken)).thenReturn(
			Optional.of(testEntity));

		// When
		Optional<FcmToken> result = fcmTokenRepository.findIfExistByValue(testFcmToken);

		// Then
		assertThat(result).isPresent();
		assertThat(result.get().value()).isEqualTo(testFcmToken);
		assertThat(result.get().userId()).isEqualTo(testUserId);
	}

	@Test
	void findIfExistByValue_WhenTokenNotExists_ThenReturnEmpty() {
		// Given
		when(fcmTokenJpaRepository.findByFcmToken(testFcmToken)).thenReturn(Optional.empty());

		// When
		Optional<FcmToken> result = fcmTokenRepository.findIfExistByValue(testFcmToken);

		// Then
		assertThat(result).isEmpty();
	}

	@Test
	void deleteFcmToken_WhenCalled_ThenReturnTrue() {
		// Given
		doNothing().when(fcmTokenJpaRepository).delete(any(FcmTokenEntity.class));

		// When
		boolean result = fcmTokenRepository.deleteFcmToken(testDomain);

		// Then
		assertThat(result).isTrue();
		verify(fcmTokenJpaRepository).delete(any(FcmTokenEntity.class));
	}

	@Test
	void save_WhenCalled_ThenReturnSavedFcmToken() {
		// Given
		when(fcmTokenJpaRepository.save(any(FcmTokenEntity.class))).thenReturn(testEntity);

		// When
		FcmToken result = fcmTokenRepository.save(testDomain);

		// Then
		assertThat(result.value()).isEqualTo(testFcmToken);
		assertThat(result.userId()).isEqualTo(testUserId);
		verify(fcmTokenJpaRepository).save(any(FcmTokenEntity.class));
	}

	@Test
	void findByUserIdAndValue_WhenTokenExists_ThenReturnFcmToken() {
		// Given
		when(fcmTokenJpaRepository.findByUserIdAndFcmToken(testUserId, testFcmToken))
			.thenReturn(Optional.of(testEntity));

		// When
		FcmToken result = fcmTokenRepository.findByUserIdAndValue(testUserId, testFcmToken);

		// Then
		assertThat(result.value()).isEqualTo(testFcmToken);
		assertThat(result.userId()).isEqualTo(testUserId);
	}

	@Test
	void findByUserIdAndValue_WhenTokenNotExists_ThenThrowException() {
		// Given
		when(fcmTokenJpaRepository.findByUserIdAndFcmToken(testUserId, testFcmToken))
			.thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> fcmTokenRepository.findByUserIdAndValue(testUserId, testFcmToken))
			.isInstanceOf(NoFcmTokenException.class)
			.hasMessageContaining("FCM Token이 존재하지 않습니다");
	}

	@Test
	void findAllByUserId_WhenTokensExist_ThenReturnTokenList() {
		// Given
		FcmTokenEntity entity1 = new FcmTokenEntity(UUID.randomUUID(), testUserId, "token1",
			LocalDateTime.now());
		FcmTokenEntity entity2 = new FcmTokenEntity(UUID.randomUUID(), testUserId, "token2",
			LocalDateTime.now());
		when(fcmTokenJpaRepository.findAllByUserId(testUserId)).thenReturn(
			List.of(entity1, entity2));

		// When
		List<FcmToken> result = fcmTokenRepository.findAllByUserId(testUserId);

		// Then
		assertThat(result).hasSize(2);
		assertThat(result.get(0).value()).isEqualTo("token1");
		assertThat(result.get(1).value()).isEqualTo("token2");
		assertThat(result).allMatch(token -> token.userId().equals(testUserId));
	}

	@Test
	void findAllByUserId_WhenNoTokens_ThenReturnEmptyList() {
		// Given
		when(fcmTokenJpaRepository.findAllByUserId(testUserId)).thenReturn(List.of());

		// When
		List<FcmToken> result = fcmTokenRepository.findAllByUserId(testUserId);

		// Then
		assertThat(result).isEmpty();
	}
}
