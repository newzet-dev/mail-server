package com.newzet.api.info;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

@Component
public class CustomInfoContributor implements InfoContributor {
	@Value("${info.app.version:unknown}")
	private String appVersion;

	@Override
	public void contribute(Info.Builder builder) {
		builder.withDetail("app", Collections.singletonMap("version", appVersion));
	}
}
