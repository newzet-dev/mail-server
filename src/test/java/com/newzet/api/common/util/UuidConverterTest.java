package com.newzet.api.common.util;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.newzet.api.common.util.exception.UuidConvertFailException;

class UuidConverterTest {

	private static final String uuid = UUID.randomUUID().toString();

	private static Stream<Arguments> invalidUuidString() {
		return Stream.of(
			null,
			Arguments.of(""),
			Arguments.of(" "),
			Arguments.of("invalid-uuid"),
			Arguments.of(uuid + " "),
			Arguments.of(" " + uuid),
			Arguments.of(uuid + "1"),
			Arguments.of(uuid + "a")
		);
	}

	@Test
	void success_on_valid_uuid_string() {
		UUID convertedUuid = UuidConverter.convert(uuid);

		assertEquals(uuid, convertedUuid.toString());
	}

	@ParameterizedTest
	@MethodSource("invalidUuidString")
	void fail_on_invalid_uuid_string(String value) {
		assertThatThrownBy(() -> UuidConverter.convert(value)).isInstanceOf(
			UuidConvertFailException.class);
	}

}