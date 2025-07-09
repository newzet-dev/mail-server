// package com.newzet.api.newsletter.recommend;
//
// import static org.assertj.core.api.Assertions.*;
// import static org.junit.jupiter.api.Assertions.*;
//
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.Collections;
// import java.util.List;
// import java.util.UUID;
//
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
//
// import com.newzet.api.category.domain.Category;
// import com.newzet.api.newsletter.business.exception.NotEnoughNewslettersException;
// import com.newzet.api.newsletter.business.service.NewsletterRecommendationService;
// import com.newzet.api.newsletter.business.service.RandomRecommendationStrategy;
// import com.newzet.api.newsletter.business.service.RecommendationStrategy;
// import com.newzet.api.newsletter.domain.Newsletter;
//
// class NewsletterRecommendationServiceTest {
// 	private final RecommendationStrategy recommendationStrategy = new RandomRecommendationStrategy();
// 	private final NewsletterRecommendationService newsletterRecommendationService = new NewsletterRecommendationService(
// 		recommendationStrategy);
//
// 	@Test
// 	public void newsletterList_has_low_number_of_elements_than_count_throw_exception() {
// 		// Given
// 		List<Newsletter> newsletterList = new ArrayList<>();
// 		List<Newsletter> advertiseNewsletterList = Collections.singletonList(
// 			createNewsletterWithName("advertise_newsletter",
// 				Category.create(UUID.randomUUID(), "category", "imageurl",
// 					"emoji")).toEntityDto()
// 				.toDomain());
//
// 		// When Then
// 		assertThrows(NotEnoughNewslettersException.class,
// 			() -> newsletterRecommendationService.recommendNewsletterList(newsletterList, advertiseNewsletterList));
//
// 	}
//
// 	@DisplayName("광고 뉴스레터 수가 1개일때(1번 카테고리의 다른 뉴스레터라 가정), 유저 카테고리에 해당되는 랜덤 뉴스레터 3개와 1개의 광고 뉴스레터가 전달되어야 한다.")
// 	@Test
// 	public void recommend_newsletter_list_expected_recommend_number() {
// 		// Given
// 		List<UUID> userCategoryIdList = Arrays.asList(UUID.randomUUID(), UUID.randomUUID(),
// 			UUID.randomUUID());
//
// 		List<Newsletter> userCategoryNewsletterList = new ArrayList<>();
// 		for (UUID id : userCategoryIdList) {
// 			createNewsletterByCategoryId(userCategoryNewsletterList, id);
// 		}
//
// 		Newsletter advertiseNewsletter = createNewsletterWithName("advertise_newsletter",
// 			userCategoryNewsletterList.get(0).getCategory());
// 		List<Newsletter> advertiseNewsletterList = Collections.singletonList(advertiseNewsletter);
//
// 		// When
// 		List<Newsletter> recommendNewsletterList = newsletterRecommendationService.recommendNewsletterList(
// 			advertiseNewsletterList, userCategoryNewsletterList);
//
// 		// Then
// 		assertThat(recommendNewsletterList).hasSize(4);
// 		List<String> newsletterName = recommendNewsletterList.stream()
// 			.map(Newsletter::getName)
// 			.toList();
// 		assertThat(newsletterName)
// 			.filteredOn(name -> name.equals("advertise_newsletter"))
// 			.hasSize(1);
// 		assertThat(newsletterName)
// 			.filteredOn(name -> name.equals("usercategory_newsletter"))
// 			.hasSize(3);
// 	}
//
// 	// 한 category 당 2개의 뉴스레터를 가진다.
// 	private void createNewsletterByCategoryId(List<Newsletter> newsletterList,
// 		UUID categoryId) {
// 		Category category = Category.create(categoryId, "testCategory", "test", "test");
// 		Newsletter newsletter = createNewsletterWithName("usercategory_newsletter", category);
// 		newsletterList.add(newsletter);
// 		Newsletter newsletter2 = createNewsletterWithName("usercategory_newsletter",
// 			category);
// 		newsletterList.add(newsletter2);
// 	}
//
// 	private Newsletter createNewsletterWithName(String newsletterName,
// 		Category category) {
// 		return new Newsletter(UUID.randomUUID(), newsletterName, UUID.,
// 			"domain not unique in mock",
// 			"malinglist", 1, "test", "test", "test", "test", "test", "test", Color.DEFAULT);
// 	}
// }
