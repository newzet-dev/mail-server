package com.newzet.api.mail.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.newzet.api.mail.business.MailServiceFacade;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/functions/v1/mail")
@Tag(name = "메일", description = "메일 관련 API")
@Slf4j
public class MailController {

	private final MailServiceFacade mailServiceFacade;

	@PostMapping
	@Operation(summary = "메일 수신", description = "메일을 수신하며 전달받은 메타데이터를 기반으로 뉴스레터 및 구독 관련 로직을 처리한다.")
	public ResponseEntity<Object> receive(@RequestBody @Valid MailMetadataDto metadata) {
		log.info("✅ Mail received. metadata: {}", metadata);
		mailServiceFacade.processMail(metadata.getFromName(), metadata.getFromDomain(),
			metadata.getToDomain(), metadata.getMailingList(), metadata.getHtmlLink(),
			metadata.getTitle());
		return ResponseEntity.ok().build();
	}
}
