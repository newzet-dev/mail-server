package com.newzet.api.article.business.repository;

import java.util.List;
import java.util.UUID;

import com.newzet.api.article.business.dto.ArticleEntityDto;

public interface ArticleRepository {
	List<ArticleEntityDto> saveAll(List<ArticleEntityDto> articleEntityDtoList);

	boolean existsByFromNameAndFromDomainAndTitleAndToUserIdAndDeletedAtIsNull(
		String fromName, String fromDomain, String title, UUID toUserId);
}
