package com.newzet.api.article.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.newzet.api.common.batch.BatchConsumer;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/articles/batch")
@Tag(name = "Article Batch", description = "[관리자용] 아티클 배치 처리 관련 API")
public class ArticleBatchController {

	private final BatchConsumer batchConsumer;

	@GetMapping("/status")
	@Operation(summary = "[관리자용] 배치 처리 상태 조회",
		description = "현재 배치 처리 상태와 대기 중인 메시지 수를 조회합니다.")
	public ResponseEntity<Map<String, Object>> getBatchStatus() {
		return ResponseEntity.ok(batchConsumer.getBatchStatus());
	}

	@PostMapping("/restart")
	@Operation(summary = "[관리자용] 배치 처리 재시작",
		description = "배치 처리를 중지하고 다시 시작합니다.")
	public ResponseEntity<String> restartBatchProcessing() {
		batchConsumer.stopProcessing();
		batchConsumer.startProcessing();
		return ResponseEntity.ok("Batch processing restarted");
	}
}
