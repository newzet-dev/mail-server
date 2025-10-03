package com.newzet.api.common.util;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class UtcTimeZoneConverter {

	private static final ZoneId UTC = ZoneId.of("UTC");
	private static final ZoneId KST = ZoneId.of("Asia/Seoul");

	public static LocalDateTime toKst(LocalDateTime utcDateTime) {
		if (utcDateTime == null) {
			return null;
		}
		return utcDateTime.atZone(UTC)
			.withZoneSameInstant(KST)
			.toLocalDateTime();
	}
}
