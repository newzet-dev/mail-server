package com.newzet.api.common.s3;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.newzet.api.common.exception.InternalErrorException;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

	private final String BUCKET_NAME = "test-bucket";
	private final String OBJECT_KEY = "test-file.txt";
	@InjectMocks
	private S3Service s3Service;
	@Mock
	private AmazonS3 amazonS3; // S3Service가 사용하는 SDK v1 클라이언트

	@Nested
	@DisplayName("성공 케이스")
	class SuccessCases {
		@Test
		@DisplayName("S3 객체를 성공적으로 읽어와 문자열로 반환한다")
		void getContentAsString_Success() {
			// given
			String expectedContent = "S3 Object Content";
			byte[] contentBytes = expectedContent.getBytes(StandardCharsets.UTF_8);

			// S3Object와 그 내용을 Mocking
			S3Object mockS3Object = new S3Object();
			S3ObjectInputStream inputStream = new S3ObjectInputStream(
				new ByteArrayInputStream(contentBytes), null);
			mockS3Object.setObjectContent(inputStream);

			when(amazonS3.getObject(any(GetObjectRequest.class))).thenReturn(mockS3Object);

			// when
			String actualContent = s3Service.getContentAsString(BUCKET_NAME, OBJECT_KEY);

			// then
			assertEquals(expectedContent, actualContent);
		}
	}

	@Nested
	@DisplayName("실패 케이스")
	class FailureCases {
		@Test
		@DisplayName("S3에 해당 Key의 객체가 없으면 InternalErrorException을 던진다")
		void getContentAsString_ThrowsNoSuchKeyException() {
			// given
			when(amazonS3.getObject(any(GetObjectRequest.class)))
				.thenThrow(new AmazonS3Exception("The specified key does not exist."));

			// when & then
			assertThrows(InternalErrorException.class, () -> {
				s3Service.getContentAsString(BUCKET_NAME, OBJECT_KEY);
			});
		}

		@Test
		@DisplayName("스트림을 읽는 중 IOException이 발생하면 InternalErrorException을 던진다")
		void getContentAsString_ThrowsIOException() throws IOException {
			// given
			// S3Object와 InputStream을 각각 Mocking하여 InputStream의 동작을 제어
			S3Object mockS3Object = mock(S3Object.class);
			S3ObjectInputStream mockInputStream = mock(S3ObjectInputStream.class);

			when(amazonS3.getObject(any(GetObjectRequest.class))).thenReturn(mockS3Object);
			when(mockS3Object.getObjectContent()).thenReturn(mockInputStream);

			// 핵심: InputStream에서 readAllBytes() 호출 시 강제로 IOException 발생
			when(mockInputStream.readAllBytes()).thenThrow(new IOException("스트림 읽기 실패"));

			// when & then
			assertThrows(InternalErrorException.class, () -> {
				s3Service.getContentAsString(BUCKET_NAME, OBJECT_KEY);
			});
		}
	}
}