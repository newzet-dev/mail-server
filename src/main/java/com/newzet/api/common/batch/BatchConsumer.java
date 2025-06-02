package com.newzet.api.common.batch;

import java.util.Map;

public interface BatchConsumer {
	Map<String, Object> getBatchStatus();

	void startProcessing();

	void stopProcessing();
}
