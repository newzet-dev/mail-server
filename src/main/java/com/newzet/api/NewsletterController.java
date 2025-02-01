package com.newzet.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.newzet.api.newsletter.business.NewsletterService;
import com.newzet.api.newsletter.domain.Newsletter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("newsletter")
@Slf4j
public class NewsletterController {

	private final NewsletterService newsletterService;

	@GetMapping
	public Newsletter getNewsletter() {
		return newsletterService.findOrCreateNewsletter("test", "", "159161.list-id.stibee.com");
	}
}
