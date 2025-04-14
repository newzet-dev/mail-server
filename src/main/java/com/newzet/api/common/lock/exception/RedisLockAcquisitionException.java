package com.newzet.api.common.lock.exception;

import com.newzet.api.common.exception.InternalErrorException;

public class RedisLockAcquisitionException extends InternalErrorException {
	public RedisLockAcquisitionException(String message) {
		super(message);
	}
}
