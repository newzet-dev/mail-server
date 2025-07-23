package com.newzet.api.event.jpa.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import com.newzet.api.config.PostgresTestContainerConfig;
import com.newzet.api.config.db.QuerydslConfig;
import com.newzet.api.event.business.repository.EventRepository;
import com.newzet.api.event.domain.Event;
import com.newzet.api.event.jpa.entity.EventEntity;

@DataJpaTest
@Import({EventRepositoryImpl.class, EventJpaQueryRepository.class, QuerydslConfig.class})
@ExtendWith(PostgresTestContainerConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EventRepositoryImplTest {

	@Autowired
	private EventRepository eventRepository;

	@Autowired
	private TestEntityManager entityManager;

	@Test
	@DisplayName("활성화된 이벤트 목록을 우선순위 순으로 정확히 조회한다")
	void findEventListOrdered_shouldReturnActiveEvents_orderedByPriority() {
		// given: 테스트 데이터 설정
		Instant now = Instant.now();

		// Case 1: 활성화된 이벤트 (우선순위 2) - 조회되어야 함
		EventEntity activeEvent1 = new EventEntity(
			null,
			"https://event.com/active1",
			"https://image.com/active1.png",
			now.minus(1, ChronoUnit.DAYS).toString(),
			now.plus(1, ChronoUnit.DAYS).toString(),
			2
		);

		// Case 2: 활성화된 이벤트 (우선순위 1) - 조회되어야 하며, 가장 먼저 나와야 함
		EventEntity activeEvent2 = new EventEntity(
			null,
			"https://event.com/active2",
			"https://image.com/active2.png",
			now.minus(2, ChronoUnit.DAYS).toString(),
			now.plus(2, ChronoUnit.DAYS).toString(),
			1
		);

		// Case 3: 종료된 이벤트 - 조회되지 않아야 함
		EventEntity expiredEvent = new EventEntity(
			null,
			"https://event.com/expired",
			"https://image.com/expired.png",
			now.minus(10, ChronoUnit.DAYS).toString(),
			now.minus(5, ChronoUnit.DAYS).toString(),
			3
		);

		// Case 4: 시작 전 이벤트 - 조회되지 않아야 함
		EventEntity upcomingEvent = new EventEntity(
			null,
			"https://event.com/upcoming",
			"https://image.com/upcoming.png",
			now.plus(5, ChronoUnit.DAYS).toString(),
			now.plus(10, ChronoUnit.DAYS).toString(),
			4
		);

		// TestEntityManager를 사용하여 DB에 저장
		entityManager.persist(activeEvent1);
		entityManager.persist(activeEvent2);
		entityManager.persist(expiredEvent);
		entityManager.persist(upcomingEvent);
		entityManager.flush(); // DB에 변경사항 즉시 반영

		// when
		List<Event> foundEvents = eventRepository.findEventList();

		// then
		// 1. 활성화된 이벤트 2개만 조회되었는지 확인
		assertThat(foundEvents).hasSize(2);

		// 2. 우선순위(priority)가 낮은 순서(오름차순)로 정렬되었는지 확인
		assertThat(foundEvents)
			.extracting(Event::getEventUrl)
			.containsExactly("https://event.com/active2", "https://event.com/active1");
	}

}