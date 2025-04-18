package com.newzet.api.category.business;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.newzet.api.category.business.dto.CategoryEntityDto;

public interface CategoryRepository {

	List<CategoryEntityDto> findAll();
}
