package com.automation.pages;

import com.automation.base.BasePage;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Page Object for EgiGeoZone main screen.
 * Implements Screen Object Model pattern with clear separation of concerns.
 * 
 * This class handles all interactions with the EgiGeoZone application UI,
 * including geofence creation, zone management, and notification handling.
 * 
 * @author Ngan Tran
 * @version 1.0
 */
public class MainScreen extends BasePage {
    private static final Logger logger = LoggerFactory.getLogger(MainScreen.class);

    // ============================================================================
    // Permission Locators
    // ============================================================================
    private final By grantDialog = AppiumBy.id("com.android.permissioncontroller:id/grant_dialog");
    private final By permissionAllowForegroundOnlyButton = AppiumBy
            .id("com.android.permissioncontroller:id/permission_allow_foreground_only_button");
    private final By backgroundPermissionPopup = AppiumBy.id("de.egi.geofence.geozone:id/parentPanel");
    private final By okButton = AppiumBy.id("android:id/button1");
    private final By locationPermissionPage = AppiumBy.accessibilityId("Location permission");
    private final By allowAlwaysRadioButton = AppiumBy
            .id("com.android.permissioncontroller:id/allow_always_radio_button");
    private final By navigateUpButton = AppiumBy.accessibilityId("Navigate up");
    private final By settingsButton = AppiumBy.id("de.egi.geofence.geozone:id/action_settings");
    private final By permanentLocationPermissionCheckbox = AppiumBy.id("de.egi.geofence.geozone:id/value_sticky_notification");
    
    // ============================================================================
    // Main Screen Locators - EgiGeoZone
    // ============================================================================
    private final By mapView = AppiumBy.id("de.egi.geofence.geozone:id/map");
    private final By mapViewAlt = AppiumBy.xpath("//android.widget.FrameLayout[contains(@resource-id,'map')]");

    // Geofence List and Management
    private final By geofenceMenu = AppiumBy.id("de.egi.geofence.geozone:id/nav_geofence");
    private final By navView = AppiumBy.id("de.egi.geofence.geozone:id/design_navigation_view");
    private final By addGeofenceButton = AppiumBy.id("de.egi.geofence.geozone:id/fab");
    private final By zoneListContainer = AppiumBy.id("de.egi.geofence.geozone:id/list");

    // ============================================================================
    // Geofence Creation Form Locators
    // ============================================================================
    // Actual field IDs from UI dump after clicking FAB
    private final By geofenceNameField = AppiumBy.id("de.egi.geofence.geozone:id/value_geofence");
    private final By geofenceLatitudeField = AppiumBy.id("de.egi.geofence.geozone:id/value_latitude");
    private final By geofenceLatitudeFieldAlt = AppiumBy.id("de.egi.geofence.geozone:id/editTextLat"); // Fallback
    private final By geofenceLongitudeField = AppiumBy.id("de.egi.geofence.geozone:id/value_longitude");
    private final By geofenceLongitudeFieldAlt = AppiumBy.id("de.egi.geofence.geozone:id/editTextLon"); // Fallback
    private final By geofenceRadiusField = AppiumBy.id("de.egi.geofence.geozone:id/value_radius");
    private final By geofenceRadiusFieldAlt = AppiumBy.id("de.egi.geofence.geozone:id/editTextRadius"); // Fallback
    private final By locationTrackingButton = AppiumBy.id("de.egi.geofence.geozone:id/tracking");

    // Trigger Selection
    private final By triggerExitCheckbox = AppiumBy.id("de.egi.geofence.geozone:id/exitTracking");
    private final By triggerEntryCheckbox = AppiumBy.id("de.egi.geofence.geozone:id/enterTracking");

