package com.newzet.api.common.batch;

import com.newzet.api.article.domain.Article;

public interface BatchProducer {
	void addToBatch(Article article);
}
