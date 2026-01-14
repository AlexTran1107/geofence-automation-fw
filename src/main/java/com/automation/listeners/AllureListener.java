package com.automation.listeners;
import io.qameta.allure.testng.AllureTestNg;
import org.testng.ITestResult;

/**
 * Custom Allure listener extending AllureTestNg for enhanced reporting.
 */
public class AllureListener extends AllureTestNg {
    
    @Override
    public void onTestFailure(ITestResult result) {
        super.onTestFailure(result);
        
        // Enhanced failure reporting is handled by base class
        // Additional customizations can be added here if needed
    }
}

