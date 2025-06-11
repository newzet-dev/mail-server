package com.newzet.api.newsletter.business.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.newzet.api.newsletter.business.repository.NewsletterRepository;
import com.newzet.api.newsletter.domain.Newsletter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsletterService {

	private final NewsletterRepository newsletterRepository;

	public List<Newsletter> findNewsletterListByName(String name) {
		return newsletterRepository.findNewsLetterListByName(name);
	}

	public List<Newsletter> findNewsletterListByCategoryId(UUID categoryId) {
		return newsletterRepository.findNewsLetterListByCategoryId(categoryId);
	}

	public Newsletter findNewsLetterById(UUID id) {
		return newsletterRepository.findById(id);
	}

	public List<Newsletter> findNewsletterListByIdList(List<UUID> idList) {
		return newsletterRepository.findNewsletterListByIdList(idList);

	}

	public List<Newsletter> findNewsletterListByCategoryIdList(List<UUID> categoryIdList) {
		return newsletterRepository.findNewsletterListByCategoryIdList(categoryIdList);
	}
}
