package com.newzet.api.article.repository;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.newzet.api.article.business.dto.ArticleEntityDto;
import com.newzet.api.article.domain.Article;
import com.newzet.api.article.repository.entity.ArticleEntity;

import jakarta.persistence.EntityManager;

@ExtendWith(MockitoExtension.class)
class ArticleRepositoryTest {

	@Mock
	private ArticleJpaRepository articleJpaRepository;
	@Mock
	private EntityManager entityManager;
	@InjectMocks
	private ArticleRepositoryImpl articleRepository;

	@BeforeEach
	void setUp() {
		articleRepository = new ArticleRepositoryImpl(articleJpaRepository);
		ReflectionTestUtils.setField(articleRepository, "entityManager", entityManager);
	}

	@Test
	void existsByFromNameAndFromDomainAndTitleAndToUserIdAndDeletedAtIsNull_WhenArticleExists_ThenReturnTrue() {
		// Given
		UUID userId = UUID.randomUUID();
		String fromName = "Newsletter";
		String fromDomain = "example.com";
		String title = "Weekly News";

		when(
			articleJpaRepository.existsByFromNameAndFromDomainAndTitleAndToUserIdAndDeletedAtIsNull(
				fromName, fromDomain, title, userId)).thenReturn(true);

		// When
		boolean exists = articleRepository.existsByFromNameAndFromDomainAndTitleAndToUserIdAndDeletedAtIsNull(
			fromName, fromDomain, title, userId);

		// Then
		assertThat(exists).isTrue();
	}

	@Test
	void existsByFromNameAndFromDomainAndTitleAndToUserIdAndDeletedAtIsNull_WhenArticleDoesNotExist_ThenReturnFalse() {
		// Given
		UUID userId = UUID.randomUUID();
		String fromName = "Newsletter";
		String fromDomain = "example.com";
		String title = "Weekly News";

		when(
			articleJpaRepository.existsByFromNameAndFromDomainAndTitleAndToUserIdAndDeletedAtIsNull(
				fromName, fromDomain, title, userId)).thenReturn(false);

		// When
		boolean exists = articleRepository.existsByFromNameAndFromDomainAndTitleAndToUserIdAndDeletedAtIsNull(
			fromName, fromDomain, title, userId);

		// Then
		assertThat(exists).isFalse();
	}

	@Test
	void saveAll_WhenEntitiesLessThanBatchSize_ThenFlushOnce() {
		// Given
		int entityCount = 20;

		List<ArticleEntityDto> dtos = createMockArticleDtos(entityCount);

		// When
		articleRepository.saveAll(dtos);

		// Then
		verify(entityManager, times(entityCount)).persist(any(ArticleEntity.class));

		verify(entityManager, times(1)).flush();
		verify(entityManager, times(1)).clear();
	}

	@Test
	void saveAll_WhenEntitiesMoreThanBatchSize_ThenFlushMultipleTimes() {
		// Given
		int batchSize = 50;
		int entityCount = 120;
		int expectedFlushCount = (entityCount / batchSize) + (entityCount % batchSize > 0 ? 1 : 0);

		List<ArticleEntityDto> dtos = createMockArticleDtos(entityCount);

		// When
		articleRepository.saveAll(dtos);

		// Then
		verify(entityManager, times(entityCount)).persist(any(ArticleEntity.class));
		verify(entityManager, times(expectedFlushCount)).flush();
		verify(entityManager, times(expectedFlushCount)).clear();
	}

	@Test
	void saveAll_WhenBatchSizeExactlyMatches_ThenFlushOnce() {
		// Given
		int batchSize = 50;
		List<ArticleEntityDto> dtos = createMockArticleDtos(batchSize);

		// When
		articleRepository.saveAll(dtos);

		// Then
		verify(entityManager, times(batchSize)).persist(any(ArticleEntity.class));
		verify(entityManager, times(1)).flush();
		verify(entityManager, times(1)).clear();
	}

