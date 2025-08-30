package com.newzet.api.common.s3;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.newzet.api.common.exception.InternalErrorException;
import com.newzet.api.config.s3.S3Config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {
	private final AmazonS3 amazonS3;
	private final S3Config s3Config;

	public String getContentAsString(String key) {
		GetObjectRequest getObjectRequest = new GetObjectRequest(s3Config.getContentBucketName(), key);

		try (S3Object s3Object = amazonS3.getObject(getObjectRequest);
			 S3ObjectInputStream inputStream = s3Object.getObjectContent()) {

			byte[] contentBytes = inputStream.readAllBytes();
			return new String(contentBytes, StandardCharsets.UTF_8);

		} catch (AmazonS3Exception e) {
			log.error("S3에서 객체를 가져오는 중 오류가 발생했습니다. Key: {}", key, e);
			throw new InternalErrorException("아티클을 불러오는 과정에서 에러가 발생하였습니다.");
		} catch (IOException e) {
			log.error("S3 파일 내용을 읽는 중 I/O 오류가 발생했습니다.", e);
			throw new InternalErrorException("아티클을 불러오는 과정에서 에러가 발생하였습니다.");
		}
	}
}
