package com.newzet.api.common.cache;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.newzet.api.common.cache.local.CacheEntry;

public class CacheEntryTest {

	@Test
	public void isExpired_whenBeforeTTL_returnFalse() {
		//When
		CacheEntry<String> entry = new CacheEntry<>("test", 1000);
		boolean isExpired = entry.isExpired();

		//Then
		Assertions.assertFalse(isExpired);
	}

	@Test
	public void isExpired_whenAfterTTL_returnTrue() throws InterruptedException {
		//When
		CacheEntry<String> entry = new CacheEntry<>("test", 100);
		Thread.sleep(110);
		boolean isExpired = entry.isExpired();

		//Then
		Assertions.assertTrue(isExpired);
	}
}
