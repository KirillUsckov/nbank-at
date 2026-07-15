package ru.kduskov.common.confs;

import ru.kduskov.common.enums.ConfigParams;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class Config {
    private static final Config INSTANCE = new Config();

    private final Properties properties = new Properties();

    private Config() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (is == null) {
                throw new RuntimeException("config.properties wasn't found in resources");
            }
            properties.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Fail to load config.properties");
        }
    }


    public static String getProperty(ConfigParams configParam) {
        var property = System.getProperty(configParam.getValue());
        if (property != null) {
            return property;
        }
        property = System.getenv(configParam.name());
        if (property != null) {
            return property;
        }
        return INSTANCE.properties.getProperty(configParam.getValue());
    }
}
