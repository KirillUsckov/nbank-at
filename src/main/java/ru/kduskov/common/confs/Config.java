package ru.kduskov.common.confs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class Config {
    private final static Config INSTANCE = new Config();
    private final Properties properties = new Properties();

    private Config(){
        try(InputStream is = getClass().getClassLoader().getResourceAsStream("config.properties")){
            if(is == null)
                throw new RuntimeException("config.properties wasn't found in resources");
            properties.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Fail to load config.properties");
        }
    }

    public static String getProperty(String key) {
        return INSTANCE.properties.getProperty(key);
    }
}
