package com.newzet.api.common.cache;

import java.util.Optional;

public interface CacheUtil {
	public <T> Optional<T> get(String key, Class<T> classType);

	public <T> Boolean set(String key, T object, long ttl);
}
