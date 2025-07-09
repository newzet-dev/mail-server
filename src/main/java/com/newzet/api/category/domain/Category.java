package com.newzet.api.category.domain;

import java.util.UUID;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Category {
	private final UUID id;
	private final String name;
	private final String imageUrl;
	private final String emoji;
}
