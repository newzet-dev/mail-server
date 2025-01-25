package com.newzet.api.user;

public interface UserRepository {
	MailReciepient findMailRecipientByEmail(String email);
}
