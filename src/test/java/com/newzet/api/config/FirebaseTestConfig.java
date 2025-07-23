package com.newzet.api.config;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class FirebaseTestConfig implements BeforeAllCallback {

	@Override
	public void beforeAll(ExtensionContext context) {
		System.setProperty("FIREBASE_PROJECT_ID", "test-project-id");
		System.setProperty("FIREBASE_PRIVATE_KEY_ID", "test-private-key-id");
		System.setProperty("FIREBASE_PRIVATE_KEY",
			"-----BEGIN PRIVATE KEY-----\\n" +
				"MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDHfbsrFLUnLmJg\\n" +
				"tHqgmO4mcShpI37s10AH6TS4C9exfWcaoT4Ty7vY/fvhgHLpSEe3veqp2Rv8FkM7\\n" +
				"yryeoT+VItgPDq7CAOmjaoshY/DTvsJXfbw6uZ4JWbD96gM4C2qoTHZcI0n+kAbB\\n" +
				"TO3mvgH65GXQB5hGmuqDTacQ2eq5d2XOQV+OaiYKvEUUhnXCpleQjtimSt1HNoym\\n" +
				"MPYg5YuPRpGMa2nQsSl+yPD1n/CtHOnVF8iJMN5D72lySjk5TmcUAaI6PRhJNxna\\n" +
				"vKoVQYTmVoCcgfbNGVRfubfa0F/V/oO3rEGaqLmDim+9qH40Zxy/2p7OeIwXHLyt\\n" +
				"+MzT+1p1AgMBAAECggEADYitT4gT8tNgmjtJodFDlY5navFHtaK0IIqaPdg+gGHy\\n" +
				"bOnpL7z1LWIBxMk1l+JZoeYRCwfZ2wQu+ISnFO358HtmLr3/bir+BCJWIQ703Ntc\\n" +
				"DO/QpX1H8vjHm1pVGz1vCs7xLWFkLjswMiIEISYyioZp6oScDeze9xx9YT/KKbMz\\n" +
				"Ujc0rdAiWkHtar28HinQzLBl2Y2qUywD5gVl8GuzlMgrZO5eDlsHn8lQSWT6KsAq\\n" +
				"DkoLjb+jkYVQvdf6kI/h99PDZITaHZXjIpyvQlxmYcILSPn1HvCH1FRIamiyDIjt\\n" +
				"ZB1SynhlGmAy/KB6q49yzERKROYxI4z6YnwjWhTrWwKBgQD6btD595ZX5ocC0uXs\\n" +
				"lXHMK7h2YRM6QXExHX+BT4Q3QDSg4Vl7fIv06qzjP+Wb+he7llHwSr3JAus55kuZ\\n" +
				"+8pou13+Y4zBAKhtIOyuwtbIx2MoeZNQ5RT2pNHQmtbPnBBw6vLnNRBwKl7vdPfc\\n" +
				"RI1m81i4czvDIJIYtee5nssVuwKBgQDL7QLts6VquHlB8vyGOg76KdtfJ4aLYIg8\\n" +
				"GqKzFFNKNHbfjWIeRLqQJbLHNB/dljPlmqHZNyelyLH88jOW1OPveDKaMoLbPtiB\\n" +
				"YSTAyqYwY9PMf/HRn5Dl1SejJzqvqPhMOLBlSASdXgvv7+XE7qnLY0AWIo3wRk8I\\n" +
				"OTbsLTe1jwKBgQDm6P7vPQb3Daw3QlaWikVfSIDRRjkAYg8IhnZmuPbkKuNb4+0q\\n" +
				"G3DA5xF6iBQiebsgUD5FHeVYTsStolbbKHs9jmXghdHms8CYvt79VNHOV2pqi471\\n" +
				"7AQkV1zOx7aBvxi5xSkrXpZFlgvrJyLTirIG1yJbEIVuKb4L4s5DLNN8uQKBgA5g\\n" +
				"9tzl1tsQiNRCmtWoEFhJTUOHWPBI7TI3upMf8sN/sYYPxQRXWkRBtDphjYGlTqF4\\n" +
				"5sKXJf+FiC9KsKKI/k1rTz4aI6nr434z6FCDuXYeA9geiWF7e88I2ZOid3vdUSym\\n" +
				"rqFlk5W5BOR1KOFa5rQFmoY1B4cSng35YssCYTQ3AoGBALlF2QZId/v6rRiwJJPp\\n" +
				"kf1e9bNneLk6q/7JG+MQw+rSHzlLgrp+nRFi2jqjH32e7zFVSoaMunaqzncgLLfB\\n" +
				"sIYKICkexMIt+Amg4y3a0xtdTbc5Qz439EEYYyGI162SOmrniMrM2XV3knUlEPOl\\n" +
				"xDwsCn2Aig7xYK1Ac4FQPDNW\\n" +
				"-----END PRIVATE KEY-----\\n"
		);
		System.setProperty("FIREBASE_CLIENT_EMAIL", "test@test-project.iam.gserviceaccount.com");
		System.setProperty("FIREBASE_CLIENT_ID", "100768672478630755715");
	}
}
