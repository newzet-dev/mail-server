package com.newzet.api.newsletter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.newzet.api.common.auth.annotation.Login;
import com.newzet.api.common.auth.annotation.RequireAuth;
import com.newzet.api.common.auth.domain.AuthUser;
import com.newzet.api.common.response.ResponseCode;
import com.newzet.api.common.response.SuccessResponse;
import com.newzet.api.newsletter.business.service.NewsletterRecommendationService;
import com.newzet.api.newsletter.business.service.NewsletterService;
import com.newzet.api.newsletter.controller.dto.NewsletterInfoResponse;
import com.newzet.api.newsletter.controller.dto.NewsletterListResponse;
import com.newzet.api.newsletter.controller.dto.NewsletterRecommendResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/newsletter")
@Tag(name = "뉴스레터", description = "뉴스레터 관련 API")
public class NewsletterController {

	private final NewsletterService newsletterService;
	private final NewsletterRecommendationService newsletterRecommendationService;

	@GetMapping("/search")
	@Operation(summary = "뉴스레터 검색",
		description = "뉴스레터 이름으로 뉴스테러를 검색한다.")
	public ResponseEntity<SuccessResponse<NewsletterListResponse>> getNewsletterListByName(
		@RequestParam(value = "name") String name) {
		NewsletterListResponse newsletterListResponse = newsletterService.searchNewsletterListByName(
			name);
		SuccessResponse<NewsletterListResponse> response = SuccessResponse.create(
			ResponseCode.SUCCESS, "뉴스레터 목록 조회 성공", newsletterListResponse);

		return ResponseEntity.ok(response);
	}

	@GetMapping
	@Operation(summary = "뉴스레터 리스트 조회",
		description = "뉴스레터 카테고리 id로 뉴스테러 리스트를 조회한다.")
	public ResponseEntity<SuccessResponse<NewsletterListResponse>> getNewsletterListByCategoryId(
		@RequestParam(value = "categoryId") String categoryId) {
		NewsletterListResponse newsletterListResponse = newsletterService.getNewsletterListByCategoryId(
			categoryId);
		SuccessResponse<NewsletterListResponse> response = SuccessResponse.create(
			ResponseCode.SUCCESS, "뉴스레터 목록 조회 성공", newsletterListResponse);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/{newsletterId}")
	@Operation(summary = "뉴스레터 상세정보 조회",
		description = "뉴스레터 id로 뉴스테러를 조회한다.")
	public ResponseEntity<SuccessResponse<NewsletterInfoResponse>> getNewsletterById(
		@PathVariable("newsletterId") String newsletterId) {
		NewsletterInfoResponse newsletterInfoResponse = newsletterService.getNewsLetterById(
			newsletterId);
		SuccessResponse<NewsletterInfoResponse> response = SuccessResponse.create(
			ResponseCode.SUCCESS, "뉴스레터 상세정보 조회 성공", newsletterInfoResponse);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/recommend")
	@RequireAuth
	@Operation(summary = "뉴스레터 추천 리스트 조회",
		description = "유저 id로 뉴스레터 추천 리스트를 조회한다.")
	public ResponseEntity<SuccessResponse<NewsletterRecommendResponse>> recommendNewsletterList(
		@Login AuthUser authUser) {
		NewsletterRecommendResponse newsletterRecommendResponse = newsletterRecommendationService.recommendNewsletterList(
			authUser.getId());
		SuccessResponse<NewsletterRecommendResponse> response = SuccessResponse.create(
			ResponseCode.SUCCESS, "뉴스레터 추천 성공", newsletterRecommendResponse);
		return ResponseEntity.ok(response);
	}
}
