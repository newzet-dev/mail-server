package com.newzet.api.event.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.newzet.api.common.response.SuccessResponse;
import com.newzet.api.event.presentation.dto.EventListResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequestMapping("/api/v1/event")
@Tag(name = "이벤트 배너", description = "이벤트 배너 관련 API")
public interface EventApi {

	@GetMapping
	@Operation(summary = "이벤트 배너 리스트 조회",
		description = "모든 이벤트 배너 리스트를 조회한다.")
	SuccessResponse<EventListResponse> getEventList();
}
