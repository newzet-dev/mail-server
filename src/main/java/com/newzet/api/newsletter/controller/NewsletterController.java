package com.newzet.api.newsletter.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.newzet.api.common.response.ResponseCode;
import com.newzet.api.common.response.SuccessResponse;
import com.newzet.api.newsletter.business.NewsletterService;
import com.newzet.api.newsletter.controller.dto.NewsletterInfoResponse;
import com.newzet.api.newsletter.controller.dto.NewsletterListResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/newsletter")
public class NewsletterController {

	private final NewsletterService newsletterService;

	@GetMapping
	public ResponseEntity<SuccessResponse<NewsletterListResponse>> getNewsletterListByNameOrCategoryId(
		@RequestParam("name") String name,
		@RequestParam("categoryId") String categoryId) {
		NewsletterListResponse newsletterListResponse = newsletterService.searchNewsletterListByNameOrCategoryId(
			name, UUID.fromString(categoryId));
		SuccessResponse<NewsletterListResponse> response = SuccessResponse.create(
			ResponseCode.SUCCESS, "뉴스레터 목록 조회 성공", newsletterListResponse);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/{newsletterId}")
	public ResponseEntity<SuccessResponse<NewsletterInfoResponse>> getNewsletterById(
		@PathVariable("newsletterId") String newsletterId) {
		NewsletterInfoResponse newsletterInfoResponse = newsletterService.getNewsLetterById(
			UUID.fromString(newsletterId));
		SuccessResponse<NewsletterInfoResponse> response = SuccessResponse.create(
			ResponseCode.SUCCESS, "뉴스레터 상세정보 조회 성공", newsletterInfoResponse);

		return ResponseEntity.ok(response);
	}
}
