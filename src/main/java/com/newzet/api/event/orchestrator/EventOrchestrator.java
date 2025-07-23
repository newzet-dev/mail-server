package com.newzet.api.event.orchestrator;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newzet.api.event.business.service.EventService;
import com.newzet.api.event.presentation.dto.EventListResponse;
import com.newzet.api.event.presentation.mapper.EventResponseMapper;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventOrchestrator {

	private final EventService eventService;

	public EventListResponse getAllEvents() {
		return EventResponseMapper.toListResponse(eventService.getAllEvents());
	}
}