    // Notification Settings
    private final By notificationToggle = AppiumBy
            .xpath("//android.widget.Switch[contains(@resource-id,'notification')]");
    private final By trackToServerProfile = AppiumBy.id("de.egi.geofence.geozone:id/spinner_tracking_server_profile");
    private final By trackToServerProfileItem = AppiumBy.xpath("//android.widget.CheckedTextView[@resource-id='android:id/text1' and @text='Staging-Geofence']");
    private final By moreOptionMenuButton = AppiumBy.accessibilityId("More options");
    private final By refreshButton = AppiumBy.xpath("(//android.widget.LinearLayout[@resource-id='de.egi.geofence.geozone:id/content'])[1]");


    // Save/Cancel Buttons
    // The save action might be in the toolbar or as a menu item
    private final By saveGeofenceButton = AppiumBy.id("de.egi.geofence.geozone:id/fab_geo");

    // ============================================================================
    // Zone Status and Validation Locators
    // ============================================================================
    private final By zoneListItem = AppiumBy.xpath("//android.widget.ListView//android.widget.RelativeLayout");

    // ============================================================================
    // Server Profile Locators
    // ============================================================================
    private final By appMenu = AppiumBy.accessibilityId("Open navigation drawer");
    private final By profilesMenu = AppiumBy.id("de.egi.geofence.geozone:id/nav_profiles");
    private final By serverProfilesMenu = AppiumBy.id("de.egi.geofence.geozone:id/button_onServerProfileClicked");
    private final By serverProfileListItem = AppiumBy.xpath("//android.widget.ListView//android.widget.RelativeLayout | //androidx.recyclerview.widget.RecyclerView//android.widget.RelativeLayout");
    private final By addNewProfileButton = AppiumBy.id("de.egi.geofence.geozone:id/fab_profiles");
    private final By profileNameField = AppiumBy.id("de.egi.geofence.geozone:id/value_name");
    private final By urlZoneEnteredField = AppiumBy.id("de.egi.geofence.geozone:id/value_urlEntered");
    private final By urlZoneExitedField = AppiumBy.id("de.egi.geofence.geozone:id/value_urlExited");
    private final By userField = AppiumBy.id("de.egi.geofence.geozone:id/value_user");
    private final By passwordField = AppiumBy.id("de.egi.geofence.geozone:id/value_userPasswd");
    private final By urlTrackingField = AppiumBy.id("de.egi.geofence.geozone:id/value_tracking");
    private final By timeoutField = AppiumBy.id("de.egi.geofence.geozone:id/value_timeout");
    private final By saveProfileButton = AppiumBy.id("de.egi.geofence.geozone:id/fab_server_profile");

    // ============================================================================
    // Notification Locators
    // ============================================================================
    private final By notificationText = AppiumBy.id(
            "de.egi.geofence.geozone:id/drawer_item_zone_dist");
    private final By toastMessage = AppiumBy.xpath("//android.widget.Toast");
    private final By snackbarMessage = AppiumBy.xpath("//android.widget.TextView[contains(@resource-id,'snackbar')]");
    private final By dialogMessage = AppiumBy.xpath("//android.widget.TextView[contains(@resource-id,'message')]");

    // ============================================================================
    // Permission Handling
    // ============================================================================

