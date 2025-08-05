package com.newzet.api.newsletter.presentation.dto;

import java.util.UUID;

public record NewsletterResponse(
	UUID id,
	String name,
	String imageUrl,
	String description,
	Integer priority
) {
}
