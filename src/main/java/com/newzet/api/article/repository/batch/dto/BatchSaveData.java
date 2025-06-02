package com.newzet.api.article.repository.batch.dto;

import java.util.List;
import java.util.Map;

import com.newzet.api.article.business.dto.ArticleEntityDto;

public record BatchSaveData(
	List<ArticleEntityDto> toSave,
	Map<String, String> toCache
) {
}
