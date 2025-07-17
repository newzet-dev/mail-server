package com.newzet.api.newsletter.orchestrator;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newzet.api.advertise.business.service.AdvertiseService;
import com.newzet.api.advertise.domain.Advertise;
import com.newzet.api.category.business.service.CategoryService;
import com.newzet.api.category.domain.Category;
import com.newzet.api.newsletter.business.service.NewsletterRecommendationService;
import com.newzet.api.newsletter.business.service.NewsletterService;
import com.newzet.api.newsletter.domain.Newsletter;
import com.newzet.api.newsletter.presentation.dto.NewsletterInfoResponse;
import com.newzet.api.newsletter.presentation.dto.NewsletterListResponse;
import com.newzet.api.newsletter.presentation.dto.NewsletterRecommendResponse;
import com.newzet.api.newsletter.presentation.mapper.NewsletterResponseMapper;
import com.newzet.api.subscription.business.service.SubscriptionService;
import com.newzet.api.usercategory.business.service.UserCategoryService;
import com.newzet.api.usercategory.domain.UserCategory;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NewsletterOrchestrator {
	private final NewsletterService newsletterService;
	private final UserCategoryService userCategoryService;
	private final CategoryService categoryService;
	private final SubscriptionService subscriptionService;
	private final AdvertiseService advertiseService;
	private final NewsletterRecommendationService newsletterRecommendationService;

	@Transactional(readOnly = true)
	public NewsletterListResponse searchNewsletterListByName(String name) {
		List<Newsletter> newsletterList = newsletterService.findNewsletterListByName(name);
		return NewsletterResponseMapper.toListResponse(newsletterList);
	}

	@Transactional(readOnly = true)
	public NewsletterListResponse getNewsletterListByCategoryId(UUID categoryId) {
		List<Newsletter> newsletterList = newsletterService.findNewsletterListByCategoryId(categoryId);
		return NewsletterResponseMapper.toListResponse(newsletterList);
	}

	@Transactional(readOnly = true)
	public NewsletterInfoResponse getNewsletterInfoWithoutLogin(UUID id) {
		Newsletter newsletter = newsletterService.findNewsLetterById(id);
		Category category = categoryService.getCategoryById(newsletter.getCategoryId());
		return NewsletterResponseMapper.toInfoResponse(newsletter, category, false);
	}

	@Transactional(readOnly = true)
	public NewsletterInfoResponse getNewsLetterInfoWithLogin(UUID userId, UUID id) {
		Newsletter newsletter = newsletterService.findNewsLetterById(id);
		Category category = categoryService.getCategoryById(newsletter.getCategoryId());
		boolean isSubscribing = subscriptionService.isSubscribing(userId, newsletter.getDomain(),
			newsletter.getMailingList());
		return NewsletterResponseMapper.toInfoResponse(newsletter, category, isSubscribing);
	}

	@Transactional(readOnly = true)
	public NewsletterRecommendResponse recommendNewsletterList(UUID userId) {
		List<Newsletter> advertiseNewsletterList = prepareAdvertiseNewsletterList();
		List<Newsletter> userCategoryNewsletterList = prepareUserCategoryNewsletterList(userId);
		List<Newsletter> recommendNewsletterList = newsletterRecommendationService.recommendNewsletterList(
			advertiseNewsletterList, userCategoryNewsletterList);
		return NewsletterResponseMapper.toRecommendResponse(recommendNewsletterList);
	}
	
	private List<Newsletter> prepareAdvertiseNewsletterList() {
		List<UUID> advertiseNewsletterIdList = advertiseService.getAdvertiseList().stream()
			.map(Advertise::getNewsletterId)
			.toList();
		return newsletterService.findNewsletterListByIdList(
			advertiseNewsletterIdList);
	}

	private List<Newsletter> prepareUserCategoryNewsletterList(UUID userId) {
		List<UUID> userCategoryIdList = userCategoryService.findCategoryListByUserId(userId).stream()
			.map(UserCategory::getId)
			.toList();
		return newsletterService.findNewsletterListByCategoryIdList(userCategoryIdList);
	}
}
