package com.newzet.api.article.repository.batch.dto;

import lombok.Data;

@Data
public class BatchProcessingResult {
	private int successCount;
	private int failCount;
	private int duplicateCount;
	private int cacheHitCount;
	private int batchDuplicateCount;

	public void incrementFailCount() {
		this.failCount++;
	}

	public void incrementFailCount(int count) {
		this.failCount += count;
	}

	public void incrementDuplicateCount() {
		this.duplicateCount++;
	}

	public void addToDuplicateCount(int count) {
		this.duplicateCount += count;
	}

	public void incrementCacheHitCount() {
		this.cacheHitCount++;
	}

	public void incrementBatchDuplicateCount(int count) {
		this.batchDuplicateCount += count;
	}
}
