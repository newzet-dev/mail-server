package com.newzet.api.advertise.domain;

import java.util.UUID;

public record Advertise(
	UUID id,
	UUID newsletterId
) {
}
