package com.newzet.api.userinfo.business.service;

import java.net.URI;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.newzet.api.userinfo.exception.AuthUserFailException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthUserService {

	private final RestTemplate restTemplate;

	@Value("${supabase.url}")
	private String supabaseUrl;

	@Value("${supabase.service-key}")
	private String supabaseServiceKey;

	public void deleteAuthUser(UUID userId) {
		String url = supabaseUrl + "/auth/v1/admin/users/" + userId;

		RequestEntity<Void> requestEntity = RequestEntity
			.delete(URI.create(url))
			.header("apikey", supabaseServiceKey)
			.header("Authorization", "Bearer " + supabaseServiceKey)
			.build();

		try {
			// DELETE 요청 보내기
			ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);
			if (!response.getStatusCode().is2xxSuccessful()) {
				throw new AuthUserFailException("Supabase 사용자 삭제 실패: " + response.getBody());
			}

		} catch (Exception e) {
			// 네트워크 오류 등 처리
			throw new AuthUserFailException("Supabase 요청 중 알 수 없는 오류가 발생하였습니다.");
		}
	}
}
