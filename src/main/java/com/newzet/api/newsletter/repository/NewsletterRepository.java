package com.newzet.api.newsletter.repository;

import com.newzet.api.newsletter.domain.Newsletter;

public interface NewsletterRepository {
	Newsletter findNewsletter(String domain, String maillingList);
}
