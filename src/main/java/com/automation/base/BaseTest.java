package com.automation.base;

import com.automation.core.ConfigManager;
import com.automation.core.DriverManager;
import com.automation.listeners.AllureListener;
import com.automation.listeners.TestListener;
import com.automation.utils.LoggerUtil;
import com.automation.utils.ScreenshotUtil;
import io.qameta.allure.Allure;
import org.slf4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

/**
 * Base test class providing common setup and teardown functionality.
 * All test classes should extend this class.
 *
 * Listeners are attached here so they work both with TestNG suites (testng.xml)
 * and with Maven -Dtest runs.
 */
@Listeners({TestListener.class, AllureListener.class})
public abstract class BaseTest {
    protected static final Logger logger = LoggerUtil.getLogger(BaseTest.class);
    protected static final ConfigManager config = ConfigManager.getInstance();

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        logger.info("=== Starting Test Suite ===");
        Allure.addAttachment("Test Configuration", 
            String.format("Environment: %s%nPlatform: %s%nDevice: %s",
                config.getProperty("environment", "local"),
                config.getProperty("device.platform", "Android"),
                config.getProperty("device.name", "Unknown")));
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod(org.testng.ITestContext context) {
        logger.info("=== Starting Test Method ===");
        if (!DriverManager.isDriverInitialized()) {
            DriverManager.initializeDriver();
        }
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod(org.testng.ITestResult result) {
        try {
            if (result.getStatus() == org.testng.ITestResult.FAILURE) {
                String screenshotPath = ScreenshotUtil.captureScreenshot(
                    result.getMethod().getMethodName());
                if (screenshotPath != null) {
                    logger.info("Screenshot captured for failed test: {}", screenshotPath);
                }
            }
        } catch (Exception e) {
            logger.error("Error in afterMethod", e);
        } finally {
            DriverManager.quitDriver();
        }
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        logger.info("=== Test Suite Completed ===");
        DriverManager.stopAppiumServer();
    }
}




