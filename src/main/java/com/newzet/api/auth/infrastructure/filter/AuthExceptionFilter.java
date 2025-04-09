package com.newzet.api.auth.infrastructure.filter;

import java.io.IOException;
import java.nio.file.AccessDeniedException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newzet.api.auth.exception.AccessTokenExpiredException;
import com.newzet.api.auth.exception.JWTBadRequestException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthExceptionFilter implements Filter {

	private final ObjectMapper objectMapper;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
		HttpServletResponse httpResponse = (HttpServletResponse)response;
		try {
			chain.doFilter(request, response);
		} catch (JWTBadRequestException e) {
			handleException(httpResponse, HttpStatus.UNAUTHORIZED, "AUTHENTICATION_FAILED",
				e.getMessage());
		} catch (AccessTokenExpiredException e) {
			handleException(httpResponse, HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED", e.getMessage());
		} catch (AccessDeniedException e) {
			handleException(httpResponse, HttpStatus.FORBIDDEN, "ACCESS_DENIED", e.getMessage());
		} catch (ServletException e) {
			Throwable rootCause = e.getRootCause();
			if (rootCause instanceof JWTBadRequestException) {
				handleException(httpResponse, HttpStatus.UNAUTHORIZED, "AUTHENTICATION_FAILED",
					rootCause.getMessage());
			} else {
				handleException(httpResponse, HttpStatus.INTERNAL_SERVER_ERROR,
					"INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다.");
			}
		} catch (IOException e) {
			handleException(httpResponse, HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR",
				"서버 내부 오류가 발생했습니다.");
		}
	}

	private void handleException(HttpServletResponse response, HttpStatus status, String code,
		String message) {
		try {
			if (!response.isCommitted()) {
				response.setStatus(status.value());
				response.setCharacterEncoding("UTF-8");
				response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);

				ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, message);
				problemDetail.setTitle(status.getReasonPhrase());
				problemDetail.setProperty("code", code);

				objectMapper.writeValue(response.getWriter(), problemDetail);
			}
		} catch (IOException ex) {
			log.error("예외 처리 중 오류 발생", ex);
		}
	}
}
