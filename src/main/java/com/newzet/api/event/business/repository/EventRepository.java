package com.newzet.api.event.business.repository;

import java.util.List;

import com.newzet.api.event.domain.Event;

public interface EventRepository {

	List<Event> findEventList();
}
