package com.newzet.api.config;

import java.util.List;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.newzet.api.auth.infrastructure.filter.AuthExceptionFilter;
import com.newzet.api.auth.infrastructure.interceptor.JwtAuthInterceptor;
import com.newzet.api.auth.infrastructure.resolver.AuthUserArgumentResolver;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
	private final JwtAuthInterceptor jwtAuthInterceptor;
	private final AuthUserArgumentResolver authUserArgumentResolver;
	private final AuthExceptionFilter authExceptionFilter;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(jwtAuthInterceptor)
			.order(1)
			.addPathPatterns("/**");
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(authUserArgumentResolver);
	}

	@Bean
	public FilterRegistrationBean<AuthExceptionFilter> authExceptionFilterRegistration() {
		FilterRegistrationBean<AuthExceptionFilter> registration = new FilterRegistrationBean<>();
		registration.setFilter(authExceptionFilter);
		registration.addUrlPatterns("/*");
		registration.setName("authExceptionFilter");
		registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return registration;
	}

	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOriginPattern("*");
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		config.addExposedHeader("Authorization");
		config.setMaxAge(3600L);
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}
}
