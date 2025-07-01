package com.newzet.api.config.fcm;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;

@Configuration
public class FirebaseConfig {

	@Value("${firebase.project-id}")
	private String projectId;

	@Value("${firebase.private-key-id}")
	private String privateKeyId;

	@Value("${firebase.private-key}")
	private String privateKey;

	@Value("${firebase.client-email}")
	private String clientEmail;

	@Value("${firebase.client-id}")
	private String clientId;

	@Bean
	public FirebaseMessaging firebaseMessaging() throws IOException {
		String serviceAccountJson = createServiceAccountJson();

		InputStream serviceAccount = new ByteArrayInputStream(serviceAccountJson.getBytes());

		FirebaseOptions options = FirebaseOptions.builder()
			.setCredentials(GoogleCredentials.fromStream(serviceAccount))
			.setProjectId(projectId)
			.build();

		if (FirebaseApp.getApps().isEmpty()) {
			FirebaseApp.initializeApp(options);
		}

		return FirebaseMessaging.getInstance();
	}

	private String createServiceAccountJson() {
		return String.format("""
				{
				  "type": "service_account",
				  "project_id": "%s",
				  "private_key_id": "%s",
				  "private_key": "%s",
				  "client_email": "%s",
				  "client_id": "%s",
				  "auth_uri": "https://accounts.google.com/o/oauth2/auth",
				  "token_uri": "https://oauth2.googleapis.com/token",
				  "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
				  "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/%s",
				  "universe_domain": "googleapis.com"
				}
				""",
			projectId,
			privateKeyId,
			privateKey.replace("\\n", "\n"),
			clientEmail,
			clientId,
			clientEmail.replace("@", "%40")
		);
	}
}
