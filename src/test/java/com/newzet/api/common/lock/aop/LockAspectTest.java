package com.newzet.api.common.lock.aop;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.newzet.api.common.lock.LockFactory;
import com.newzet.api.config.JwtTestConfig;
import com.newzet.api.config.PostgresTestContainerConfig;
import com.newzet.api.config.RedisTestContainerConfig;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith({RedisTestContainerConfig.class, PostgresTestContainerConfig.class,
	JwtTestConfig.class})
@EnableAspectJAutoProxy
class LockAspectTest {

	@Autowired
	LockTestService lockTestService;
	@MockitoBean
	LockFactory lockFactory;
	@Mock
	Lock mockLock;

	@Test
	@DisplayName("서비스 메서드의 트랜잭션 종료 이후에 락이 반납되는지 테스트")
	void unlock_after_service_transaction() {
		// given
		List<String> trace = new ArrayList<>();
		when(lockFactory.tryLock(anyString(), anyLong(), anyLong()))
			.thenReturn(mockLock);
		doAnswer(invocation -> {
			trace.add("락 해제됨");
			return null; // void 리턴이므로 null
		}).when(lockFactory).unlock(any(Lock.class));


		// when
		lockTestService.run(trace);

		// then
		InOrder inOrder = inOrder(lockFactory, mockLock); // 순서대로 호출되어야함을 의미
		inOrder.verify(lockFactory).tryLock(anyString(), anyLong(), anyLong());
		inOrder.verify(lockFactory).unlock(any(Lock.class));
		assertThat(trace).containsExactly("business run!",
			"트랜잭션 커밋 이후 후처리 실행됨",
			"락 해제됨");

	}

}