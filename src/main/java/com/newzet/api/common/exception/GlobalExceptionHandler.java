package com.newzet.api.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(NewzetException.class)
	public ProblemDetail handleNewzetEx(NewzetException e) {
		ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.OK);
		problemDetail.setTitle("Newzet Error");
		problemDetail.setDetail(e.getMessage());
		problemDetail.setProperty("responseCode", e.getResponseCode());
		return problemDetail;
	}
}
