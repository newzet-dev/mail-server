package com.newzet.api.userinfo.business.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.newzet.api.userinfo.business.repository.UserinfoRepository;
import com.newzet.api.userinfo.domain.UserRole;
import com.newzet.api.userinfo.domain.Userinfo;

@ExtendWith(MockitoExtension.class)
public class UserinfoServiceTest {

	private final UUID TEST_USER_ID = UUID.randomUUID();
	private final String TEST_EMAIL = "test@example.com";
	private final String TEST_NICKNAME = "testUser";

	@Mock
	private UserinfoRepository userinfoRepository;

	@InjectMocks
	private UserinfoService userinfoService;

	@Test
	@DisplayName("ID로 Userinfo를 찾을 수 있으면 해당 객체를 반환한다.")
	public void findUserinfoById_whenUserExists_shouldReturnUserinfo() {
		// Given
		Userinfo userinfo = new Userinfo(TEST_USER_ID, TEST_EMAIL, TEST_NICKNAME, UserRole.MEMBER, LocalDateTime.now(),
			null);
		when(userinfoRepository.findUserinfoById(TEST_USER_ID)).thenReturn(userinfo);

		// When
		Userinfo foundUserinfo = userinfoService.findUserinfoById(TEST_USER_ID);

		// Then
		assertNotNull(foundUserinfo);
		assertEquals(TEST_USER_ID, foundUserinfo.getId());
		assertEquals(TEST_EMAIL, foundUserinfo.getEmail());
		assertEquals(TEST_NICKNAME, foundUserinfo.getNickname());
		verify(userinfoRepository, times(1)).findUserinfoById(TEST_USER_ID);
	}

	@Test
	@DisplayName("이메일이 존재하지 않으면 true를 반환한다.")
	public void isUniqueEmailInUserinfo_whenEmailDoesNotExist_shouldReturnTrue() {
		// Given
		when(userinfoRepository.findOptionalUserinfoByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

		// When
		boolean isUnique = userinfoService.isUniqueEmailInUserinfo(TEST_EMAIL);

		// Then
		assertTrue(isUnique);
		verify(userinfoRepository, times(1)).findOptionalUserinfoByEmail(TEST_EMAIL);
	}

	@Test
	@DisplayName("이메일이 이미 존재하면 false를 반환한다.")
	public void isUniqueEmailInUserinfo_whenEmailExists_shouldReturnFalse() {
		// Given
		Userinfo userinfo = new Userinfo(TEST_USER_ID, TEST_EMAIL, TEST_NICKNAME, UserRole.MEMBER, LocalDateTime.now(),
			null);
		when(userinfoRepository.findOptionalUserinfoByEmail(TEST_EMAIL)).thenReturn(Optional.of(userinfo));

		// When
		boolean isUnique = userinfoService.isUniqueEmailInUserinfo(TEST_EMAIL);

		// Then
		assertFalse(isUnique);
		verify(userinfoRepository, times(1)).findOptionalUserinfoByEmail(TEST_EMAIL);
	}

	@Test
	@DisplayName("이메일이 null이 아니면 true를 반환한다.")
	public void isInitialized_whenEmailIsNotNull_shouldReturnTrue() {
		// Given
		Userinfo userinfo = new Userinfo(TEST_USER_ID, TEST_EMAIL, TEST_NICKNAME, UserRole.MEMBER, LocalDateTime.now(),
			null);
		when(userinfoRepository.findUserinfoById(TEST_USER_ID)).thenReturn(userinfo);

		// When
		boolean isInitialized = userinfoService.isInitialized(TEST_USER_ID);

		// Then
		assertTrue(isInitialized);
		verify(userinfoRepository, times(1)).findUserinfoById(TEST_USER_ID);
	}

	@Test
	@DisplayName("이메일이 null이면 false를 반환한다.")
	public void isInitialized_whenEmailIsNull_shouldReturnFalse() {
		// Given
		Userinfo userinfo = new Userinfo(TEST_USER_ID, null, TEST_NICKNAME, UserRole.MEMBER, LocalDateTime.now(), null);
		when(userinfoRepository.findUserinfoById(TEST_USER_ID)).thenReturn(userinfo);

		// When
		boolean isInitialized = userinfoService.isInitialized(TEST_USER_ID);

		// Then
		assertFalse(isInitialized);
		verify(userinfoRepository, times(1)).findUserinfoById(TEST_USER_ID);
	}

	@Test
	@DisplayName("이메일로 Userinfo를 찾을 수 있으면 해당 객체를 반환한다.")
	public void findUserinfoByEmail_whenUserExists_shouldReturnUserinfo() {
		// Given
		Userinfo userinfo = new Userinfo(TEST_USER_ID, TEST_EMAIL, TEST_NICKNAME, UserRole.MEMBER, LocalDateTime.now(),
			null);
		when(userinfoRepository.findUserinfoByEmail(TEST_EMAIL)).thenReturn(userinfo);

		// When
		Userinfo foundUserinfo = userinfoService.findUserinfoByEmail(TEST_EMAIL);

		// Then
		assertNotNull(foundUserinfo);
		assertEquals(TEST_EMAIL, foundUserinfo.getEmail());
		verify(userinfoRepository, times(1)).findUserinfoByEmail(TEST_EMAIL);
	}
}
