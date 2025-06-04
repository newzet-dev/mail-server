package com.newzet.api.article.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.newzet.api.article.business.service.ArticleService;
import com.newzet.api.article.controller.dto.ArticleContentResponse;
import com.newzet.api.article.controller.dto.ArticleLikeListResponse;
import com.newzet.api.article.controller.dto.ArticleListResponse;
import com.newzet.api.common.auth.annotation.Login;
import com.newzet.api.common.auth.annotation.RequireAuth;
import com.newzet.api.common.auth.domain.AuthUser;
import com.newzet.api.common.response.ResponseCode;
import com.newzet.api.common.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/article")
@Tag(name = "아티클", description = "아티클 관련 API")
public class ArticleController {

	private final ArticleService articleService;

	@GetMapping
	@RequireAuth
	@Operation(summary = "아티클 월별 목록 조회",
		description = "유저가 구독한 뉴스레터들의 월별 아티클 목록을 조회한다.")
	public ResponseEntity<SuccessResponse<ArticleListResponse>> getMonthlyArticleList(
		@RequestParam("y") int year,
		@RequestParam("m") int month, @Login AuthUser user) {
		UUID userId = user.getId();
		ArticleListResponse monthlyArticleList = articleService.getMonthlyArticleList(userId, year,
			month);

		SuccessResponse<ArticleListResponse> response = SuccessResponse.create(
			ResponseCode.SUCCESS, "아티클 월별 목록 조회 성공", monthlyArticleList);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/{articleId}")
	@RequireAuth
	@Operation(summary = "아티클 단건 조회",
		description = "유저가 구독한 뉴스레터의 아티클을 조회한다.")
	public ResponseEntity<SuccessResponse<ArticleContentResponse>> getArticle(
		@PathVariable("articleId") String articleId) {
		ArticleContentResponse articleContentResponse = articleService.getArticle(articleId);

		SuccessResponse<ArticleContentResponse> response = SuccessResponse.create(
			ResponseCode.SUCCESS, "아티클 단건 조회 성공", articleContentResponse);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/like")
	@RequireAuth
	@Operation(summary = "좋아요 아티클 목록 조회",
		description = "유저가 좋아요를 누른 뉴스레터의 아티클 목록을 조회한다.")
	public ResponseEntity<SuccessResponse<ArticleLikeListResponse>> getArticleLikeList(
		@Login AuthUser user) {
		UUID userId = user.getId();
		ArticleLikeListResponse articleLikeList = articleService.getArticleLikeList(userId);

		SuccessResponse<ArticleLikeListResponse> response = SuccessResponse.create(
			ResponseCode.SUCCESS, "아티클 좋아요 목록 조회 성공", articleLikeList);

		return ResponseEntity.ok(response);
	}

	@PatchMapping("/like/{articleId}")
	@RequireAuth
	@Operation(summary = "아티클 좋아요 상태 변경",
		description = "유저가 좋아요의 상태를 변경한다.")
	public ResponseEntity<SuccessResponse<String>> updateArticleLike(
		@PathVariable("articleId") String articleId,
		@RequestParam("isLike") boolean newLikeStatus) {
		articleService.changeLikeStatus(articleId, newLikeStatus);

		SuccessResponse<String> response = SuccessResponse.create(
			ResponseCode.SUCCESS, "좋아요 상태 업데이트 성공", "empty");

		return ResponseEntity.ok(response);
	}
}
