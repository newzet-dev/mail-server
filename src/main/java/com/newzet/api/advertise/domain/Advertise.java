package com.newzet.api.advertise.domain;

import java.util.UUID;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Advertise {
	private final UUID id;
	private final UUID newsletterId;
}
