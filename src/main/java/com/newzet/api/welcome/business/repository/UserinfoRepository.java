package com.newzet.api.welcome.business.repository;

import java.util.Optional;
import java.util.UUID;

import com.newzet.api.welcome.domain.Userinfo;

public interface UserinfoRepository {
	Userinfo findUserinfoById(UUID id);

	Userinfo save(Userinfo userinfo);

	Optional<Userinfo> findOptionalUserinfoByEmail(String email);

	Userinfo findUserinfoByEmail(String email);
}
