package com.newzet.api.common.batch;

import java.util.Map;

import com.newzet.api.article.business.dto.ArticleDto;

public interface BatchProcessor {
	void addToBatch(ArticleDto articleDto);

	Map<String, Object> getBatchStatus();

	void startProcessing();

	void stopProcessing();
}
