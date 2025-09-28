package com.newzet.api.userinfo.business.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.newzet.api.userinfo.exception.AuthUserFailException;

@ExtendWith(MockitoExtension.class)
class AuthUserServiceTest {

	private final String supabaseUrl = "http://test-supabase.com";
	private final String supabaseServiceKey = "test-service-key";
	@InjectMocks
	private AuthUserService authUserService;
	@Mock
	private RestTemplate restTemplate;

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(authUserService, "supabaseUrl", supabaseUrl);
		ReflectionTestUtils.setField(authUserService, "supabaseServiceKey", supabaseServiceKey);
	}

	@Test
	@DisplayName("Supabase 사용자 삭제 성공")
	void deleteAuthUser_Success() {
		// given: 테스트 준비
		UUID userId = UUID.randomUUID();
		ResponseEntity<String> successResponse = new ResponseEntity<>("User deleted",
			HttpStatus.OK);
		when(restTemplate.exchange(any(RequestEntity.class), eq(String.class)))
			.thenReturn(successResponse);

		// when & then: 실행 및 검증
		assertDoesNotThrow(() -> authUserService.deleteAuthUser(userId));

		// verify: RestTemplate이 올바른 인자와 함께 호출되었는지 검증
		ArgumentCaptor<RequestEntity<Void>> requestCaptor = ArgumentCaptor.forClass(
			RequestEntity.class);
		verify(restTemplate).exchange(requestCaptor.capture(), eq(String.class));

		RequestEntity<Void> capturedRequest = requestCaptor.getValue();
		assertEquals(URI.create(supabaseUrl + "/auth/v1/admin/users/" + userId),
			capturedRequest.getUrl());
		assertEquals("DELETE", capturedRequest.getMethod().name());
		assertEquals(supabaseServiceKey, capturedRequest.getHeaders().getFirst("apikey"));
		assertEquals("Bearer " + supabaseServiceKey,
			capturedRequest.getHeaders().getFirst("Authorization"));
	}

	@Test
	@DisplayName("Supabase API가 에러를 반환하여 사용자 삭제 실패")
	void deleteAuthUser_Fail_WhenSupabaseReturnsError() {
		// given: 테스트 준비
		UUID userId = UUID.randomUUID();
		String errorBody = "{\"error\":\"User not found\"}";
		// RestTemplate이 실패(400 Bad Request) 응답을 반환하도록 설정합니다.
		ResponseEntity<String> errorResponse = new ResponseEntity<>(errorBody,
			HttpStatus.BAD_REQUEST);
		when(restTemplate.exchange(any(RequestEntity.class), eq(String.class)))
			.thenReturn(errorResponse);

		// when & then: 실행 및 검증
		assertThrows(AuthUserFailException.class,
			() -> authUserService.deleteAuthUser(userId));
	}

	@Test
	@DisplayName("네트워크 오류로 인해 사용자 삭제 실패")
	void deleteAuthUser_Fail_WhenNetworkErrorOccurs() {
		// given: 테스트 준비
		UUID userId = UUID.randomUUID();
		// RestTemplate 호출 시 런타임 예외가 발생하도록 설정합니다. (네트워크 문제 시뮬레이션)
		when(restTemplate.exchange(any(RequestEntity.class), eq(String.class)))
			.thenThrow(new RuntimeException("Network error"));

		// when & then: 실행 및 검증
		AuthUserFailException exception = assertThrows(AuthUserFailException.class,
			() -> authUserService.deleteAuthUser(userId));

		// 정의된 일반 오류 메시지와 일치하는지 확인합니다.
		assertEquals("Supabase 요청 중 알 수 없는 오류가 발생하였습니다.", exception.getMessage());
	}
}