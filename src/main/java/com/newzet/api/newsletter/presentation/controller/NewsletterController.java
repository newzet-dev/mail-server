package com.newzet.api.newsletter.presentation.controller;

import java.util.Optional;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.newzet.api.common.auth.annotation.Login;
import com.newzet.api.common.auth.annotation.OptionalLogin;
import com.newzet.api.common.auth.annotation.RequireAuth;
import com.newzet.api.common.auth.domain.AuthUser;
import com.newzet.api.common.response.ResponseCode;
import com.newzet.api.common.response.SuccessResponse;
import com.newzet.api.newsletter.orchestrator.NewsletterOrchestrator;
import com.newzet.api.newsletter.presentation.dto.NewsletterInfoResponse;
import com.newzet.api.newsletter.presentation.dto.NewsletterListResponse;
import com.newzet.api.newsletter.presentation.dto.NewsletterRecommendResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/newsletter")
@Tag(name = "뉴스레터", description = "뉴스레터 관련 API")
public class NewsletterController {

	private final NewsletterOrchestrator newsletterOrchestrator;

	@GetMapping("/search")
	@Operation(summary = "뉴스레터 검색",
		description = "뉴스레터 이름으로 뉴스테러를 검색한다.")
	public SuccessResponse<NewsletterListResponse> getNewsletterListByName(
		@RequestParam(value = "name") String name) {
		NewsletterListResponse response = newsletterOrchestrator.searchNewsletterListByName(name);
		return SuccessResponse.create(ResponseCode.SUCCESS, "뉴스레터 목록 조회 성공", response);
	}

	@GetMapping
	@Operation(summary = "뉴스레터 리스트 조회",
		description = "뉴스레터 카테고리 id로 뉴스테러 리스트를 조회한다.")
	public SuccessResponse<NewsletterListResponse> getNewsletterListByCategoryId(
		@RequestParam(value = "categoryId") UUID categoryId) {
		NewsletterListResponse response = newsletterOrchestrator.getNewsletterListByCategoryId(
			categoryId);
		return SuccessResponse.create(ResponseCode.SUCCESS, "뉴스레터 목록 조회 성공", response);
	}

	@GetMapping("/{newsletterId}")
	@RequireAuth(optional = true)
	@Operation(summary = "뉴스레터 상세정보 조회",
		description = "뉴스레터 id로 뉴스테러를 조회한다.")
	public SuccessResponse<NewsletterInfoResponse> getNewsletterInfoById(
		@OptionalLogin Optional<AuthUser> authUser,
		@PathVariable("newsletterId") UUID newsletterId) {
		NewsletterInfoResponse response = authUser
			.map(user -> newsletterOrchestrator.getNewsLetterInfoWithLogin(user.getId(),
				newsletterId)) // 로그인 유저 로직
			.orElseGet(() -> newsletterOrchestrator.getNewsletterInfoWithoutLogin(
				newsletterId)); // 비로그인 유저 로직
		return SuccessResponse.create(ResponseCode.SUCCESS, "뉴스레터 상세정보 조회 성공", response);
	}

	@GetMapping("/recommend")
	@RequireAuth
	@Operation(summary = "뉴스레터 추천 리스트 조회",
		description = "유저 id로 뉴스레터 추천 리스트를 조회한다.")
	public SuccessResponse<NewsletterRecommendResponse> recommendNewsletterList(
		@Login AuthUser authUser) {
		NewsletterRecommendResponse response = newsletterOrchestrator.recommendNewsletterList(
			authUser.getId());
		return SuccessResponse.create(ResponseCode.SUCCESS, "뉴스레터 추천 성공", response);
	}
}
