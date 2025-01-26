package com.newzet.api.user.repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import com.newzet.api.user.domain.UserStatus;

public class MockJpaRepository implements UserJpaRepository {
	private static final String EXIST_EMAIL = "exist@example.com";

	@Override
	public Optional<UserJpaEntity> findByEmailAndStatus(String email, UserStatus status) {
		if (email.equals(EXIST_EMAIL) && status == UserStatus.ACTIVE) {
			return Optional.of(new UserJpaEntity(EXIST_EMAIL, UserStatus.ACTIVE));
		}
		return Optional.empty();
	}

	@Override
	public void flush() {

	}

	@Override
	public <S extends UserJpaEntity> S saveAndFlush(S entity) {
		return null;
	}

	@Override
	public <S extends UserJpaEntity> List<S> saveAllAndFlush(Iterable<S> entities) {
		return List.of();
	}

	@Override
	public void deleteAllInBatch(Iterable<UserJpaEntity> entities) {

	}

	@Override
	public void deleteAllByIdInBatch(Iterable<Long> longs) {

	}

	@Override
	public void deleteAllInBatch() {

	}

	@Override
	public UserJpaEntity getOne(Long aLong) {
		return null;
	}

	@Override
	public UserJpaEntity getById(Long aLong) {
		return null;
	}

	@Override
	public UserJpaEntity getReferenceById(Long aLong) {
		return null;
	}

	@Override
	public <S extends UserJpaEntity> Optional<S> findOne(Example<S> example) {
		return Optional.empty();
	}

	@Override
	public <S extends UserJpaEntity> List<S> findAll(Example<S> example) {
		return List.of();
	}

	@Override
	public <S extends UserJpaEntity> List<S> findAll(Example<S> example, Sort sort) {
		return List.of();
	}

	@Override
	public <S extends UserJpaEntity> Page<S> findAll(Example<S> example, Pageable pageable) {
		return null;
	}

	@Override
	public <S extends UserJpaEntity> long count(Example<S> example) {
		return 0;
	}

	@Override
	public <S extends UserJpaEntity> boolean exists(Example<S> example) {
		return false;
	}

	@Override
	public <S extends UserJpaEntity, R> R findBy(Example<S> example,
		Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
		return null;
	}

	@Override
	public <S extends UserJpaEntity> S save(S entity) {
		return null;
	}

	@Override
	public <S extends UserJpaEntity> List<S> saveAll(Iterable<S> entities) {
		return List.of();
	}

	@Override
	public Optional<UserJpaEntity> findById(Long aLong) {
		return Optional.empty();
	}

	@Override
	public boolean existsById(Long aLong) {
		return false;
	}

	@Override
	public List<UserJpaEntity> findAll() {
		return List.of();
	}

	@Override
	public List<UserJpaEntity> findAllById(Iterable<Long> longs) {
		return List.of();
	}

	@Override
	public long count() {
		return 0;
	}

	@Override
	public void deleteById(Long aLong) {

	}

	@Override
	public void delete(UserJpaEntity entity) {

	}

	@Override
	public void deleteAllById(Iterable<? extends Long> longs) {

	}

	@Override
	public void deleteAll(Iterable<? extends UserJpaEntity> entities) {

	}

	@Override
	public void deleteAll() {

	}

	@Override
	public List<UserJpaEntity> findAll(Sort sort) {
		return List.of();
	}

	@Override
	public Page<UserJpaEntity> findAll(Pageable pageable) {
		return null;
	}
}
