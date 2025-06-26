package com.newzet.api.fcm.jpa.batch.dto;

import lombok.Data;

@Data
public class FcmBatchProcessingResult {
	private int successCount;
	private int failCount;
	private int invalidTokenCount;

	public void incrementSuccessCount() {
		this.successCount++;
	}

	public void incrementFailCount() {
		this.failCount++;
	}

	public void incrementInvalidTokenCount() {
		this.invalidTokenCount++;
	}
}
