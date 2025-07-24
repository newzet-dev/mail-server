package com.newzet.api.common.s3;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.newzet.api.common.exception.InternalErrorException;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

	private final String bucketName = "test-bucket";
	private final String key = "test-object.txt";
	@InjectMocks
	private S3Service s3Service;
	@Mock
	private S3Client s3Client;

	@Test
	@DisplayName("성공: S3 객체를 성공적으로 읽어와 문자열로 반환한다")
	void getContentAsString_Success() throws IOException {
		// given
		String expectedContent = "S3 Object Content";
		byte[] contentBytes = expectedContent.getBytes(StandardCharsets.UTF_8);

		ResponseInputStream<GetObjectResponse> s3ObjectStream = new ResponseInputStream<>(
			GetObjectResponse.builder().build(),
			new ByteArrayInputStream(contentBytes)
		);

		when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(s3ObjectStream);

		// when
		String actualContent = s3Service.getContentAsString(bucketName, key);

		// then
		assertEquals(expectedContent, actualContent);
		verify(s3Client).getObject(any(GetObjectRequest.class));
	}

	@Test
	@DisplayName("실패: S3에 해당 Key의 객체가 존재하지 않으면 InternalErrorException을 던진다")
	void getContentAsString_ThrowsNoSuchKeyException() {
		// given
		when(s3Client.getObject(any(GetObjectRequest.class)))
			.thenThrow(NoSuchKeyException.builder().message("Not Found").build());

		// when & then
		assertThrows(InternalErrorException.class, () -> {
			s3Service.getContentAsString(bucketName, key);
		});
	}

	@Test
	@DisplayName("실패: 스트림을 읽는 중 IOException이 발생하면 InternalErrorException을 던진다")
	void getContentAsString_ThrowsIOException() throws IOException {
		// given
		InputStream mockInputStream = mock(InputStream.class);
		when(mockInputStream.read(any(byte[].class), anyInt(), anyInt()))
			.thenThrow(new IOException("Failed to read stream"));

		ResponseInputStream<GetObjectResponse> s3ObjectStream = new ResponseInputStream<>(
			GetObjectResponse.builder().build(),
			mockInputStream
		);

		when(s3Client.getObject(any(GetObjectRequest.class)))
			.thenReturn(s3ObjectStream);
		// when(s3ObjectStream.readAllBytes())
		// 	.thenThrow(new IOException("Failed to read stream"));

		// when & then
		assertThrows(InternalErrorException.class, () -> {
			s3Service.getContentAsString(bucketName, key);
		});
	}
}