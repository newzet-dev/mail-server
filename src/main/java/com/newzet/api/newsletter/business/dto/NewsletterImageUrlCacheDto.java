package com.newzet.api.newsletter.business.dto;

public record NewsletterImageUrlCacheDto(
	String imageUrl
) {

	public static NewsletterImageUrlCacheDto create(String imageUrl) {
		return new NewsletterImageUrlCacheDto(imageUrl);
	}
}
