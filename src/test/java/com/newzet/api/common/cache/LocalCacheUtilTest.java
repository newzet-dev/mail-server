package com.newzet.api.common.cache;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.newzet.api.common.cache.local.CacheEntry;
import com.newzet.api.common.cache.local.LocalCacheUtil;
import com.newzet.api.newsletter.business.dto.NewsletterImageUrlCacheDto;

public class LocalCacheUtilTest {

	private LocalCacheUtil localCacheUtil;

	@BeforeEach
	void setUp() {
		Cache<String, CacheEntry<?>> cache = Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build();
		localCacheUtil = new LocalCacheUtil(cache);
	}

	@Test
	public void get_whenValueIsValid_returnOptionalValue() {
		//Given
		String key = "testKey";
		String value = "testValue";
		long ttl = 1000L;

		localCacheUtil.set(key, value, ttl);
		assertTrue(localCacheUtil.get(key, String.class).isPresent());

		//When
		Optional<String> cachedValue = localCacheUtil.get(key, String.class);

		//Then
		assertTrue(cachedValue.isPresent());
		assertEquals(value, cachedValue.get());
	}

	@Test
	public void get_whenValueNotExist_returnOptionalEmpty() {
		//Given
		String key = "testKey";

		//When
		Optional<String> cachedValue = localCacheUtil.get(key, String.class);

		//Then
		assertFalse(cachedValue.isPresent());
	}

	@Test
	public void get_whenValueIsExpired_returnOptionalEmpty() throws InterruptedException {
		//Given
		String key = "testKey";
		String value = "testValue";
		long ttl = 1000L;

		localCacheUtil.set(key, value, ttl);
		assertTrue(localCacheUtil.get(key, String.class).isPresent());

		//When
		Thread.sleep(1100);
		Optional<String> cachedValue = localCacheUtil.get(key, String.class);

		//Then
		assertFalse(localCacheUtil.get(key, String.class).isPresent());
	}

	@DisplayName("로컬 캐시에서 두 개의 key에 하나의 value를 매핑할때, 동일한 value를 참조하는지 테스트")
	@Test
	void two_keys_have_same_value_object() {
		// Given
		String domainKey = "nImgUrl:domain:" + "domain@domain.com";
		String mListKey = "nImgUrl:mList:" + "mList";
		long ttl = 3000L;
		NewsletterImageUrlCacheDto cacheDto = NewsletterImageUrlCacheDto.create("https://a.com");

		// When
		localCacheUtil.set(domainKey, cacheDto, ttl);
		localCacheUtil.set(mListKey, cacheDto, ttl);

		// Then
		assertSame(localCacheUtil.get(domainKey, NewsletterImageUrlCacheDto.class).get(),
			localCacheUtil.get(mListKey, NewsletterImageUrlCacheDto.class).get());
	}
}
