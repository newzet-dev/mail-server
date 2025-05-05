package com.newzet.api.auth.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.newzet.api.auth.exception.OAuthBadRequestException;

class DeviceTypeTest {

	@Test
	void fromString_WithWebString_ShouldReturnWebType() {
		// Given
		String value = "web";

		// When
		DeviceType result = DeviceType.fromString(value);

		// Then
		assertThat(result).isEqualTo(DeviceType.WEB);
	}

	@Test
	void fromString_WithAppString_ShouldReturnAppType() {
		// Given
		String value = "app";

		// When
		DeviceType result = DeviceType.fromString(value);

		// Then
		assertThat(result).isEqualTo(DeviceType.APP);
	}

	@Test
	void fromString_WithMixedCaseString_ShouldBeCaseInsensitive() {
		// Given
		String value = "ApP";

		// When
		DeviceType result = DeviceType.fromString(value);

		// Then
		assertThat(result).isEqualTo(DeviceType.APP);
	}

	@Test
	void fromString_WithNullString_ShouldThrowException() {
		// When, Then
		assertThatThrownBy(() -> DeviceType.fromString(null))
			.isInstanceOf(OAuthBadRequestException.class)
			.hasMessage("state값이 없습니다.");
	}

	@Test
	void fromString_WithEmptyString_ShouldThrowException() {
		// Given
		String value = "";

		// When, Then
		assertThatThrownBy(() -> DeviceType.fromString(value))
			.isInstanceOf(OAuthBadRequestException.class)
			.hasMessage("state값이 없습니다.");
	}

	@ParameterizedTest
	@ValueSource(strings = {"mobile", "tablet", "desktop", "other"})
	void fromString_WithUnsupportedValue_ShouldThrowException(String value) {
		// When, Then
		assertThatThrownBy(() -> DeviceType.fromString(value))
			.isInstanceOf(OAuthBadRequestException.class)
			.hasMessage("올바른 DeviceType이 아닙니다.");
	}
}
