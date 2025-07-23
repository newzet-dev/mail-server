package com.newzet.api.event.business.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.newzet.api.event.business.repository.EventRepository;
import com.newzet.api.event.domain.Event;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventService {
	private final EventRepository eventRepository;

	public List<Event> getAllEvents() {
		return eventRepository.findEventList();
	}
}
