package com.newzet.api.subscription.repository.exception;

public class NoSubscriptionException extends RuntimeException {
	public NoSubscriptionException(String route) {
		super(String.format("[%s]-일치하는 구독이 없습니다.", route));
	}
}
