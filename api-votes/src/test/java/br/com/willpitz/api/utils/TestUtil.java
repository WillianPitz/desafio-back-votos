package br.com.willpitz.api.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.test.json.BasicJsonTester;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

public class TestUtil {

    public static final String DESERIALIZATION_ERROR_MSG = "Falha ao tentar deserializar o JSON de teste.";

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule());
    private static final BasicJsonTester JSON_TESTER = new BasicJsonTester(TestUtil.class);

    public static Object copyObject(Object homeResponse, Class<?> clazz) {
        try {
            return MAPPER.readValue(MAPPER.writeValueAsString(homeResponse), clazz);
        } catch (JsonProcessingException e) {
            return new RuntimeException("Falha ao copiar objeto.", e);
        }
    }

    public static <T> T jsonToObject(String path, Class<T> clazz) {
        try {
            return MAPPER.readValue(JSON_TESTER.from(path).getJson(), clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(DESERIALIZATION_ERROR_MSG, e);
        }
    }

    public static <T> T jsonStrToObject(String json, Class<T> clazz) {
        try {
            return MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(DESERIALIZATION_ERROR_MSG, e);
        }
    }

    public static String jsonFromFile(String path) {
        return JSON_TESTER.from(path).getJson();
    }

    public static ResultMatcher assertJsonFromPath(String path) {
        return content().json(jsonFromFile(path), true);
    }

    public static ResultMatcher assertJson(String json) {
        return content().json(json, true);
    }
}
