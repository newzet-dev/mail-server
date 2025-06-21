package com.newzet.api.userinfo.business.repository;

import java.util.UUID;

import com.newzet.api.userinfo.domain.Userinfo;

public interface UserinfoRepository {
	Userinfo getUserinfoById(UUID id);

	Userinfo save(Userinfo userinfo);
}
