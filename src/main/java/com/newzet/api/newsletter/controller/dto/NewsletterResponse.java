package com.newzet.api.newsletter.controller.dto;

import java.util.UUID;

public record NewsletterResponse(
	String id,
	String name,
	String imageUrl,
	String description,
	Integer priority
) {
	public static NewsletterResponse create(UUID id, String name, String imageUrl,
		String description, Integer priority) {
		return new NewsletterResponse(id.toString(), name, imageUrl, description, priority);
	}
}
