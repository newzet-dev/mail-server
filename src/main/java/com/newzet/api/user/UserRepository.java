package com.newzet.api.user;

public interface UserRepository {
	ActiveUser findActiveUserByEmail(String email);
}
