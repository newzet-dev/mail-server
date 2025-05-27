package com.newzet.api.common.batch;

import com.newzet.api.article.business.dto.ArticleDto;

public interface BatchProducer {
	void addToBatch(ArticleDto articleDto);
}
