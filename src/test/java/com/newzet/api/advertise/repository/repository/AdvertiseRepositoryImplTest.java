package com.newzet.api.advertise.repository.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.newzet.api.advertise.business.dto.AdvertiseEntityDto;
import com.newzet.api.advertise.repository.entity.AdvertiseEntity;
import com.newzet.api.config.PostgresTestContainerConfig;
import com.newzet.api.newsletter.repository.NewsletterEntity;
import com.newzet.api.newsletter.repository.NewsletterEntityStatus;
import com.newzet.api.newsletter.repository.NewsletterJpaRepository;

@DataJpaTest
@Import(AdvertiseRepositoryImpl.class)
@ExtendWith(PostgresTestContainerConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AdvertiseRepositoryImplTest {

	List<NewsletterEntity> newsletters;
	@Autowired
	private AdvertiseRepositoryImpl advertiseRepository;
	@Autowired
	private NewsletterJpaRepository newsletterRepository;
	@Autowired
	private AdvertiseJpaRepository advertiseJpaRepository;

	@BeforeEach
	void setUp() {
		newsletters = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			NewsletterEntity newsletter = newsletterRepository.save(NewsletterEntity.create("news" + i,
				"domain"+i, "mailinglist", NewsletterEntityStatus.REGISTERED.toString()));
			newsletters.add(newsletter);
		}
	}


	@Test
	void find_all_newsletter_ids_of_advertise() {
		// given
		newsletters.forEach(newsletter -> {
			advertiseJpaRepository.save(AdvertiseEntity.createForTest(newsletter.getId()));
		});

		// when
		List<AdvertiseEntityDto> advertiseNewsletters = advertiseRepository.getAllAdvertise();

		// then
		for (int i = 0; i < 5; i++) {
			assertThat(advertiseNewsletters.get(i).getNewsletterId()).isEqualTo(newsletters.get(i).getId());
		}
	}


}