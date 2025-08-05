package com.newzet.api.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
//TODO: 코드 잘못 던지고 있는 부분 수정
public enum ResponseCode {
	SUCCESS(20000),
	INVALID_ARGUMENTS(40000),
	UNAUTHORIZED(40001),
	FORBIDDEN(40003),
	NOT_FOUND(40004),
	CONFLICT(40009),
	TOKEN_EXPIRED(40020),
	TOKEN_STOLEN(40021),
	SERVER_ERROR(50000),
	DATABASE_ACCESS_ERROR(50002),
	OAUTH_ERROR(50003),
	SUPABASE_ERROR(50005),
	S3_ACCESS_ERROR(50006),
	STORAGE_ACCESS_ERROR(50007);

	private final int code;

}
