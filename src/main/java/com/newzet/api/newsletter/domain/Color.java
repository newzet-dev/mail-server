package com.newzet.api.newsletter.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Color {
	RED("RED"),
	BLUE("BLUE"),
	GREEN("GREEN"),
	YELLOW("YELLOW"),
	ORANGE("ORANGE"),
	SKY("SKY"),
	PINK("PINK"),
	DEFAULT("GRAY");

	private final String color;


}
