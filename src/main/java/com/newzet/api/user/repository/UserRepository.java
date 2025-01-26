package com.newzet.api.user.repository;

import com.newzet.api.user.domain.MailReciepient;

public interface UserRepository {
	MailReciepient findMailRecipientByEmail(String email);
}
