package com.newzet.api.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(NewzetException.class)
	public ResponseEntity<ResponseFormat> handleNewzetEx(NewzetException e) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(new BasicResponseFormat(e.getResponseCode(), e.getMessage()));
	}
}
