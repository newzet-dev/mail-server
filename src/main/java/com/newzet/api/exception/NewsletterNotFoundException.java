package com.newzet.api.exception;

public class NewsletterNotFoundException extends RuntimeException {
	public NewsletterNotFoundException(String message) {
		super(message);
	}
}
