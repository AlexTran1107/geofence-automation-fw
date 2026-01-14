package com.automation.utils;

import com.automation.core.DriverManager;
import io.qameta.allure.Allure;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for capturing and managing screenshots.
 */
public class ScreenshotUtil {
    private static final Logger logger = LoggerFactory.getLogger(ScreenshotUtil.class);
    private static final String SCREENSHOT_DIR = "screenshots";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    static {
        createScreenshotDirectory();
    }

    private static void createScreenshotDirectory() {
        try {
            Path path = Paths.get(SCREENSHOT_DIR);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            logger.error("Failed to create screenshot directory", e);
        }
    }

    public static String captureScreenshot(String testName) {
        try {
            io.appium.java_client.android.AndroidDriver driver = DriverManager.getDriver();
            if (driver == null) {
                logger.warn("Driver is null, cannot capture screenshot");
                return null;
            }

            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
            
            String timestamp = LocalDateTime.now().format(FORMATTER);
            String fileName = String.format("%s_%s.png", testName, timestamp);
            File destinationFile = new File(SCREENSHOT_DIR, fileName);
            
            FileUtils.copyFile(sourceFile, destinationFile);
            logger.info("Screenshot captured: {}", destinationFile.getAbsolutePath());
            
            // Attach to Allure
            attachScreenshotToAllure(destinationFile, testName);
            
            return destinationFile.getAbsolutePath();
        } catch (Exception e) {
            logger.error("Failed to capture screenshot", e);
            return null;
        }
    }

    private static void attachScreenshotToAllure(File screenshot, String testName) {
        try {
            Allure.addAttachment(testName + " Screenshot", 
                "image/png", 
                Files.newInputStream(screenshot.toPath()), 
                ".png");
        } catch (IOException e) {
            logger.error("Failed to attach screenshot to Allure", e);
        }
    }

    public static void attachScreenshotToAllure(byte[] screenshotBytes, String testName) {
        Allure.addAttachment(testName + " Screenshot", 
            "image/png", 
            new java.io.ByteArrayInputStream(screenshotBytes), 
            ".png");
    }
}

