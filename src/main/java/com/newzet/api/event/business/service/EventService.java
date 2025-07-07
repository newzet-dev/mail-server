package com.newzet.api.event.business.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.newzet.api.event.business.repository.EventRepository;
import com.newzet.api.event.domain.Event;
import com.newzet.api.event.presentation.dto.EventListResponse;
import com.newzet.api.event.presentation.mapper.EventResponseMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventService {
	private final EventRepository eventRepository;

	public EventListResponse getAllEvents() {
		List<Event> eventList = eventRepository.findEventList();
		return EventResponseMapper.toListResponse(eventList);
	}
}
