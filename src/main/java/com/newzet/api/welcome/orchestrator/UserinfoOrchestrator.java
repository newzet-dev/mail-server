package com.newzet.api.welcome.orchestrator;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newzet.api.category.business.service.CategoryService;
import com.newzet.api.category.domain.Category;
import com.newzet.api.usercategory.business.service.UserCategoryService;
import com.newzet.api.usercategory.domain.UserCategory;
import com.newzet.api.welcome.business.service.UserinfoService;
import com.newzet.api.welcome.domain.Userinfo;
import com.newzet.api.welcome.presentation.dto.UniqueMailResponse;
import com.newzet.api.welcome.presentation.dto.UserinfoInitResponse;
import com.newzet.api.welcome.presentation.dto.UserinfoWithCategoryListResponse;
import com.newzet.api.welcome.presentation.mapper.UserinfoResponseMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserinfoOrchestrator {
	private final UserinfoService userinfoService;
	private final UserCategoryService userCategoryService;
	private final CategoryService categoryService;

	@Transactional(readOnly = true)
	public UserinfoWithCategoryListResponse getUserinfoWithCategoryList(UUID userId) {
		Userinfo userinfo = userinfoService.findUserinfoById(userId);
		List<UUID> categoryIdList = userCategoryService.findCategoryListByUserId(userId).stream()
			.map(UserCategory::categoryId)
			.toList();
		List<Category> categoryList = categoryService.getCategoryListByIdList(categoryIdList);
		return UserinfoResponseMapper.toWithCategoryListResponse(userinfo, categoryList);
	}

	@Transactional
	public void updateUserinfo(UUID userId, String email, String nickname, List<UUID> categoryIdList) {
		userinfoService.updateUserEmailAndNickname(userId, email, nickname);
		userCategoryService.deleteUserCategoriesByUserId(userId);
		userCategoryService.addUserCategories(userId, categoryIdList);
	}

	@Transactional(readOnly = true)
	public UniqueMailResponse checkEmailUniqueness(String email) {
		boolean uniqueness = userinfoService.isUniqueEmailInUserinfo(email);
		if (uniqueness) {
			return UniqueMailResponse.ofUnique();
		}
		return UniqueMailResponse.ofDuplicate();
	}

	@Transactional(readOnly = true)
	public UserinfoInitResponse checkUserInitializeCompleted(UUID userId) {
		boolean isInitialized = userinfoService.isInitialized(userId);
		return new UserinfoInitResponse(isInitialized);
	}
}
