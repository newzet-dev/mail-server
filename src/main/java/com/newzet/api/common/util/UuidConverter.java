package com.newzet.api.common.util;

import java.util.UUID;

import com.newzet.api.common.util.exception.UuidConvertFailException;

public class UuidConverter {
	public static UUID convert(String stringId) {
		if (stringId == null || stringId.isEmpty()) {
			throw new UuidConvertFailException("빈 값을 uuid로 변환할 수 없습니다.");
		}

		try {
			return UUID.fromString(stringId);
		} catch (IllegalArgumentException e) {
			throw new UuidConvertFailException(e.getMessage());
		}
	}
}
