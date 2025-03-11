package com.newzet.api.common.cache.exception;

public class LockAcquisitionException extends RuntimeException {
	public LockAcquisitionException(String route) {
		super(String.format("[%s]-waitTime 내 Lock 획득 실패", route));
	}
}
