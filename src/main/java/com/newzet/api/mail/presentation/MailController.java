package com.newzet.api.mail.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.newzet.api.mail.business.MailServiceFacade;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/functions/v1/mail")
public class MailController {

	private final MailServiceFacade mailServiceFacade;

	@PostMapping
	public ResponseEntity<Object> receive(@RequestBody @Valid MailMetadataDto metadata) {
		mailServiceFacade.processMail(metadata.getFromName(), metadata.getFromDomain(),
			metadata.getToDomain(), metadata.getMailingList(), metadata.getHtmlLink());
		return ResponseEntity.ok().build();
	}
}
