package com.newzet.api.advertise.repository.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.newzet.api.advertise.repository.entity.AdvertiseEntity;

@Repository
public interface AdvertiseJpaRepository extends JpaRepository<AdvertiseEntity, UUID> {

}
