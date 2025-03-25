package com.newzet.api.common.lock.exception;

import lombok.extern.slf4j.Slf4j;

public class RedisLockAcquisitionException extends RuntimeException {
	public RedisLockAcquisitionException() {
		super();
	}
}