	@Test
	void saveAll_WhenMixedEntities_ThenCallMergeOrPersistAccordingly() {
		// Given
		List<ArticleEntityDto> dtos = new ArrayList<>();

		dtos.addAll(createMockArticleDtos(5));
		dtos.addAll(createMockArticleDtosWithIds(5));

		ArgumentCaptor<ArticleEntity> persistCaptor = ArgumentCaptor.forClass(ArticleEntity.class);
		ArgumentCaptor<ArticleEntity> mergeCaptor = ArgumentCaptor.forClass(ArticleEntity.class);

		// When
		articleRepository.saveAll(dtos);

		// Then
		verify(entityManager, times(5)).persist(persistCaptor.capture());
		verify(entityManager, times(5)).merge(mergeCaptor.capture());

		assertThat(persistCaptor.getAllValues())
			.allMatch(entity -> entity.getId() == null);

		assertThat(mergeCaptor.getAllValues())
			.allMatch(entity -> entity.getId() != null);
	}

	@Test
	void saveAll_ShouldReturnCorrectResults() {
		// Given
		int entityCount = 10;
		List<ArticleEntityDto> inputDtos = createMockArticleDtos(entityCount);

		// When
		doAnswer(invocation -> {
			ArticleEntity entity = invocation.getArgument(0);
			ReflectionTestUtils.setField(entity, "id", UUID.randomUUID());
			return null;
		}).when(entityManager).persist(any(ArticleEntity.class));

		List<ArticleEntityDto> result = articleRepository.saveAll(inputDtos);

		// Then
		assertThat(result).hasSize(entityCount);
		assertThat(result).allMatch(dto -> dto.getId() != null);
	}

	@Test
	void saveAll_WhenEmptyList_ThenReturnEmptyList() {
		// Given
		List<ArticleEntityDto> emptyList = List.of();

		// When
		List<ArticleEntityDto> result = articleRepository.saveAll(emptyList);

		// Then
		assertThat(result).isEmpty();
		verifyNoInteractions(entityManager);

		verify(articleJpaRepository, never()).saveAll(any());
		verify(entityManager, never()).persist(any());
		verify(entityManager, never()).merge(any());
		verify(entityManager, never()).flush();
		verify(entityManager, never()).clear();
	}

	@Test
	void saveAll_WhenExceptionOccurs_ThenPropagateException() {
		// Given
		List<ArticleEntityDto> dtos = createMockArticleDtos(5);

		doThrow(new RuntimeException("Test exception")).when(entityManager)
			.persist(any(ArticleEntity.class));

		// Then
		assertThatThrownBy(() -> articleRepository.saveAll(dtos))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("Test exception");
	}

	@Test
	void saveAll_WhenProcessingNullFields_ThenHandleGracefully() {
		// Given
		ArticleEntityDto dto = ArticleEntityDto.builder()
			.toUserId(UUID.randomUUID())
			.fromName(null)
			.fromDomain(null)
			.title("Test Title")
			.build();

		// When
		articleRepository.saveAll(List.of(dto));

		// Then
		ArgumentCaptor<ArticleEntity> entityCaptor = ArgumentCaptor.forClass(ArticleEntity.class);
		verify(entityManager).persist(entityCaptor.capture());

		ArticleEntity capturedEntity = entityCaptor.getValue();
		assertThat(capturedEntity.getFromName()).isNull();
		assertThat(capturedEntity.getFromDomain()).isNull();
	}

	private List<ArticleEntityDto> createMockArticleDtos(int count) {
		return IntStream.range(0, count)
			.mapToObj(i -> Article.createNewArticle(
				UUID.randomUUID(),
				"Newsletter" + i,
				"example.com",
				"weekly",
				"Weekly News " + i,
				"https://example.com/news/" + i
			))
			.map(ArticleEntityDto::fromDomain)
			.collect(Collectors.toList());
	}

	private List<ArticleEntityDto> createMockArticleDtosWithIds(int count) {
		return IntStream.range(0, count)
			.mapToObj(i -> {
				Article article = Article.createNewArticle(
					UUID.randomUUID(),
					"Newsletter" + i,
					"example.com",
					"weekly",
					"Weekly News " + i,
					"https://example.com/news/" + i
				);
				ArticleEntityDto dto = ArticleEntityDto.fromDomain(article);
				ReflectionTestUtils.setField(dto, "id", UUID.randomUUID());
				return dto;
			})
			.collect(Collectors.toList());
	}
}
