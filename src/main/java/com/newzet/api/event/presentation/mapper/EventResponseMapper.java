package com.newzet.api.event.presentation.mapper;

import java.util.List;

import com.newzet.api.event.domain.Event;
import com.newzet.api.event.presentation.dto.EventListResponse;
import com.newzet.api.event.presentation.dto.EventResponse;

public class EventResponseMapper {

	public static EventListResponse toListResponse(List<Event> events) {
		return new EventListResponse(events.stream()
			.map(event -> new EventResponse(event.getImageUrl(), event.getEventUrl() ))
			.toList());
	}
}
