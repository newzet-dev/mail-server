package com.newzet.api.event.business.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newzet.api.event.business.repository.EventRepository;
import com.newzet.api.event.domain.Event;
import com.newzet.api.event.presentation.dto.EventListResponse;
import com.newzet.api.event.presentation.mapper.EventResponseMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {
	private final EventRepository eventRepository;

	public EventListResponse getAllEvents() {
		List<Event> eventList = eventRepository.findEventList();
		return EventResponseMapper.toListResponse(eventList);
	}
}
