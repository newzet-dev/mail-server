package com.newzet.api.usercategory.domain;

import java.util.UUID;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserCategory {
	private final UUID id;
	private final UUID userId;
	private final UUID categoryId;
}
