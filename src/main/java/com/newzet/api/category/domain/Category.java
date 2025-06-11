package com.newzet.api.category.domain;

import java.util.UUID;

public record Category(
	UUID id,
	String name,
	String imageUrl,
	String emoji
) {
}
