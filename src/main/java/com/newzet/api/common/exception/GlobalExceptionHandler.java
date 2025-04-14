package com.newzet.api.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.newzet.api.common.response.ResponseCode;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(NewzetException.class)
	public ProblemDetail handleNewzetEx(NewzetException e) {
		ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.OK);
		problemDetail.setTitle("Newzet Service Error");
		problemDetail.setProperty("code", e.getResponseCode().getCode());
		problemDetail.setProperty("message", e.getMessage());
		return problemDetail;
	}

	@ExceptionHandler(InternalErrorException.class)
	public ProblemDetail handleInternalErrorEx(InternalErrorException e) {
		ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.OK);
		problemDetail.setTitle("Internal Error");
		problemDetail.setProperty("code", "내부에서 요청 처리에 실패하였습니다. 다시 시도해주세요.");
		problemDetail.setProperty("message", e.getMessage());
		return problemDetail;
	}

	@ExceptionHandler(Exception.class)
	public ProblemDetail handleUnknownException(Exception e) {
		ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.OK);
		problemDetail.setTitle("Unknown Error");
		problemDetail.setProperty("code", ResponseCode.SERVER_ERROR);
		problemDetail.setProperty("message", "알 수 없는 서버 내부 오류가 발생하였습니다.");
		return problemDetail;
	}
}
