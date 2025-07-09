package com.newzet.api.userinfo.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.newzet.api.config.PostgresTestContainerConfig;
import com.newzet.api.userinfo.domain.UserRole;
import com.newzet.api.userinfo.domain.Userinfo;
import com.newzet.api.userinfo.exception.NoUserinfoException;
import com.newzet.api.userinfo.jpa.repository.UserinfoRepositoryImpl;

@DataJpaTest
@Import(UserinfoRepositoryImpl.class)
@ExtendWith(PostgresTestContainerConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserinfoRepositoryImplTest {

	@Autowired
	private UserinfoRepositoryImpl userinfoRepository;

	@Test
	public void findUserinfoByEmail_whenUserinfoExist_returnUserinfo() {
		//Given
		Userinfo saved = userinfoRepository.save(createUserinfo());

		//When
		Userinfo founded = userinfoRepository.findUserinfoByEmail(saved.getEmail());

		//Then
		verifySameUserinfo(saved, founded);
	}

	@Test
	public void findUserinfoByEmail_whenUserNoExist_throwNoUserinfoException() {
		//When, Then
		assertThrows(NoUserinfoException.class, () -> userinfoRepository.findUserinfoByEmail("test"));
	}

	@Test
	public void findUserinfoById_whenUserinfoExist_returnUserinfo() {
		//Given
		Userinfo saved = userinfoRepository.save(createUserinfo());

		//When
		Userinfo founded = userinfoRepository.findUserinfoById(saved.getId());

		//Then
		verifySameUserinfo(saved, founded);
	}

	@Test
	public void findUserinfoById_whenUserNoExist_throwNoUserinfoException() {
		//When, Then
		assertThrows(NoUserinfoException.class, () -> userinfoRepository.findUserinfoById(UUID.randomUUID()));
	}

	private Userinfo createUserinfo() {
		return new Userinfo(null, "test@test.com", "test", UserRole.MEMBER, LocalDateTime.now(),
			LocalDateTime.now());
	}

	private void verifySameUserinfo(Userinfo saved, Userinfo founded) {
		assertEquals(saved.getId(), founded.getId());
		assertEquals(saved.getEmail(), founded.getEmail());
		assertEquals(saved.getNickname(), founded.getNickname());
		assertEquals(saved.getRole(), founded.getRole());
		assertEquals(saved.getCreatedAt(), founded.getCreatedAt());
		assertEquals(saved.getDeletedAt(), founded.getDeletedAt());
	}
}
