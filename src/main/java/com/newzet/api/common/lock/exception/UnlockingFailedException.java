package com.newzet.api.common.lock.exception;

import com.newzet.api.common.exception.InternalErrorException;

public class UnlockingFailedException extends InternalErrorException {
	public UnlockingFailedException(String message) {
		super(message);
	}
}
