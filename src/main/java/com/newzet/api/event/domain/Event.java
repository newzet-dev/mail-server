package com.newzet.api.event.domain;

import java.util.UUID;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Event{
	private final UUID id;
	private final String eventUrl;
	private final String imageUrl;

	public static Event create(UUID id, String eventUrl, String imageUrl){
		return new Event(id, eventUrl, imageUrl);
	}

}
