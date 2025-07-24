package com.newzet.api.common.s3;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Service;

import com.newzet.api.common.exception.InternalErrorException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {
	private final S3Client s3Client;

	public String getContentAsString(String bucketName, String key) {
		GetObjectRequest getObjectRequest = GetObjectRequest.builder()
			.bucket(bucketName)
			.key(key)
			.build();

		try {
			ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(
				getObjectRequest);
			byte[] contentBytes = s3Object.readAllBytes(); // InputStream의 모든 byte를 읽어와 UTF-8 문자열로 변환
			return new String(contentBytes, StandardCharsets.UTF_8);
		} catch (NoSuchKeyException e) { // 파일이 존재하지 않을 경우 예외 처리
			log.error("S3에 해당 파일이 존재하지 않습니다. Key: {}", key);
			throw new InternalErrorException("아티클을 불러오는 과정에서 에러가 발생하였습니다.");
		} catch (IOException e) {
			log.error("S3 파일 내용을 읽는 중 오류가 발생했습니다.", e);
			throw new InternalErrorException("아티클을 불러오는 과정에서 에러가 발생하였습니다.");
		}
	}
}
