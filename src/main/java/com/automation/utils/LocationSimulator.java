package com.automation.utils;

import com.automation.core.DriverManager;
import io.appium.java_client.android.AndroidDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openqa.selenium.html5.Location;

/**
 * Utility class for simulating device location changes.
 * Used for testing geofence exit scenarios.
 */
public class LocationSimulator {
    private static final Logger logger = LoggerFactory.getLogger(LocationSimulator.class);

    /**
     * Sets the device location to specified coordinates.
     * 
     * @param latitude Latitude coordinate
     * @param longitude Longitude coordinate
     */
    public static void setLocation(double latitude, double longitude) {
        AndroidDriver driver = DriverManager.getDriver();
        if (driver == null) {
            throw new IllegalStateException("Driver is not initialized");
        }

        logger.info("Setting mock location via Appium: lat={}, lon={}", latitude, longitude);
        try {
            Location location = new Location(latitude, longitude, 0);
            driver.setLocation(location);

            // Wait for GPS + geofence processing
            Thread.sleep(5000);

            logger.info("Mock location applied successfully via Appium");

        } catch (Exception e) {
            logger.error("Failed to set mock location via Appium", e);
            throw new RuntimeException("Location simulation failed", e);
        }
    }

    /**
     * Simulates device movement outside geofence by setting location
     * at a specified distance from the geofence center.
     * 
     * @param geofenceLat Geofence center latitude
     * @param geofenceLon Geofence center longitude
     * @param distanceMeters Distance in meters to move outside geofence
     */
    public static void simulateExitGeofence(double geofenceLat, double geofenceLon, double distanceMeters) {
        // Calculate new coordinates at specified distance
        // Using simple approximation: 1 degree latitude ≈ 111 km
        // For longitude, adjust by cos(latitude)
        double latOffset = distanceMeters / 111000.0;
        double lonOffset = distanceMeters / (111000.0 * Math.cos(Math.toRadians(geofenceLat)));
        
        double newLat = geofenceLat + latOffset;
        double newLon = geofenceLon + lonOffset;
        
        logger.info("Simulating exit from geofence. Moving {} meters away", distanceMeters);
        setLocation(newLat, newLon);
    }

    /**
     * Resets device location to geofence center.
     * 
     * @param geofenceLat Geofence center latitude
     * @param geofenceLon Geofence center longitude
     */
    public static void resetToGeofenceCenter(double geofenceLat, double geofenceLon) {
        logger.info("Resetting location to geofence center");
        setLocation(geofenceLat, geofenceLon);
    }
}

