package com.newzet.api.event.jpa.mapper;

import com.newzet.api.event.domain.Event;
import com.newzet.api.event.jpa.entity.EventEntity;

public class EventEntityMapper {

	public static Event toDomain(EventEntity eventEntity) {
		return Event.create(eventEntity.getId(), eventEntity.getEventUrl(), eventEntity.getImageUrl());
	}

}
