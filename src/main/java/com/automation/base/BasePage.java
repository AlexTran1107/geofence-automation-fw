package com.automation.base;

import com.automation.core.ConfigManager;
import com.automation.core.DriverManager;
import io.appium.java_client.android.AndroidDriver;
import io.netty.handler.timeout.TimeoutException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

/**
 * Base page object class providing common element interaction methods.
 * All page/screen classes should extend this class.
 * 
 * This class provides:
 * - Common WebDriver and WebDriverWait instances
 * - Pre-configured wait instances (shortWait: 2s, mediumWait: 5s, longWait: 10s)
 * - Reusable element interaction methods (click, sendKeys, findElement, etc.)
 * - Implicit wait configuration from application.properties
 * 
 * @author Automation Team
 * @version 1.0
 */
public abstract class BasePage {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected AndroidDriver driver;
    protected WebDriverWait wait;
    protected static final ConfigManager config = ConfigManager.getInstance();
    /** Short wait instance (2 seconds) - for quick element checks */
    protected WebDriverWait shortWait;
    /** Medium wait instance (5 seconds) - for standard element interactions */
    protected WebDriverWait mediumWait;
    /** Long wait instance (10 seconds) - for operations that may take longer */
    protected WebDriverWait longWait;

    public BasePage() {
        this.driver = DriverManager.getDriver();
        this.wait = DriverManager.getWait();
        setImplicitWait();
        // Initialize wait instances
        this.shortWait = new WebDriverWait(driver, Duration.ofSeconds(2));
        this.mediumWait = new WebDriverWait(driver, Duration.ofSeconds(5));
        this.longWait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /**
     * Sets implicit wait timeout for the driver.
     * Reads timeout value from configuration (test.timeout.implicit).
     * Default is 2 seconds if not configured.
     */
    protected void setImplicitWait() {
        int implicitTimeoutSeconds = config.getIntProperty("test.timeout.implicit", 2);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitTimeoutSeconds));
        logger.debug("Implicit wait set to {} seconds", implicitTimeoutSeconds);
    }

    protected WebElement findElement(By locator) {
        logger.debug("Finding element: {}", locator);
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /**
     * Finds multiple elements matching the locator.
     * Uses driver's findElements method which returns an empty list if no elements are found.
     * 
     * @param locator Element locator
     * @return List of WebElement instances matching the locator (empty if none found)
     */
    protected List<WebElement> findElements(By locator) {
        logger.debug("Finding elements: {}", locator);
        return driver.findElements(locator);
    }

    /**
     * Clicks an element after waiting for it to be clickable.
     * 
     * @param locator Element locator to click
     * @throws org.openqa.selenium.TimeoutException if element is not clickable within wait timeout
     */
    protected void click(By locator) {
        logger.info("Clicking element: {}", locator);
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        element.click();
    }

    /**
     * Sends text to an element after clearing its current value.
     * 
     * @param locator Element locator to send keys to
     * @param text Text to send to the element
     * @throws org.openqa.selenium.TimeoutException if element is not found within wait timeout
     */
    protected void sendKeys(By locator, String text) {
        logger.info("Sending keys to element {}: {}", locator, text);
        WebElement element = findElement(locator);
        element.clear();
        element.sendKeys(text);
    }

    /**
     * Gets the text content of an element.
     * 
     * @param locator Element locator
     * @return Text content of the element
     * @throws org.openqa.selenium.TimeoutException if element is not found within wait timeout
     */
    protected String getText(By locator) {
        logger.debug("Getting text from element: {}", locator);
        WebElement element = findElement(locator);
        return element.getText();
    }

    /**
     * Checks if an element is displayed on the screen.
     * Returns false if element is not found or not visible.
     * 
     * @param locator Element locator
     * @return true if element is displayed, false otherwise
     */
    protected boolean isDisplayed(By locator) {
        try {
            WebElement element = findElement(locator);
            return element.isDisplayed();
        } catch (Exception e) {
            logger.debug("Element not displayed: {}", locator);
            return false;
        }
    }

    /**
     * Checks if an element is enabled (can be interacted with).
     * Returns false if element is not found or not enabled.
     * 
     * @param locator Element locator
     * @return true if element is enabled, false otherwise
     */
    protected boolean isEnabled(By locator) {
        try {
            WebElement element = findElement(locator);
            return element.isEnabled();
        } catch (Exception e) {
            logger.debug("Element not enabled: {}", locator);
            return false;
        }
    }

    /**
     * Waits for an element to be present in the DOM.
     * Uses the default wait timeout from DriverManager.
     * 
     * @param locator Element locator to wait for
     * @throws org.openqa.selenium.TimeoutException if element is not found within wait timeout
     */
    protected void waitForElement(By locator) {
        logger.debug("Waiting for element: {}", locator);
        wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /**
     * Waits for an element to be clickable (present and enabled).
     * 
     * @param locator Element locator to wait for
     * @throws org.openqa.selenium.TimeoutException if element is not clickable within wait timeout
     */
    protected void waitForElementToBeClickable(By locator) {
        logger.debug("Waiting for element to be clickable: {}", locator);
        wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * Waits for an element to disappear from the DOM or become invisible.
     * 
     * @param locator Element locator to wait for disappearance
     * @throws org.openqa.selenium.TimeoutException if element is still visible after wait timeout
     */
    protected void waitForElementToDisappear(By locator) {
        logger.debug("Waiting for element to disappear: {}", locator);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    /**
     * Waits for specific text to appear in an element.
     * 
     * @param locator Element locator
     * @param text Text to wait for
     * @throws org.openqa.selenium.TimeoutException if text is not found within wait timeout
     */
    protected void waitForTextToBePresent(By locator, String text) {
        logger.debug("Waiting for text '{}' in element: {}", text, locator);
        wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    /**
     * Waits for an element to be ready (clickable) using primary locator,
     * with fallback to alternative locator if primary fails.
     * 
     * @param primaryLocator Primary locator to try first
     * @param alternativeLocator Alternative locator to try if primary fails
     * @return WebElement that is ready and clickable
     * @throws RuntimeException if both locators fail
     */
    protected WebElement waitForElementToReady(By primaryLocator, By alternativeLocator) {
        logger.debug("Waiting for element to be ready - primary: {}, alternative: {}", primaryLocator, alternativeLocator);
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(primaryLocator));
            logger.debug("Element found using primary locator: {}", primaryLocator);
            return element;
        } catch (Exception e) {
            logger.debug("Primary locator failed, trying alternative: {}", e.getMessage());
            try {
                WebElement element = wait.until(ExpectedConditions.elementToBeClickable(alternativeLocator));
                logger.debug("Element found using alternative locator: {}", alternativeLocator);
                return element;
            } catch (Exception altEx) {
                logger.error("Both primary and alternative locators failed for element");
                throw new RuntimeException("Element not found with primary locator (" + primaryLocator + 
                    ") or alternative locator (" + alternativeLocator + ")", altEx);
            }
        }
    }

    /**
     * Handles a notification popup if it is present on the screen.
     * Waits for the popup to appear, then waits for it to disappear (auto-closes).
     * If no popup appears within the timeout, continues execution.
     * 
     * @param popupLocator Locator for the popup element
     * @param timeoutSec Timeout in seconds to wait for popup to appear/disappear
     */
    protected void handleNotificationPopupIfPresent(By popupLocator, int timeoutSec) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSec));
    
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(popupLocator));
            logger.info("Popup detected, waiting for it to close...");
    
            wait.until(ExpectedConditions.invisibilityOfElementLocated(popupLocator));
            logger.info("Popup closed successfully");
    
        } catch (TimeoutException e) {
            logger.debug("No popup detected within {} seconds, continuing flow", timeoutSec);
        } catch (Exception e) {
            logger.debug("Error handling popup, continuing flow: {}", e.getMessage());
        }
    }
}

