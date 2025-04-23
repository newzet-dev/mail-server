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
		trace.add("business run!");

		TransactionSynchronizationManager.registerSynchronization(
			new TransactionSynchronizationAdapter() {
				@Override
				public void afterCommit() {
					trace.add("트랜잭션 커밋 이후 후처리 실행됨");
				}
			}
		);
	}
}
