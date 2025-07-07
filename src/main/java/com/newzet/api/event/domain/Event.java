package com.newzet.api.event.domain;

import java.util.UUID;

public record Event(
	UUID id,
	String eventUrl,
	String imageUrl
) {
}
