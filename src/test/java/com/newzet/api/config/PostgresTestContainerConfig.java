package com.newzet.api.config;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class PostgresTestContainerConfig implements BeforeAllCallback {
	private static final String POSTGRES_IMAGE = "postgres:15.6";
	private static final String POSTGRES_DB = "test_db";
	private static final String POSTGRES_USER = "test_user";
	private static final String POSTGRES_PASSWORD = "test_password";

	private static final int POSTGRES_PORT = 5432;
	private static GenericContainer<?> postgresContainer;

	@Override
	public void beforeAll(ExtensionContext context) {
		postgresContainer = new GenericContainer<>(DockerImageName.parse(POSTGRES_IMAGE))
			.withExposedPorts(POSTGRES_PORT)
			.withEnv("POSTGRES_DB", POSTGRES_DB)
			.withEnv("POSTGRES_USER", POSTGRES_USER)
			.withEnv("POSTGRES_PASSWORD", POSTGRES_PASSWORD);

		postgresContainer.start();

		// System properties 설정
		System.setProperty("spring.datasource.url",
			"jdbc:postgresql://" + postgresContainer.getHost() + ":" +
				postgresContainer.getMappedPort(POSTGRES_PORT) + "/" + POSTGRES_DB);
		System.setProperty("spring.datasource.username", POSTGRES_USER);
		System.setProperty("spring.datasource.password", POSTGRES_PASSWORD);
		System.setProperty("spring.datasource.driver-class-name", "org.postgresql.Driver");

		System.setProperty("spring.jpa.properties.hibernate.dialect",
			"org.hibernate.dialect.PostgreSQLDialect");
		System.setProperty("spring.jpa.hibernate.ddl-auto", "create-drop");
		System.setProperty("SPRING_JPA_SHOW_SQL", "false");
	}
}
