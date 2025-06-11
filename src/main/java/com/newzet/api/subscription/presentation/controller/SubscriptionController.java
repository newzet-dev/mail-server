package com.newzet.api.subscription.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.newzet.api.common.auth.annotation.Login;
import com.newzet.api.common.auth.domain.AuthUser;
import com.newzet.api.common.response.ResponseCode;
import com.newzet.api.common.response.SuccessResponse;
import com.newzet.api.subscription.business.repository.SubscriptionQueryRepository;
import com.newzet.api.subscription.business.service.SubscriptionService;
import com.newzet.api.subscription.presentation.dto.SubscriptionListWithImageResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subscription")
@Tag(name = "구독", description = "구독 관련 API")
public class SubscriptionController {

	private final SubscriptionService subscriptionService;
	private final SubscriptionQueryRepository subscriptionQueryRepository;

	@GetMapping
	@Operation(summary = "뉴스레터 구독 목록 조회",
		description = "유저의 뉴스레터 구독 목록을 조회한다.")
	public ResponseEntity<SuccessResponse<SubscriptionListWithImageResponse>> getSubscriptionList(
		@Login AuthUser authUser) {
		SubscriptionListWithImageResponse subscriptionListWithImage = subscriptionQueryRepository.getSubscriptionWithImage(
			authUser.id());
		SuccessResponse<SubscriptionListWithImageResponse> response = SuccessResponse.create(
			ResponseCode.SUCCESS, "뉴스레터 구독 목록 조회 성공", subscriptionListWithImage
		);
		return ResponseEntity.ok(response);

	}

	@DeleteMapping("/{subscriptionId}")
	@Operation(summary = "뉴스레터 구독 삭제",
		description = "유저의 뉴스레터 구독을 삭제한다.")
	public ResponseEntity<SuccessResponse> deleteSubscription(
		@PathVariable String subscriptionId) {
		subscriptionService.deleteSubscription(subscriptionId);
		SuccessResponse<String> response = SuccessResponse.create(
			ResponseCode.SUCCESS, "뉴스레터 구독 삭제 성공", null);

		return ResponseEntity.ok(response);
	}
}
