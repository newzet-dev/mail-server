package com.newzet.api.event.presentation.dto;

import java.util.List;

public record EventListResponse(
	List<EventResponse> eventList
) {
}
