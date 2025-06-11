package com.newzet.api.user.controller;

import com.newzet.api.common.response.ResponseCode;
import com.newzet.api.common.response.SuccessResponse;
import com.newzet.api.user.business.dto.UniqueMailResponse;
import com.newzet.api.user.business.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "유저", description = "유저 관련 API")
@RequestMapping("/api")
public class UserController {

    private final UserService userService;


    @GetMapping("/my/mail")
    @Operation(summary = "메일 중복조회",
            description = "메일이 사용 가능한지 조회한다. 휴면유저/탈퇴한 유저의 메일도 사용 불가.")
    public ResponseEntity<SuccessResponse<UniqueMailResponse>> checkEmailUniqueness(
            @RequestParam("v") String email) {
        UniqueMailResponse result = userService.checkEmailUniqueness(email);
        SuccessResponse<UniqueMailResponse> response = SuccessResponse.create(
                ResponseCode.SUCCESS, "메일 중복조회 성공", result
        );
        return ResponseEntity.ok(response);
    }

    //TODO: 로그아웃
    //TODO: 회원 탈퇴
    //TODO: user 정보 수정
    //TODO: 유저 정보 등록 여부 확인
}
