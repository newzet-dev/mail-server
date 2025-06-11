package com.newzet.api.userinfo.orchestrator;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.newzet.api.category.business.service.CategoryService;
import com.newzet.api.category.domain.Category;
import com.newzet.api.usercategory.business.service.UserCategoryService;
import com.newzet.api.usercategory.domain.UserCategory;
import com.newzet.api.userinfo.business.service.UserinfoService;
import com.newzet.api.userinfo.domain.Userinfo;
import com.newzet.api.userinfo.presentation.dto.UserinfoWithCategoryListResponse;
import com.newzet.api.userinfo.presentation.mapper.UserinfoResponseMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserinfoOrchestrator {
	private final UserinfoService userinfoService;
	private final UserCategoryService userCategoryService;
	private final CategoryService categoryService;

	public UserinfoWithCategoryListResponse getUserinfoWithCategoryList(UUID userId) {
		Userinfo userinfo = userinfoService.getUserinfoById(userId);
		List<UUID> categoryIdList = userCategoryService.findCategoryListByUserId(userId).stream()
			.map(UserCategory::categoryId)
			.toList();
		List<Category> categoryList = categoryService.getCategoryListByIdList(categoryIdList);
		return UserinfoResponseMapper.toWithCategoryListResponse(userinfo, categoryList);
	}

	public void updateUserinfo(UUID id, String email, String nickname, List<UUID> categories) {
		userinfoService.updateUserinfo(id, email, nickname);
	}
}
