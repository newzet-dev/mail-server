package com.newzet.api.fcm.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.newzet.api.fcm.business.batch.FcmBatchConsumer;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fcm/batch")
@Tag(name = "FCM Batch", description = "[관리자용] FCM 배치 처리 관련 API")
public class FcmBatchController {

	private final FcmBatchConsumer fcmBatchConsumer;

	@GetMapping("/status")
	@Operation(summary = "[관리자용] FCM 배치 처리 상태 조회",
		description = "현재 FCM 배치 처리 상태와 대기 중인 메시지 수를 조회합니다.")
	public ResponseEntity<Map<String, Object>> getBatchStatus() {
		return ResponseEntity.ok(fcmBatchConsumer.getBatchStatus());
	}

	@PostMapping("/restart")
	@Operation(summary = "[관리자용] FCM 배치 처리 재시작",
		description = "FCM 배치 처리를 중지하고 다시 시작합니다.")
	public ResponseEntity<String> restartBatchProcessing() {
		fcmBatchConsumer.stopProcessing();
		fcmBatchConsumer.startProcessing();
		return ResponseEntity.ok("FCM batch processing restarted");
	}
}
