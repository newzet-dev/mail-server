package com.newzet.api.common.objectMapper;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class OptionalObjectMapperTest {

	private final OptionalObjectMapper objectMapper = new OptionalObjectMapper(new ObjectMapper());

	@Test
	public void deserialize_whenValueHasNoText_returnOptionalEmpty() {
		//When, then
		assertEquals(Optional.empty(), objectMapper.deserialize("", Object.class));
	}

	@Test
	public void deserialize_whenJsonProcessingException_throwDeserializationException() {
		//Given
		String invalidJson = "{key:value}";

		//When, Then
		DeserializationException exception = assertThrows(DeserializationException.class,
			() -> objectMapper.deserialize(invalidJson, Object.class));
		assertEquals("Object 역직렬화 오류", exception.getMessage());
	}

	@Test
	public void deserialize_whenSuccessToDeserialize_returnClassTypeObject() {
		// Given
		String validJson = "{\"value\":\"test\"}";

		// When
		Optional<TestObject> obj = objectMapper.deserialize(validJson, TestObject.class);

		// Then
		assertTrue(obj.isPresent());
		assertEquals("test", obj.get().value);
	}

	@Test
	public void serialize_whenJsonProcessingException_throwSerializationException() {
		//Given
		Object testObject = new Object();

		//When, Then
		SerializationException exception = assertThrows(SerializationException.class,
			() -> objectMapper.serialize(testObject));
		assertEquals("Object 직렬화 오류", exception.getMessage());
	}

	@Test
	public void serialize_whenSuccessToSerialize_returnStringOfObject() {
		// Given
		TestObject testObject = new TestObject("test");

		// When
		String parsedObject = objectMapper.serialize(testObject);

		// Then
		assertNotNull(parsedObject);
		assertEquals("{\"value\":\"test\"}", parsedObject);
	}

	static class TestObject {
		private String value;

		public TestObject() {
		}

		public TestObject(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}
}
