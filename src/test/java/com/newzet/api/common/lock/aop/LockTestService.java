package com.newzet.api.common.lock.aop;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@Transactional
public class LockTestService {

	@WithLock(prefix = "test:domain", domain = "domain123")
	public void run(List<String> trace) {
		trace.add("1.서비스 로직 진입");
		innerDBMethod(trace);
	}

	private void innerDBMethod(List<String> trace) {
		trace.add("2.DB 접근이 발생하는 내부 메서드 진입");
		TransactionSynchronizationManager.registerSynchronization(
			new TransactionSynchronizationAdapter() {
				@Override
				public void afterCommit() {
					trace.add("3.트랜잭션 커밋 완료");
				}
			}
		);
	}
}
