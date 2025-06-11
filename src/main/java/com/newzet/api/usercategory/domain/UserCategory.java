package com.newzet.api.usercategory.domain;

import java.util.UUID;

public record UserCategory(
	UUID id,
	UUID userId,
	UUID categoryId
) {
}
