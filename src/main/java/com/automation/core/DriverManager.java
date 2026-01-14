package com.automation.core;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Driver Manager for Appium AndroidDriver lifecycle management.
 * Implements Singleton pattern with ThreadLocal for parallel execution support.
 * 
 * Responsibilities:
 * - Driver initialization and configuration
 * - App installation and management
 * - Driver lifecycle management (start, quit, cleanup)
 * - Wait object management
 * 
 * @author Ngan Tran
 * @version 1.0
 */
public class DriverManager {
    private static final Logger logger = LoggerFactory.getLogger(DriverManager.class);
    private static final AtomicReference<AndroidDriver> driver = new AtomicReference<>();
    private static final AtomicReference<WebDriverWait> wait = new AtomicReference<>();
    private static final ConfigManager config = ConfigManager.getInstance();

    /**
     * Initializes the Android driver with configuration from application.properties.
     */
    public static void initializeDriver() {
        try {
            logger.info("Initializing Android driver...");

            UiAutomator2Options options = new UiAutomator2Options();

            // Device configuration
            options.setPlatformName(config.getProperty("device.platform", "Android"));
            options.setDeviceName(config.getProperty("device.name", "Android Emulator"));
            String udid = config.getProperty("device.udid", "emulator-5554");
            options.setUdid(udid);

            // App configuration
            String appPath = config.getProperty("device.app.path");
            String appPackage = config.getProperty("device.app.package");
            String appActivity = config.getProperty("device.app.activity");
            boolean appProvided = false;

            if (appPath != null) {
                File appFile = new File(appPath);
                if (!appFile.isAbsolute()) {
                    Path projectRoot = Paths.get(System.getProperty("user.dir"));
                    appFile = projectRoot.resolve(appPath).toFile();
                }

                if (appFile.exists()) {
                    String absoluteAppPath = appFile.getAbsolutePath();
                    logger.info("APK file found: {}", absoluteAppPath);

                    if (!isAppInstalled(udid, appPackage)) {
                        logger.info("App not installed on device. Installing APK...");
                        try {
                            installApp(udid, absoluteAppPath);
                            logger.info("APK installed successfully");
                        } catch (Exception e) {
                            logger.warn("Failed to install APK (may be due to SDK version mismatch): {}. Using package/activity mode instead", e.getMessage());
                        }
                    }
                    
                    // Always use APK path if available - more reliable than package/activity mode
                    options.setApp(absoluteAppPath);
                    appProvided = true;
                    logger.info("Using APK path for app launch: {}", absoluteAppPath);
                } else {
                    logger.warn("APK file not found at path: {}. Falling back to package/activity", appFile.getAbsolutePath());
                }
            }

            if (!appProvided && appPath != null) {
                // If APK exists but wasn't installed/used, try to install it now
                File appFile = new File(appPath);
                if (!appFile.isAbsolute()) {
                    Path projectRoot = Paths.get(System.getProperty("user.dir"));
                    appFile = projectRoot.resolve(appPath).toFile();
                }
                
                if (appFile.exists() && !isAppInstalled(udid, appPackage)) {
                    logger.info("Attempting to install APK before using package/activity mode");
                    try {
                        installApp(udid, appFile.getAbsolutePath());
                        logger.info("APK installed successfully");
                    } catch (Exception e) {
                        logger.warn("Failed to install APK: {}", e.getMessage());
                    }
                }
                
                // Use package/activity mode
                options.setAppPackage(appPackage);
                options.setAppActivity(appActivity);
                options.setNoReset(true);
                logger.info("Using app package: {} (activity: {})", appPackage, appActivity);
            } else {
                options.setNoReset(false);
            }

            // Additional options
            options.setAutomationName(config.getProperty("device.automation.name", "UiAutomator2"));
            options.setFullReset(false);
            options.setAutoGrantPermissions(true);
            options.setNewCommandTimeout(Duration.ofSeconds(
                config.getIntProperty("appium.server.timeout", 30000) / 1000));

            String serverUrl = config.getProperty("appium.server.url", "http://localhost:4723");
            AndroidDriver androidDriver = new AndroidDriver(new URL(serverUrl), options);

            // Set implicit wait
            int implicitTimeout = config.getIntProperty("test.timeout.implicit", 10);
            androidDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitTimeout));

            // Initialize WebDriverWait
            int explicitTimeout = config.getIntProperty("test.timeout.explicit", 30);
            WebDriverWait webDriverWait = new WebDriverWait(androidDriver, Duration.ofSeconds(explicitTimeout));

            driver.set(androidDriver);
            wait.set(webDriverWait);

            logger.info("Android driver initialized successfully");

        } catch (Exception e) {
            logger.error("Failed to initialize driver", e);
            throw new RuntimeException("Driver initialization failed", e);
        }
    }

    /**
     * Gets the current AndroidDriver instance.
     * 
     * @return AndroidDriver instance
     */
    public static AndroidDriver getDriver() {
        AndroidDriver currentDriver = driver.get();
        if (currentDriver == null) {
            throw new IllegalStateException("Driver is not initialized. Call initializeDriver() first.");
        }
        return currentDriver;
    }

    /**
     * Gets the WebDriverWait instance.
     * 
     * @return WebDriverWait instance
     */
    public static WebDriverWait getWait() {
        WebDriverWait currentWait = wait.get();
        if (currentWait == null) {
            throw new IllegalStateException("Wait is not initialized. Call initializeDriver() first.");
        }
        return currentWait;
    }

    /**
     * Checks if driver is initialized.
     * 
     * @return true if driver is initialized
     */
    public static boolean isDriverInitialized() {
        return driver.get() != null;
    }

    /**
     * Quits the driver and cleans up resources.
     */
    public static void quitDriver() {
        try {
            AndroidDriver currentDriver = driver.get();
            if (currentDriver != null) {
                logger.info("Quitting driver...");
                currentDriver.quit();
                driver.set(null);
                wait.set(null);
                logger.info("Driver quit successfully");
            }
        } catch (Exception e) {
            logger.error("Error quitting driver", e);
        }
    }

    /**
     * Stops the Appium server (if started by framework).
     * Note: In most cases, Appium is started externally.
     */
    public static void stopAppiumServer() {
        logger.info("Appium server stop requested (typically managed externally)");
        // Appium server is usually started externally, so this is a placeholder
    }

    /**
     * Checks if app is installed on device.
     * 
     * @param udid Device UDID
     * @param packageName App package name
     * @return true if app is installed
     */
    private static boolean isAppInstalled(String udid, String packageName) {
        try {
            ProcessBuilder pb = new ProcessBuilder("adb", "-s", udid, "shell", "pm", "list", "packages", packageName);
            Process process = pb.start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            logger.warn("Error checking if app is installed", e);
            return false;
        }
    }

    /**
     * Installs APK on device using ADB.
     * 
     * @param udid Device UDID
     * @param apkPath Path to APK file
     */
    private static void installApp(String udid, String apkPath) {
        try {
            ProcessBuilder pb = new ProcessBuilder("adb", "-s", udid, "install", "-g", apkPath);
            Process process = pb.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("APK installation failed with exit code: " + exitCode);
            }
        } catch (Exception e) {
            logger.error("Failed to install APK", e);
            throw new RuntimeException("APK installation failed", e);
        }
    }
}
