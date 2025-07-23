package com.newzet.api.event.jpa.entity;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "event_banner")
public class EventEntity {

	@Id
	@UuidGenerator
	@Column(columnDefinition = "uuid", updatable = false, nullable = false)
	private UUID id;

	@Column(name = "event_url", nullable = false)
	private String eventUrl;

	@Column(name = "image_url", nullable = false)
	private String imageUrl;

	@Column(name = "post_start")
	private String postStart;

	@Column(name = "post_end")
	private String postEnd;

	@Column(name = "priority")
	private Integer priority;

}