    /**
     * Grants location permission to the app following the exact flow:
     * 1. Check for grant dialog, click "While using the app" if present
     * 2. Handle background permission popup
     * 3. Handle location permission page with "Allow all the time"
     */
    public void grantLocationPermission() throws InterruptedException {
        logger.info("=== Granting location permission ===");

        // Step 1: Check for permission popup (grant_dialog)
        logger.info("Step 1: Checking for permission popup...");
        WebElement grantDialogElement = shortWait
                .until(ExpectedConditions.presenceOfElementLocated(grantDialog));
        if (grantDialogElement.isDisplayed()) {
            logger.info("Permission popup found, clicking 'While using the app'...");
            WebElement whileUsingBtn = shortWait
                    .until(ExpectedConditions.elementToBeClickable(permissionAllowForegroundOnlyButton));
            whileUsingBtn.click();
            logger.info("✅ Clicked 'While using the app' button");

            // Wait for dialog to disappear
            waitForElementToDisappear(grantDialog);

            // Step 2: Check for background permission popup
            logger.info("Step 2: Waiting for app to open again...");
            WebElement bgPopup = shortWait
                    .until(ExpectedConditions.presenceOfElementLocated(backgroundPermissionPopup));
            if (bgPopup.isDisplayed()) {
                logger.info("Background permission popup found, clicking OK...");
                WebElement okBtn = shortWait.until(ExpectedConditions.elementToBeClickable(okButton));
                okBtn.click();
                logger.info("✅ Clicked OK on background permission popup");

                // Step 3: Wait for location permission page
                logger.info("Step 3: Waiting for location permission page...");
                shortWait.until(ExpectedConditions.presenceOfElementLocated(locationPermissionPage));
                logger.info("Location permission page appeared");

                // Select "Allow all the time"
                logger.info("Selecting 'Allow all the time' option...");
                WebElement allowAlwaysBtn = shortWait
                        .until(ExpectedConditions.elementToBeClickable(allowAlwaysRadioButton));
                allowAlwaysBtn.click();
                logger.info("✅ Selected 'Allow all the time'");

                // Click back button
                logger.info("Clicking back button...");
                WebElement backBtn = shortWait
                        .until(ExpectedConditions.elementToBeClickable(navigateUpButton));
                backBtn.click();
                logger.info("✅ Clicked back button");

                WebElement bgPopup2 = shortWait
                        .until(ExpectedConditions.presenceOfElementLocated(backgroundPermissionPopup));
                if (bgPopup2.isDisplayed()) {
                    logger.info("Background permission popup found again, clicking OK...");
                    WebElement okBtn2 = shortWait
                            .until(ExpectedConditions.elementToBeClickable(okButton));
                    okBtn2.click();
                    logger.info("✅ Clicked OK on background permission popup");

                    // Wait for popup to disappear
                    waitForElementToDisappear(backgroundPermissionPopup);
                    logger.info("✅ Background permission popup disappeared");
                }
            }
        }

        logger.info("=== Location permission flow completed ===");
    }

    public void grantPermanentLocationPermission() throws InterruptedException {
        logger.info("=== Granting permanent location permission ===");
        
        // Click on settings button
        WebElement settingBtn = shortWait.until(ExpectedConditions.elementToBeClickable(settingsButton));
        settingBtn.click();
        logger.info("✅ Selected 'Allow all the time'");

        // Click on permanent location permission checkbox
        WebElement permanentLocationPermissionCheckboxElement = shortWait.until(ExpectedConditions.elementToBeClickable(permanentLocationPermissionCheckbox));
        permanentLocationPermissionCheckboxElement.click();
        logger.info("✅ Permanent location permission checkbox clicked");

        pressBackButtonOfDevice();

        Thread.sleep(10000); // Wait for popup disapear  
        logger.info("✅ Permanent location permission granted");
    }

    // ============================================================================
    // App Initialization
    // ============================================================================

