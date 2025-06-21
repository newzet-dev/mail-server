package com.newzet.api.userinfo.business.repository;

import java.util.Optional;
import java.util.UUID;

import com.newzet.api.userinfo.domain.Userinfo;

public interface UserinfoRepository {
	Userinfo findUserinfoById(UUID id);

	Userinfo save(Userinfo userinfo);

	Optional<Userinfo> findOptionalUserinfoByEmail(String email);

	Userinfo findUserinfoByEmail(String email);
}
