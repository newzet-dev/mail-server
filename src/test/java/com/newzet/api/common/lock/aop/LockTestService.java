package com.newzet.api.common.lock.aop;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@Transactional
public class LockTestService {

	@WithLock(prefix = "test:domain", key = "#domain")
	public void run(String domain, List<String> trace) {
		trace.add("1.서비스 로직 진입");
		innerDBMethod(trace);

		TransactionSynchronizationManager.registerSynchronization(
			new TransactionSynchronization() {
				@Override
				public void afterCommit() {
					trace.add("3.트랜잭션 커밋 완료");
				}
			}
		);
	}

	private void innerDBMethod(List<String> trace) {
		trace.add("2.DB 접근이 발생하는 내부 메서드 진입");
	}
}
