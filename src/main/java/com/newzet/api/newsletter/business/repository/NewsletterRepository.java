package com.newzet.api.newsletter.business.repository;

import java.util.List;
import java.util.UUID;

import com.newzet.api.newsletter.business.dto.NewsletterSaveRequest;
import com.newzet.api.newsletter.domain.Newsletter;

public interface NewsletterRepository {

	Newsletter save(NewsletterSaveRequest request);

	List<Newsletter> findNewsLetterListByName(String name);

	List<Newsletter> findNewsLetterListByCategoryId(UUID categoryId);

	Newsletter findById(UUID id);

	List<Newsletter> findNewsletterListByCategoryIdList(List<UUID> categoryIdList);

	List<Newsletter> findNewsletterListByIdList(List<UUID> idList);
}
