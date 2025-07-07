package com.newzet.api.event.jpa.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.newzet.api.event.business.repository.EventQueryRepository;
import com.newzet.api.event.business.repository.EventRepository;
import com.newzet.api.event.domain.Event;
import com.newzet.api.event.jpa.mapper.EventEntityMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class EventRepositoryImpl implements EventRepository {

	private final EventQueryRepository eventQueryRepository;

	@Override
	public List<Event> findEventList() {
		return eventQueryRepository.findEventListOrdered().stream()
			.map(EventEntityMapper::toDomain)
			.toList();
	}
}
