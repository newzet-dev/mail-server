package com.newzet.api.auth.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.newzet.api.auth.exception.OAuthBadRequestException;

class OAuthProviderTest {

	@Test
	void fromString_WithKakaoString_ShouldReturnKakaoProvider() {
		// Given
		String value = "kakao";

		// When
		OAuthProvider result = OAuthProvider.fromString(value);

		// Then
		assertThat(result).isEqualTo(OAuthProvider.KAKAO);
	}

	@Test
	void fromString_WithAppleString_ShouldReturnAppleProvider() {
		// Given
		String value = "apple";

		// When
		OAuthProvider result = OAuthProvider.fromString(value);

		// Then
		assertThat(result).isEqualTo(OAuthProvider.APPLE);
	}

	@Test
	void fromString_WithMixedCase_ShouldBeCaseInsensitive() {
		// Given
		String value = "KaKaO";

		// When
		OAuthProvider result = OAuthProvider.fromString(value);

		// Then
		assertThat(result).isEqualTo(OAuthProvider.KAKAO);
	}

	@Test
	void fromString_WithNull_ShouldThrowException() {
		// When, Then
		assertThatThrownBy(() -> OAuthProvider.fromString(null))
			.isInstanceOf(OAuthBadRequestException.class)
			.hasMessage("Provider 값이 없습니다.");
	}

	@Test
	void fromString_WithEmpty_ShouldThrowException() {
		// When, Then
		assertThatThrownBy(() -> OAuthProvider.fromString(""))
			.isInstanceOf(OAuthBadRequestException.class)
			.hasMessage("Provider 값이 없습니다.");
	}

	@ParameterizedTest
	@ValueSource(strings = {"google", "facebook", "naver", "linkedin"})
	void fromString_WithUnsupportedValue_ShouldThrowException(String value) {
		// When, Then
		assertThatThrownBy(() -> OAuthProvider.fromString(value))
			.isInstanceOf(OAuthBadRequestException.class)
			.hasMessage("올바른 Provider가 아닙니다.");
	}
}
