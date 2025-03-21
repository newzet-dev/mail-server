package com.newzet.api.common.objectMapper;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OptionalObjectMapper {

	private final ObjectMapper objectMapper;

	public <T> Optional<T> deserialize(String value, Class<T> classType) {
		if (!StringUtils.hasText(value)) {
			return Optional.empty();
		}
		try {
			return Optional.of(objectMapper.readValue(value, classType));
		} catch (JsonProcessingException e) {
			throw new DeserializationException(classType.getSimpleName(), e);
		}
	}

	public <T> String serialize(T object) {
		try {
			return objectMapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new SerializationException(object.getClass().getSimpleName(), e);
		}
	}
}
