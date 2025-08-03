package com.newzet.api.event.presentation.controller;

import org.springframework.web.bind.annotation.RestController;

import com.newzet.api.common.response.ResponseCode;
import com.newzet.api.common.response.SuccessResponse;
import com.newzet.api.event.api.EventApi;
import com.newzet.api.event.orchestrator.EventOrchestrator;
import com.newzet.api.event.presentation.dto.EventListResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class EventController implements EventApi {

	private final EventOrchestrator eventOrchestrator;

	@Override
	public SuccessResponse<EventListResponse> getEventList() {
		EventListResponse eventList = eventOrchestrator.getAllEvents();
		return SuccessResponse.create(ResponseCode.SUCCESS, "이벤트 리스트 조회 성공",
			eventList);
	}
}
