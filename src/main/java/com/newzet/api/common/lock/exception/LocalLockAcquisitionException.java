package com.newzet.api.common.lock.exception;

import com.newzet.api.common.exception.InternalErrorException;

public class LocalLockAcquisitionException extends InternalErrorException {
	public LocalLockAcquisitionException(String message) {
		super(message);
	}
}
