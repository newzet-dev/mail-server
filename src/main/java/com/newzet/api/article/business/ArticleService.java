package com.newzet.api.article.business;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.newzet.api.article.business.dto.ArticleEntityDto;
import com.newzet.api.article.presentation.dto.ArticleDetailResponse;
import com.newzet.api.article.presentation.dto.ArticleListResponse;
import com.newzet.api.common.util.UuidConverter;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArticleService {

	private final ArticleRepository articleRepository;

	public ArticleListResponse getArticleListAtYearAndMonth(String userId, int year, int month) {
		UUID convertUserId = UuidConverter.convert(userId);
		List<ArticleEntityDto> articleListAtYearAndMonth = articleRepository.getMonthlyArticleWithImage(
			convertUserId, year, month);

	}

	public ArticleDetailResponse getArticleAndRead(String articleId) {

	}

}
