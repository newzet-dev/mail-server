package com.newzet.api.event.jpa.repository;

import static com.newzet.api.event.jpa.entity.QEventEntity.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.newzet.api.event.business.repository.EventQueryRepository;
import com.newzet.api.event.jpa.entity.EventEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class EventJpaQueryRepository implements EventQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<EventEntity> findEventListOrdered() {
		LocalDateTime currentTime = LocalDateTime.now();

		return jpaQueryFactory
			.selectFrom(eventEntity)
			.where(
				eventEntity.postStart.lt(currentTime),
				eventEntity.postEnd.gt(currentTime)
			)
			.orderBy(eventEntity.priority.asc())
			.fetch();
	}
}