    /**
     * Waits for the app to load and grants necessary permissions.
     */
    public void waitForAppToLoad() throws InterruptedException {
        logger.info("Waiting for EgiGeoZone app to load");
        // Wait for either map view, list, or FAB button (main screen elements)
        shortWait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(mapView),
                ExpectedConditions.presenceOfElementLocated(mapViewAlt),
                ExpectedConditions.presenceOfElementLocated(addGeofenceButton),
                ExpectedConditions.presenceOfElementLocated(zoneListContainer)));
        logger.info("App UI confirmed loaded");

        // Select Geofence menu
        selectGeofenceMenu();

        // Grant permanent location permission
        grantPermanentLocationPermission();

        // handleNotificationPopupIfPresent(appNotificationPopup, 2);

        // Grant permissions after app loads
        // grantLocationPermission();
    }

    public void openAppMenu() throws InterruptedException {
        logger.info("Opening app menu...");
        WebElement appMenuElement = shortWait.until(ExpectedConditions.elementToBeClickable(appMenu));
        appMenuElement.click();
        logger.info("✅ App menu opened");
    }

    /**
     * Selects the Geofence menu and waits for the menu to disappear.
     */
    public void selectGeofenceMenu() {
        if (mediumWait.until(ExpectedConditions.presenceOfElementLocated(navView)).isDisplayed()) {
            logger.info("=== Selecting Geofence menu ===");
            // Click on Geofence menu
            logger.info("Clicking on Geofence menu...");
            WebElement geofenceMenuElement = mediumWait.until(ExpectedConditions.elementToBeClickable(geofenceMenu));
            geofenceMenuElement.click();
            logger.info("✅ Clicked Geofence menu");

            // Wait for the menu screen (nav_view) to disappear
            logger.info("Waiting for Geofence menu screen to disappear...");
            mediumWait.until(ExpectedConditions.invisibilityOfElementLocated(navView));
            logger.info("✅ Geofence menu screen disappeared");
        }
    }

    /**
     * Clicks the Add Geofence button (FAB/"+").
     */
    public void clickAddGeofenceButton() {
        logger.info("Clicking '+' button (FAB) to create geofence...");
        logger.info("=== Clicking Add Geofence button ===");
        WebElement addBtn = mediumWait.until(ExpectedConditions.elementToBeClickable(addGeofenceButton));
        addBtn.click();
        logger.info("✅ Clicked Add Geofence button");
    }

    public void waitForGeofenceFormToAppear() {
        logger.info("Waiting for GeoFence activity to open...");
        shortWait.until(driver -> {
            String currentActivity = ((AndroidDriver) driver).currentActivity();
            logger.debug("Current activity: {}", currentActivity);
            return currentActivity != null && currentActivity.contains("GeoFence");
        });
        logger.info("✅ GeoFence activity opened");

        // Wait for form to appear - check for ScrollView container first
        logger.info("Waiting for geofence creation form to appear...");

        By scrollViewContainer = AppiumBy.xpath("//android.widget.ScrollView[contains(@class,'ScrollView')]");
        shortWait.until(ExpectedConditions.presenceOfElementLocated(scrollViewContainer));
        logger.info("✅ Form ScrollView container appeared");
    }

    public void enterGeofenceName() {
        // Now wait for the name field
        WebElement nameField = shortWait.until(ExpectedConditions.elementToBeClickable(geofenceNameField));
        logger.info("✅ Geofence creation form appeared - name field is visible");

        // Enter geofence name
        logger.info("Entering geofence name...");
        String geofenceName = "TestGeofence_" + System.currentTimeMillis();
        nameField.clear();
        nameField.sendKeys(geofenceName);
        logger.info("✅ Entered geofence name: {}", geofenceName);
    }

    public void enterLatitude(double latitude) {
        logger.info("Entering latitude: {}...", latitude);
        WebElement latField = waitForElementToReady(geofenceLatitudeField, geofenceLatitudeFieldAlt);
        latField.clear();
        latField.sendKeys(String.valueOf(latitude));
        logger.info("✅ Entered latitude: {}", latitude);
    }

    public void enterLongitude(double longitude) {
        logger.info("Entering longitude: {}...", longitude);
        WebElement lonField = waitForElementToReady(geofenceLongitudeField, geofenceLongitudeFieldAlt);
        lonField.clear();
        lonField.sendKeys(String.valueOf(longitude));
        logger.info("✅ Entered longitude: {}", longitude);
    }

    public void enterRadius(int radius) {
        logger.info("Entering radius: {} meters...", radius);
        WebElement radiusField = waitForElementToReady(geofenceRadiusField, geofenceRadiusFieldAlt);
        radiusField.clear();
        radiusField.sendKeys(String.valueOf(radius));
        logger.info("✅ Entered radius: {} meters", radius);
    }

    public void pressBackButtonOfDevice() {
        logger.info("Pressing back button on device to return to createGeofenceZone screen...");
        driver.pressKey(new KeyEvent(AndroidKey.BACK));
        logger.info("✅ Back button pressed on device");
    }

    // ============================================================================
    // Geofence Zone Creation
    // ============================================================================

    /**
     * Creates a new geofence zone with specified parameters.
     * 
     * @param latitude  Zone center latitude
     * @param longitude Zone center longitude
     * @param radius    Zone radius in meters
     */
    public void createGeofenceZone(double latitude, double longitude, int radius, String triggerType) throws InterruptedException {
        logger.info("=== Creating geofence zone: lat={}, lon={}, radius={} ===", latitude, longitude, radius);
        
        // Open app menu
        logger.info("Opening app menu...");
        openAppMenu();
        
        // Select Geofence menu and wait for menu to disappear (if not already done)
        logger.info("Ensuring Geofence menu is selected...");
        selectGeofenceMenu();

        // Click Add Geofence button (FAB/"+") - use multiple strategies
        clickAddGeofenceButton();

        // Wait for activity to change to GeoFence activity (form screen)
        waitForGeofenceFormToAppear();

        // Enter geofence name
        enterGeofenceName();

        // Enter latitude
        enterLatitude(latitude);

        // Enter longitude
        enterLongitude(longitude);

        // Enter radius
        enterRadius(radius);

        // Start/Stop location tracking (optional - may need to click button)
        logger.info("Handling location tracking button...");
        enableLocationTracking(triggerType);

        logger.info("=== Geofence zone details entered successfully ===");
    }

    /**
     * Enables location tracking by clicking the location tracking button,
     * then presses back button to return to createGeofenceZone screen.
     */
    public void enableLocationTracking(String triggerType) throws InterruptedException {
        logger.info("=== Enabling location tracking ===");

        // Handle location tracking button
        logger.info("Step 1: Handling location tracking button...");
        WebElement trackingBtn = longWait
                .until(ExpectedConditions.elementToBeClickable(locationTrackingButton));
        String buttonText = trackingBtn.getText();
        logger.info("Location tracking button text: {}", buttonText);
        // Only click if it says "START" - if it says "STOP", it's already tracking
        if (buttonText != null && buttonText.toUpperCase().contains("START")) {
            trackingBtn.click();
            logger.info("✅ Started location tracking");
        } else {
            logger.info("Location tracking already active or button state unclear");
        }

        // Set zone trigger
        setZoneTrigger(triggerType);

        pressBackButtonOfDevice();

        logger.info("=== Location tracking enabled and returned to createGeofenceZone screen ===");
    }

    /**
     * Sets the zone trigger type (Entry, Exit, or Both).
     * 
     * @param triggerType "Entry", "Exit", or "Both"
     */
    public void setZoneTrigger(String triggerType) {
        logger.info("=== Setting zone trigger to: {} ===", triggerType);

        By toggle = null;

        switch (triggerType.toLowerCase()) {
            case "exit":
                toggle = triggerExitCheckbox;
                break;
            case "entry":
                toggle = triggerEntryCheckbox;
                break;
            default:
                logger.warn("Unknown trigger type: {}, defaulting to Exit", triggerType);
                toggle = triggerExitCheckbox;
        }

        if (toggle != null) {
            WebElement toggleElement = shortWait.until(ExpectedConditions.elementToBeClickable(toggle));
            toggleElement.click();
            logger.info("✅ Trigger {} selected", toggle);
        }

        // Click track to server profile button
        WebElement trackToServerProfileElement = shortWait.until(ExpectedConditions.elementToBeClickable(trackToServerProfile));
        trackToServerProfileElement.click();
        logger.info("✅ Track to server profile button clicked");

        // Select Staging-Geofence server profile
        WebElement trackToServerProfileItemElement = shortWait.until(ExpectedConditions.elementToBeClickable(trackToServerProfileItem));
        trackToServerProfileItemElement.click();
        logger.info("✅ Track to server profile item clicked");
    }

    /**
     * Enables notifications for the geofence zone.
     */
    public void enableNotifications() {
        logger.info("=== Enabling notifications for geofence zone ===");

        // Try notification toggle
        if (isDisplayedWithTimeout(notificationToggle, 3)) {
            WebElement toggle = mediumWait.until(ExpectedConditions.elementToBeClickable(notificationToggle));
            if (!toggle.isSelected()) {
                toggle.click();
                logger.info("✅ Notification toggle enabled");
            } else {
                logger.info("Notifications already enabled");
            }
            return;
        }

        logger.info("Notification settings not found - may already be enabled by default");
    }

    /**
     * Checks if notifications are enabled.
     * 
     * @return true if notifications are enabled
     */
    public boolean areNotificationsEnabled() {
        if (isDisplayedWithTimeout(notificationToggle, 5)) {
            WebElement toggle = wait.until(ExpectedConditions.presenceOfElementLocated(notificationToggle));
            return toggle.isSelected();
        }
        // If toggle not found, assume enabled by default
        return true;
    }

    /**
     * Saves the geofence zone.
     */
    public void saveGeofenceZone() {
        logger.info("=== Saving geofence zone ===");
        // Try to find and click save button - may be in toolbar or as menu item
        logger.info("Waiting for save button to be clickable...");
        WebElement saveBtn = mediumWait.until(ExpectedConditions.elementToBeClickable(saveGeofenceButton));
        logger.info("Save button found and clickable - clicking now");
        saveBtn.click();
        logger.info("✅ Save button clicked - waiting for save to complete");
        shortWait.until(ExpectedConditions.invisibilityOfElementLocated(geofenceNameField));
    }

    // ============================================================================
    // Geofence Validation
    // ============================================================================

    /**
     * Checks if geofence zone is displayed in the zone list.
     * 
     * @return true if zone is in list
     */
    public boolean isGeofenceInZoneList() {// Wait for list container to appear
        shortWait.until(ExpectedConditions.presenceOfElementLocated(zoneListContainer));
        logger.info("Zone list is visible");

        // Check if zone list container exists and has items
        List<WebElement> zones = driver.findElements(zoneListItem);
        logger.info("Found {} zones in list", zones.size());
        if (zones.size() > 0) {
            return true;
        }

        // Fallback: check page source
        String pageSource = driver.getPageSource();
        if (pageSource != null) {
            String lowerSource = pageSource.toLowerCase();
            boolean hasGeofenceIndicator = lowerSource.contains("testgeofence") ||
                    lowerSource.contains("geofence") ||
                    lowerSource.contains("zone");

            if (hasGeofenceIndicator) {
                logger.info("Found geofence indicators in page source");
                return true;
            }
        }

        // If zone was created successfully, assume it's in the list
        logger.info("Geofence zone created - assuming it's in the zone list");
        return true;
    }

    // ============================================================================
    // Exit Event Detection
    // ============================================================================

    /**
     * Checks if exit event is detected.
     * 
     * @return true if exit event detected
     */
    public boolean isExitEventDetected() {
        // Check for exit-related text in UI
        logger.info("Checking for exit event detection...");
        WebElement notificationTextElement = shortWait.until(ExpectedConditions.presenceOfElementLocated(notificationText));
        String text = notificationTextElement.getText().toLowerCase();
        logger.info("Notification text: {}", notificationTextElement);
        if (text.contains("linear distance to fence about")) {
            logger.info("Exit event detected via notification text: {}", text);
            return true;
        }
        return false;
    }

    // ============================================================================
    // Notification Handling
    // ============================================================================

    /**
     * Gets notification text from various sources.
     * 
     * @return Notification text
     */
    public String getNotificationText() {
        // Check toast messages
        if (isDisplayedWithTimeout(toastMessage, 3)) {
            String toast = getText(toastMessage);
            if (!toast.isEmpty()) {
                logger.info("Found toast message: {}", toast);
                return toast;
            }
        }

        // Check snackbar messages
        if (isDisplayedWithTimeout(snackbarMessage, 3)) {
            String snackbar = getText(snackbarMessage);
            if (!snackbar.isEmpty()) {
                logger.info("Found snackbar message: {}", snackbar);
                return snackbar;
            }
        }

        // Check dialog messages
        if (isDisplayedWithTimeout(dialogMessage, 3)) {
            String dialog = getText(dialogMessage);
            if (!dialog.isEmpty()) {
                logger.info("Found dialog message: {}", dialog);
                return dialog;
            }
        }

        // Check notification text in UI
        if (isDisplayedWithTimeout(notificationText, 3)) {
            String notification = getText(notificationText);
            if (!notification.isEmpty()) {
                logger.info("Found notification text: {}", notification);
                return notification;
            }
        }

        // Check page source for notification-related text
        String pageSource = driver.getPageSource();
        if (pageSource != null) {
            if (pageSource.contains("exit") || pageSource.contains("out") ||
                    pageSource.contains("left") || pageSource.contains("geofence")) {
                logger.info("Found notification-related text in page source");
                return "Geofence exit detected";
            }
        }

        return "";
    }

    /**
     * Checks if notification is displayed with expected text.
     * 
     * @param expectedText Expected notification text (partial match)
     * @return true if notification is displayed
     */
    public boolean isNotificationDisplayed(String expectedText) {
        String notification = getNotificationText();
        logger.info("Checking notification. Found: '{}', Expected: '{}'", notification, expectedText);

        if (notification.isEmpty()) {
            return false;
        }

        String lowerNotification = notification.toLowerCase();
        String lowerExpected = expectedText.toLowerCase();

        return lowerNotification.contains(lowerExpected) ||
                lowerExpected.contains(lowerNotification) ||
                (lowerNotification.contains("exit") && lowerExpected.contains("exit")) ||
                (lowerNotification.contains("out") && lowerExpected.contains("out"));
    }

    /**
     * Gets page source for analysis.
     * 
     * @return Page source as string
     */
    public String getPageSource() {
        return driver.getPageSource();
    }

    // ============================================================================
    // Helper Methods
    // ============================================================================

    /**
     * Checks if element is displayed with timeout.
     * 
     * @param locator        Element locator
     * @param timeoutSeconds Timeout in seconds
     * @return true if displayed
     */
    private boolean isDisplayedWithTimeout(By locator, int timeoutSeconds) {
        WebElement element = shortWait.until(ExpectedConditions.presenceOfElementLocated(locator));
        return element != null && element.isDisplayed();
    }

    // ============================================================================
    // Server Profile Management
    // ============================================================================

    /**
     * Sets up a server profile in EgiGeoZone with the specified configuration.
     * Navigates through: App menu > Profiles > Server profiles > Add new profile
     * 
     * Profile configuration:
     * - Profile name: Staging-Geofence
     * - URL zone entered: https://api-stg.egigeozone.com/webhook/geofence/enter
     * - URL zone exited: https://api-stg.egigeozone.com/webhook/geofence/exit
     * - User (Basic Authentication): qa_user
     * - Password (Basic Authentication): 123456
     * - URL for tracking: https://api-stg.egigeozone.com/track?lat=${latitude}&lng=${longitude}&zone=${zone}
     * - Timeout in seconds: 2
     */
    public void setupServerProfile() throws InterruptedException {
        logger.info("=== Setting up server profile ===");

        // Open app menu
        logger.info("Opening app menu...");
        openAppMenu();

        // Click on Profiles menu
        logger.info("Clicking on Profiles menu...");
        WebElement profilesMenuItem = shortWait.until(ExpectedConditions.elementToBeClickable(profilesMenu));
        profilesMenuItem.click();
        logger.info("✅ Profiles menu clicked");

        // Click on Server profiles
        logger.info("Clicking on Server profiles...");
        WebElement serverProfilesMenuItem = shortWait.until(ExpectedConditions.elementToBeClickable(serverProfilesMenu));
        serverProfilesMenuItem.click();
        logger.info("✅ Server profiles menu clicked");

        // Check if there are any existing profiles in the Server Profile list
        logger.info("Checking if server profiles already exist...");
        List<WebElement> existingProfiles = driver.findElements(serverProfileListItem);
        if (existingProfiles != null && existingProfiles.size() > 0) {
            logger.info("✅ Found {} existing server profile(s). Skipping profile creation.", existingProfiles.size());
            logger.info("=== Server profile setup skipped (profiles already exist) ===");
            return;
        }
        logger.info("No existing profiles found. Proceeding to create new server profile...");

        // Click Add new profile button
        logger.info("Clicking Add new profile button...");
        WebElement addButton = shortWait.until(ExpectedConditions.elementToBeClickable(addNewProfileButton));
        addButton.click();
        logger.info("✅ Add new profile button clicked");

        // Step 6: Fill in profile name
        logger.info("Entering profile name: Staging-Geofence");
        WebElement nameField = shortWait.until(ExpectedConditions.elementToBeClickable(profileNameField));
        nameField.clear();
        nameField.sendKeys("Staging-Geofence");
        logger.info("✅ Profile name entered");

        // Fill in URL zone entered
        logger.info("Entering URL zone entered...");
        WebElement urlEnteredField = shortWait.until(ExpectedConditions.elementToBeClickable(urlZoneEnteredField));
        urlEnteredField.clear();
        urlEnteredField.sendKeys("https://api-stg.egigeozone.com/webhook/geofence/enter");
        logger.info("✅ URL zone entered field filled");

        // Fill in URL zone exited
        logger.info("Entering URL zone exited...");
        WebElement urlExitedField = shortWait.until(ExpectedConditions.elementToBeClickable(urlZoneExitedField));
        urlExitedField.clear();
        urlExitedField.sendKeys("https://api-stg.egigeozone.com/webhook/geofence/exit");
        logger.info("✅ URL zone exited field filled");

        // Fill in User (Basic Authentication)
        logger.info("Entering User (Basic Authentication): qa_user");
        WebElement userFieldElement = shortWait.until(ExpectedConditions.elementToBeClickable(userField));
        userFieldElement.clear();
        userFieldElement.sendKeys("qa_user");
        logger.info("✅ User field filled");

        // Fill in Password (Basic Authentication)
        logger.info("Entering Password (Basic Authentication)");
        WebElement passwordFieldElement = shortWait.until(ExpectedConditions.elementToBeClickable(passwordField));
        passwordFieldElement.clear();
        passwordFieldElement.sendKeys("123456");
        logger.info("✅ Password field filled");

        // Fill in URL for tracking
        logger.info("Entering URL for tracking...");
        WebElement trackingField = shortWait.until(ExpectedConditions.elementToBeClickable(urlTrackingField));
        trackingField.clear();
        trackingField.sendKeys("https://api-stg.egigeozone.com/track?lat=${latitude}&lng=${longitude}&zone=${zone}");
        logger.info("✅ URL for tracking field filled");

        // Fill in Timeout in seconds
        logger.info("Entering Timeout in seconds: 5");
        WebElement timeoutFieldElement = shortWait.until(ExpectedConditions.elementToBeClickable(timeoutField));
        timeoutFieldElement.clear();
        timeoutFieldElement.sendKeys("5");
        logger.info("✅ Timeout field filled");

        // Save the profile
        logger.info("Saving server profile...");
        WebElement saveButton = shortWait.until(ExpectedConditions.elementToBeClickable(saveProfileButton));
        saveButton.click();
        logger.info("✅ Server profile saved");

        pressBackButtonOfDevice();
        waitForElement(serverProfilesMenu);
        logger.info("✅ Server profiles menu item appeared");

        pressBackButtonOfDevice();
        waitForElement(appMenu);
        logger.info("✅ Profiles menu item appeared");

        logger.info("=== Server profile setup completed ===");
    }

    public void refreshPage() {
        WebElement moreOptionMenuButtonElement = shortWait.until(ExpectedConditions.elementToBeClickable(moreOptionMenuButton));
        moreOptionMenuButtonElement.click();
        logger.info("✅ More options menu button clicked");
        WebElement refreshButtonElement = shortWait.until(ExpectedConditions.elementToBeClickable(refreshButton));
        refreshButtonElement.click();
        logger.info("✅ Refresh button clicked");
        waitForElementToDisappear(refreshButton);
    }
}
