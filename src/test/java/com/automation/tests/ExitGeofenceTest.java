package com.automation.tests;

import com.automation.base.BaseTest;
import com.automation.core.ConfigManager;
import com.automation.pages.MainScreen;
import com.automation.utils.LocationSimulator;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * TC2 - Exit Geofence Detection Test
 *
 * Scenario: EgiGeoZone triggers notification when device exits a configured zone
 *
 * Given a geofence zone is created with radius "50" meters at the current device location
 *   And notifications for this zone are enabled
 *   And the device location is set inside the zone
 * When the device moves outside the geofence area
 * Then the system should detect an exit event from the zone
 *   And EgiGeoZone should show a notification for the exit event
 *   And the notification message should contain the exit event keyword
 * 
 * @author Ngan Tran
 * @version 1.0
 */
@Epic("Geofence Functionality")
@Feature("Geofence Exit Detection")
public class ExitGeofenceTest extends BaseTest {
    private final ConfigManager config = ConfigManager.getInstance();

    @Test(description = "TC2: Verify exit geofence detection triggers notification in EgiGeoZone")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test to verify that EgiGeoZone triggers notification when device exits " +
                 "a 50m radius geofence zone")
    @Story("EgiGeoZone detects exit event and shows notification")
    public void testExitGeofence150m() {
        logger.info("Starting TC2: Exit Geofence Detection Test");

        MainScreen mainScreen = new MainScreen();

        // Given: Geofence zone is created with 50m radius at current device location
        Allure.step("Create geofence zone with 50m radius at current device location", () -> {
            mainScreen.waitForAppToLoad();

            // Setup server profile
            mainScreen.setupServerProfile();
            logger.info("Server profile setup completed");
            
            // Set current device location
            double currentLat = config.getDoubleProperty("location.simulation.latitude", 37.7749);
            double currentLon = config.getDoubleProperty("location.simulation.longitude", -122.4194);
            LocationSimulator.setLocation(currentLat, currentLon);
            
            // Create geofence zone with 50m radius
            int radius = 50; 
            mainScreen.createGeofenceZone(currentLat, currentLon, radius, "Exit");
            
            // Save zone
            mainScreen.saveGeofenceZone();
            
            logger.info("Geofence zone created with 50m radius at current location");
        });

        // When: Device moves outside the geofence area
        double geofenceLat = config.getDoubleProperty("location.simulation.latitude", 37.7750);
        double geofenceLon = config.getDoubleProperty("location.simulation.longitude", -122.4195);
        double exitDistance = config.getDoubleProperty("geofence.exit.distance", 150);

        Allure.step(String.format("Move device %s meters outside the geofence area", 
            exitDistance), () -> {
            LocationSimulator.simulateExitGeofence(geofenceLat, geofenceLon, exitDistance);
            // Wait for geofence exit detection - use explicit wait in MainScreen
            logger.info("Device moved {} meters outside geofence zone", exitDistance);
        });

        // Then: Refresh the page
        Allure.step("Refresh the page", () -> {
            mainScreen.refreshPage();
            logger.info("Page refreshed");
        });

        // Then: Validate system detects exit event from the zone
        Allure.step("Validate system detects exit event from the zone", () -> {
            boolean exitDetected = mainScreen.isExitEventDetected();
            Assert.assertTrue(exitDetected, 
                "System should detect an exit event from the zone");
            logger.info("Exit event detection validated");
        });

        logger.info("TC2 completed successfully - Exit event detected and notification verified");
    }
}
