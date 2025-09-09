package com.newzet.api.common.storage;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.newzet.api.common.exception.InternalErrorException;
import com.newzet.api.config.storage.StorageConfig;

@ExtendWith(MockitoExtension.class)
class StorageServiceTest {

	private final String AWS_BUCKET_NAME = "aws-test-bucket";
	private final String SUPABASE_BUCKET_NAME = "supabase-test-bucket";
	private final String OBJECT_KEY = "test-file.txt";

	// @InjectMocks를 제거하고 수동으로 주입
	private StorageService storageService;

	@Mock
	private AmazonS3 awsS3Client;

	@Mock
	private AmazonS3 supabaseS3Client;

	@Mock
	private StorageConfig storageConfig;

	@BeforeEach
	void setUp() {
		// 수동으로 StorageService 인스턴스 생성 및 Mock 주입
		storageService = new StorageService(awsS3Client, supabaseS3Client, storageConfig);
	}

	private void mockS3Object(AmazonS3 client, String expectedContent) {
		byte[] contentBytes = expectedContent.getBytes(StandardCharsets.UTF_8);
		S3Object mockS3Object = new S3Object();
		S3ObjectInputStream inputStream = new S3ObjectInputStream(
			new ByteArrayInputStream(contentBytes), null);
		mockS3Object.setObjectContent(inputStream);
		when(client.getObject(any(GetObjectRequest.class))).thenReturn(mockS3Object);
	}

	@Nested
	@DisplayName("성공 케이스")
	class SuccessCases {

		@Test
		@DisplayName("isSaveInStorage가 false일 때 AWS S3 객체를 성공적으로 읽어와 문자열로 반환한다")
		void getContent_FromAwsS3_Success() {
			// given
			String expectedContent = "AWS S3 Object Content";
			when(storageConfig.getAwsBucketName()).thenReturn(AWS_BUCKET_NAME);
			mockS3Object(awsS3Client, expectedContent);

			// when
			String actualContent = storageService.getContent(OBJECT_KEY, false);

			// then
			assertEquals(expectedContent, actualContent);
		}

		@Test
		@DisplayName("isSaveInStorage가 true일 때 Supabase S3 객체를 성공적으로 읽어와 문자열로 반환한다")
		void getContent_FromSupabaseS3_Success() {
			// given
			String expectedContent = "Supabase S3 Object Content";
			when(storageConfig.getSupabaseBucketName()).thenReturn(SUPABASE_BUCKET_NAME);
			mockS3Object(supabaseS3Client, expectedContent);

			// when
			String actualContent = storageService.getContent(OBJECT_KEY, true);

			// then
			assertEquals(expectedContent, actualContent);
		}
	}

	@Nested
	@DisplayName("실패 케이스")
	class FailureCases {

		@Test
		@DisplayName("S3에 해당 Key의 객체가 없으면 InternalErrorException을 던진다")
		void getContent_ThrowsNoSuchKeyException() {
			// given
			when(storageConfig.getAwsBucketName()).thenReturn(AWS_BUCKET_NAME);
			when(awsS3Client.getObject(any(GetObjectRequest.class)))
				.thenThrow(new AmazonS3Exception("The specified key does not exist."));

			// when & then
			assertThrows(InternalErrorException.class, () -> {
				storageService.getContent(OBJECT_KEY, false);
			});
		}

		@Test
		@DisplayName("스트림을 읽는 중 IOException이 발생하면 InternalErrorException을 던진다")
		void getContent_ThrowsIOException() throws IOException {
			// given
			S3Object mockS3Object = mock(S3Object.class);
			S3ObjectInputStream mockInputStream = mock(S3ObjectInputStream.class);

			when(storageConfig.getAwsBucketName()).thenReturn(AWS_BUCKET_NAME);
			when(awsS3Client.getObject(any(GetObjectRequest.class))).thenReturn(mockS3Object);
			when(mockS3Object.getObjectContent()).thenReturn(mockInputStream);
			when(mockInputStream.readAllBytes()).thenThrow(new IOException("스트림 읽기 실패"));

			// when & then
			assertThrows(InternalErrorException.class, () -> {
				storageService.getContent(OBJECT_KEY, false);
			});
		}
	}
}
