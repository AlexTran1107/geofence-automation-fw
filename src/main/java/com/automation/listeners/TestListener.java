package com.automation.listeners;

import com.automation.services.AtlassianService;
import com.automation.utils.LoggerUtil;
import com.automation.utils.ScreenshotUtil;
import org.slf4j.Logger;
import org.testng.IConfigurationListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestNG listener for test execution events.
 * Handles test failures, success, and integrates with Jira for defect creation.
 *
 * - onTestFailure(): @Test method failures
 * - onConfigurationFailure(): setup/teardown failures (@Before/@After)
 */
public class TestListener implements ITestListener, IConfigurationListener {
    private static final Logger logger = LoggerUtil.getLogger(TestListener.class);
    private final AtlassianService atlassianService;

    public TestListener() {
        this.atlassianService = new AtlassianService();
    }

    @Override
    public void onTestStart(ITestResult result) {
        logger.info("Test started: {} - {}",
            result.getTestClass().getName(),
            result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info("Test passed: {} - {}",
            result.getTestClass().getName(),
            result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        logger.error("Test failed: {} - {}",
            result.getTestClass().getName(),
            result.getMethod().getMethodName());

        handleDefectCreation(result, "test-failure");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logger.warn("Test skipped: {} - {}",
            result.getTestClass().getName(),
            result.getMethod().getMethodName());
    }

    @Override
    public void onFinish(ITestContext context) {
        logger.info("Test suite finished. Total: {}, Passed: {}, Failed: {}, Skipped: {}",
            context.getAllTestMethods().length,
            context.getPassedTests().size(),
            context.getFailedTests().size(),
            context.getSkippedTests().size());
    }

    // =========================================================
    // ========== IConfigurationListener implementation =========
    // =========================================================

    @Override
    public void onConfigurationFailure(ITestResult result) {
        logger.error("Configuration failed: {} - {}",
            result.getTestClass() != null ? result.getTestClass().getName() : "UnknownClass",
            result.getMethod() != null ? result.getMethod().getMethodName() : "UnknownMethod");

        handleDefectCreation(result, "config-failure");
    }

    @Override
    public void onConfigurationSuccess(ITestResult itr) {
        // no-op
    }

    @Override
    public void onConfigurationSkip(ITestResult itr) {
        // no-op
    }

    // =========================================================
    // ================== Helper methods =======================
    // =========================================================

    private void handleDefectCreation(ITestResult result, String failureType) {
        String methodName = result.getMethod() != null ? result.getMethod().getMethodName() : "unknown";
        String screenshotPath = ScreenshotUtil.captureScreenshot(methodName);

        try {
            String jiraKey = atlassianService.createDefect(result, screenshotPath);
            if (jiraKey != null && !jiraKey.isEmpty()) {
                logger.info("✅ Jira defect created successfully for {}: {}", failureType, jiraKey);
            } else {
                logger.warn("⚠️ Jira defect creation failed or was skipped for {}. Check logs above for details.", failureType);
            }
        } catch (Exception e) {
            logger.error("❌ Failed to create Jira defect for " + failureType, e);
        }
    }
}