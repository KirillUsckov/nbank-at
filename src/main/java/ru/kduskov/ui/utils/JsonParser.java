package ru.kduskov.ui.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

import java.io.IOException;

@UtilityClass
public final class JsonParser {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static <T> T parse(String resourcePath, Class<T> clazz) {
        var is = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(resourcePath);

        if (is == null) {
            throw new RuntimeException("Resource not found: " + resourcePath);
        }

        try {
            return MAPPER.readValue(is, clazz);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse JSON: " + resourcePath, e);
        }
    }
}
