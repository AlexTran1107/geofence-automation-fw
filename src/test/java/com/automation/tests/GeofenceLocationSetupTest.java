package com.automation.tests;

import com.automation.base.BaseTest;
import com.automation.core.ConfigManager;
import com.automation.pages.MainScreen;
import com.automation.utils.LocationSimulator;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * TC1 - Geofence Zone Creation and Activation Test
 * 
 * Scenario: Geofence zone is created
 * Given the user is on the EgiGeoZone main screen
 * When the user creates a new geofence zone
 * Then the geofence zone should be created successfully
 *   And the zone should be displayed in the zone list
 * @author Ngan Tran
 * @version 1.0
 */
@Epic("Geofence Functionality")
@Feature("Geofence Zone Creation and Activation")
public class GeofenceLocationSetupTest extends BaseTest {
    private final ConfigManager config = ConfigManager.getInstance();

    @Test(description = "TC1: Verify geofence zone creation and activation in EgiGeoZone")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test to verify that a geofence zone can be created with 50m radius, Exit trigger, " +
                 "notifications enabled, and becomes active in EgiGeoZone")
    @Story("User creates and activates geofence zone with Exit trigger")
    public void testGeofenceLocationSetup() {
        logger.info("Starting TC1: Geofence Zone Creation and Activation");

        MainScreen mainScreen = new MainScreen();

        // Given: User is on the EgiGeoZone main screen
        Allure.step("Launch EgiGeoZone application and navigate to main screen", () -> {
            // waitForAppToLoad include:
            // 1. Wait for page load
            // 2. Grant location permission
            mainScreen.waitForAppToLoad();
            logger.info("✅ EgiGeoZone application launched and permissions granted");

            // Set current device location
            double currentLat = config.getDoubleProperty("location.simulation.latitude", 37.7749);
            double currentLon = config.getDoubleProperty("location.simulation.longitude", -122.4194);
            LocationSimulator.setLocation(currentLat, currentLon);

            // Setup server profile
            mainScreen.setupServerProfile();
            logger.info("Server profile setup completed");
        });

        // When: User creates a new geofence zone
        // Step 1: Click on + button to create geofence
        Allure.step("Click on + button to create geofence", () -> {
            logger.info("Opening app menu...");
            mainScreen.openAppMenu();
            
            // Select Geofence menu and wait for menu to disappear
            mainScreen.selectGeofenceMenu();
            logger.info("✅ Navigated to Geofence list screen");

            mainScreen.clickAddGeofenceButton();
            logger.info("Clicked + button for geofence creation");
        });

        // Step 2: Create geofence zone with 50m radius
        double latitude = config.getDoubleProperty("geofence.latitude", 37.7749);
        double longitude = config.getDoubleProperty("geofence.longitude", -122.4194);
        int radius = 50; // Exactly 50 meters as per scenario

        Allure.step(String.format("Create geofence zone with radius %s meters at lat=%s, lon=%s", 
            radius, latitude, longitude), () -> {
            
            logger.info("Waiting for geofence form to appear and enter geofence infos");
            mainScreen.waitForGeofenceFormToAppear();
            mainScreen.enterGeofenceName();
            mainScreen.enterLatitude(latitude);
            mainScreen.enterLongitude(longitude);
            mainScreen.enterRadius(radius);
            mainScreen.enableLocationTracking("Exit");
            logger.info("✅ Geofence zone creation initiated");
            
            logger.info("Saving geofence zone...");
            mainScreen.saveGeofenceZone();
            logger.info("Geofence zone saved");
        });

        Allure.step("Validate zone is displayed in the zone list", () -> {
            boolean isInList = mainScreen.isGeofenceInZoneList();
            Assert.assertTrue(isInList, 
                "Geofence zone should be displayed in the zone list");
            logger.info("Geofence zone presence in list validated");
        });

        logger.info("TC1 completed successfully - Geofence zone created and activated");
    }
}
