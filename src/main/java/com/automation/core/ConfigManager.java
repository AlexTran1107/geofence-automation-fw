package com.automation.core;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Optional;

/**
 * Centralized configuration management for the automation framework.
 * Supports properties files and environment variable overrides.
 */
public class ConfigManager {
    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    private static ConfigManager instance;
    private Configuration config;

    private ConfigManager() {
        loadConfiguration();
    }

    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    private void loadConfiguration() {
        try {
            Configurations configs = new Configurations();
            String configFile = System.getProperty("config.file", "src/main/resources/application.properties");
            config = configs.properties(new File(configFile));
            logger.info("Configuration loaded from: {}", configFile);
        } catch (ConfigurationException e) {
            logger.error("Failed to load configuration", e);
            throw new RuntimeException("Configuration initialization failed", e);
        }
    }

    public String getProperty(String key) {
        String envValue = System.getenv(key.replace(".", "_").toUpperCase());
        if (envValue != null) {
            return envValue;
        }
        return config.getString(key);
    }

    public String getProperty(String key, String defaultValue) {
        return Optional.ofNullable(getProperty(key)).orElse(defaultValue);
    }

    public int getIntProperty(String key, int defaultValue) {
        String value = getProperty(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                logger.warn("Invalid integer value for key: {}, using default: {}", key, defaultValue);
            }
        }
        return defaultValue;
    }

    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        if (value != null) {
            return Boolean.parseBoolean(value);
        }
        return defaultValue;
    }

    public double getDoubleProperty(String key, double defaultValue) {
        String value = getProperty(key);
        if (value != null) {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                logger.warn("Invalid double value for key: {}, using default: {}", key, defaultValue);
            }
        }
        return defaultValue;
    }
}



