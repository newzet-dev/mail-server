package com.newzet.api.user.repository;

import com.newzet.api.user.domain.ActiveUser;

public interface ActiveUserRepository {
	ActiveUser findActiveUserByEmail(String email);
}
