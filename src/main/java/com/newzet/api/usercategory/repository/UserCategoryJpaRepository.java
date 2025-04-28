package com.newzet.api.usercategory.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCategoryJpaRepository extends JpaRepository<UserCategoryEntity, UUID> {
	List<UserCategoryEntity> findByUserId(UUID userId);

	void deleteByUserId(UUID userId);
}
