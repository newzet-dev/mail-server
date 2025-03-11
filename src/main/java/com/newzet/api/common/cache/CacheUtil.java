package com.newzet.api.common.cache;

import java.util.Optional;

public interface CacheUtil {
	public <T> Optional<T> get(String key, Class<T> classType);

	public Boolean set(String key, Object object, long ttl);

	public void deleteAllKeys();
}
