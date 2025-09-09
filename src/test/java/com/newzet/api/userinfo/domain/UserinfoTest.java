package com.newzet.api.userinfo.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.assertj.core.api.Assertions.*;
import java.util.UUID;
import java.time.LocalDateTime;

class UserinfoTest {

    private Userinfo userinfo;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        userinfo = new Userinfo(
                userId,
                "test@example.com",
                "testNickname",
                UserRole.MEMBER,
                LocalDateTime.now(),
                null
        );
    }

    @Test
    @DisplayName("이메일 변경 - 정상 케이스")
    void changeEmail_withValidEmail() {
        // given
        String newEmail = "  new@example.com  ";

        // when
        userinfo.changeEmail(newEmail);

        // then
        assertThat(userinfo.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    @DisplayName("이메일 변경 - null인 경우")
    void changeEmail_withNull() {
        // given
        String originalEmail = userinfo.getEmail();

        // when
        userinfo.changeEmail(null);

        // then
        assertThat(userinfo.getEmail()).isEqualTo(originalEmail);
    }

    @Test
    @DisplayName("닉네임 변경 테스트")
    void changeNickname() {
        // given
        String newNickname = "  newNickname  ";

        // when
        userinfo.changeNickname(newNickname);

        // then
        assertThat(userinfo.getNickname()).isEqualTo("newNickname");
    }

    @Test
    @DisplayName("사용자 삭제 테스트")
    void delete() {
        // given
        assertThat(userinfo.getDeletedAt()).isNull();

        // when
        userinfo.delete();

        // then
        assertThat(userinfo.getDeletedAt()).isNotNull();
        assertThat(userinfo.getDeletedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }
}
