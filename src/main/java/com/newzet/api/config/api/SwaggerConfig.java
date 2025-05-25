package com.newzet.api.config.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {
	public static final String BEARER_AUTH = "BearerAuth";

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
			.components(new Components())
			.info(createApiInfo())
			.addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH))
			.schemaRequirement(BEARER_AUTH, createSecurityScheme());
	}

	private Info createApiInfo() {
		return new Info()
			.title("뉴젯 API 명세서")
			.description(
				"[Team Notion 바로가기](https://www.notion.so/1c8b6c7804e2804081c3fd2647c58a47)")
			.version("0.0.1");
	}

	public SecurityScheme createSecurityScheme() {
		return new SecurityScheme()
			.type(SecurityScheme.Type.HTTP)
			.scheme("bearer")
			.bearerFormat("JWT")
			.in(SecurityScheme.In.HEADER)
			.name("Authorization");
	}
}
