package com.newzet.api.event.business.repository;

import java.util.List;

import com.newzet.api.event.jpa.entity.EventEntity;

public interface EventQueryRepository {

	List<EventEntity> findEventListOrdered();
}
