# Geofence Mobile Automation Framework - POC

[![Java](https://img.shields.io/badge/Java-11+-blue.svg)](https://www.oracle.com/java/)
[![Appium](https://img.shields.io/badge/Appium-8.6.0-green.svg)](https://appium.io/)
[![TestNG](https://img.shields.io/badge/TestNG-7.8.0-red.svg)](https://testng.org/)
[![Allure](https://img.shields.io/badge/Allure-2.24.0-orange.svg)](https://qameta.io/allure-report/)

A production-ready Proof of Concept (POC) automation framework for mobile geofence testing using **Java**, **Appium**, **TestNG**, **RestAssured**, and **Allure**. This framework demonstrates industry best practices including SOLID principles, Page Object Model, reusable services, environment configuration, and comprehensive reporting.

## 🎯 Framework Overview

This framework provides a scalable foundation for mobile automation with:

- **Clean Architecture**: Separation of concerns with clear layer boundaries
- **SOLID Principles**: Maintainable and extensible code structure
- **Page Object Model**: Reusable page/screen objects for UI interactions
- **Service Layer**: Business logic abstraction (Jira integration, location simulation)
- **Configuration Management**: Centralized configuration with environment overrides
- **Comprehensive Reporting**: Allure reports with screenshots, logs, and attachments
- **CI/CD Integration**: Jenkins pipeline with automated test execution
- **Jira Integration**: Automatic defect creation with screenshots and Allure links

## 📋 Table of Contents

- [Features](#-features)
- [Architecture](#-architecture)
- [Prerequisites](#-prerequisites)
- [Quick Start](#-quick-start)
- [Test Scenarios](#-test-scenarios)
- [Project Structure](#-project-structure)
- [Configuration](#-configuration)
- [Running Tests](#-running-tests)
- [CI/CD Pipeline](#-cicd-pipeline)
- [Atlassian Integration](#-atlassian-integration)
- [AI & Copilot Integration](#-ai--copilot-integration)
- [Best Practices](#-best-practices)
- [Troubleshooting](#-troubleshooting)

## ✨ Features

### Core Capabilities
- ✅ Android mobile automation with Appium
- ✅ Support for both real devices and emulators
- ✅ Automatic APK installation
- ✅ Location simulation for geofence testing
- ✅ Comprehensive test reporting with Allure
- ✅ Screenshot capture on test failures
- ✅ Parallel test execution support

### Integration Features
- ✅ **Jira Integration**: Automatic defect creation on test or configuration failures
- ✅ **RestAssured**: Jira integration via REST API
- ✅ **Jenkins CI/CD**: Automated pipeline with reporting
- ✅ **AI Copilot Ready**: MCP-compatible structure for AI assistance

## 🏗️ Architecture

### Design Principles

1. **SOLID Principles**
   - **Single Responsibility**: Each class has one clear purpose
   - **Open/Closed**: Extensible without modification
   - **Liskov Substitution**: Proper inheritance hierarchy
   - **Interface Segregation**: Focused interfaces
   - **Dependency Inversion**: Depend on abstractions

2. **Page Object Model (POM)**
   - Screen objects encapsulate UI interactions
   - Reusable methods for common actions
   - Clear separation between test logic and UI details

3. **Service Layer Pattern**
   - Business logic separated from UI interactions
   - Reusable services (Atlassian, Location Simulation)
   - Easy to mock for unit testing

### Layer Structure

```
┌─────────────────────────────────────┐
│         Test Layer                  │
│  (TestNG Test Classes)              │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│      Page Object Layer              │
│  (MainScreen, BasePage)             │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│      Service Layer                  │
│  (AtlassianService, LocationSim)    │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│      Core Layer                     │
│  (DriverManager, ConfigManager)    │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│      Utility Layer                  │
│  (ScreenshotUtil, LoggerUtil)      │
└─────────────────────────────────────┘
```

## 📦 Prerequisites

### Required Software

1. **Java JDK 11+**
   ```bash
   java -version
   ```

2. **Maven 3.6+**
   ```bash
   mvn -version
   ```

3. **Node.js & Appium**
   ```bash
   npm install -g appium
   npm install -g appium-uiautomator2-driver
   appium --version
   ```

4. **Android SDK**
   - Install Android Studio
   - Set `ANDROID_HOME` environment variable
   - Add `$ANDROID_HOME/platform-tools` to PATH

5. **IntelliJ IDEA** (Recommended IDE)

### Device Setup

#### Option 1: Android Emulator (with UI visible)

**Important**: The emulator must be started with UI visible (not headless) to see test execution.

**Method 1: Using the provided script (Recommended)**
```bash
./start-emulator-ui.sh
```

**Method 2: Manual start with UI**
```bash
# List available AVDs
emulator -list-avds

# Start emulator with UI visible (DO NOT use -no-window flag)
emulator -avd Medium_Phone_API_36.0

# Or for other AVDs:
emulator -avd <your-avd-name>
```

**Method 3: Via Android Studio**
1. Open Android Studio → AVD Manager
2. Click the ▶️ Play button next to your AVD
3. Ensure the emulator window appears on your screen

**Verify emulator is running:**
```bash
adb devices
# Should show: emulator-5554    device
```

**Troubleshooting:**
- If emulator window doesn't appear, check if it's minimized or on another desktop
- Make sure you're NOT using `-no-window` or headless mode flags
- Restart the emulator if UI is not visible

#### Option 2: Real Device
1. Enable Developer Options
2. Enable USB Debugging
3. Connect device via USB
4. Verify connection:
   ```bash
   adb devices
   ```

## 🚀 Quick Start

### 1. Clone Repository
```bash
git clone <repository-url>
cd geofence
```

### 2. Configure Application
Ensure the EgiGeoZone APK is in the `file/` directory:
```
file/EgiGeoZone Geofence_3.2.7_APKPure.apk
```

### 3. Update Configuration
Edit `src/main/resources/application.properties`:
```properties
device.udid=emulator-5554  # or your device UDID
device.name=Android Emulator
```

### 4. Start Appium Server
```bash
appium
```

### 5. Run Tests
```bash
mvn clean test
```

### 6. Generate Allure Report
```bash
mvn allure:serve
```

## 🧪 Test Scenarios

### Scenario 1: Geofence Zone Creation and Activation

**Given** the user is on the EgiGeoZone main screen  
**When** the user creates a new geofence zone
- And the zone center is set to the current device location
- And the zone radius is set to "100" meters
- And the zone trigger includes "Exit"
- And notifications are enabled for the zone
- And the user saves the zone  
**Then** the geofence zone should be created successfully
- And the zone should be registered with the system geofencing service
- And the zone should be displayed in the zone list
- And the zone status should indicate "Active"

**Test Class**: `GeofenceLocationSetupTest.testGeofenceLocationSetup()`

### Scenario 2: Exit Geofence Detection

**Given** a geofence zone is created with radius "100" meters at the current device location
- And notifications for this zone are enabled
- And the device location is set inside the zone  
**When** the device moves outside the geofence area  
**Then** the system should detect an exit event from the zone
- And EgiGeoZone should show a notification for the exit event
- And the notification message should contain the exit event keyword

**Test Class**: `ExitGeofenceTest.testExitGeofence150m()`

## 📁 Project Structure

```
geofence/
├── .gitignore
├── Jenkinsfile                 # CI/CD pipeline definition
├── pom.xml                     # Maven dependencies
├── README.md                   # This file
├── ARCHITECTURE.md             # Detailed architecture documentation
├── file/                       # APK files
│   └── EgiGeoZone Geofence_3.2.7_APKPure.apk
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/automation/
│   │   │       ├── base/              # Base classes
│   │   │       │   ├── BasePage.java
│   │   │       │   └── BaseTest.java
│   │   │       ├── core/              # Core framework components
│   │   │       │   ├── ConfigManager.java
│   │   │       │   └── DriverManager.java
│   │   │       ├── listeners/         # TestNG listeners
│   │   │       │   ├── AllureListener.java
│   │   │       │   └── TestListener.java
│   │   │       ├── pages/             # Page Objects
│   │   │       │   └── MainScreen.java
│   │   │       ├── services/          # Service layer
│   │   │       │   └── AtlassianService.java
│   │   │       └── utils/              # Utility classes
│   │   │           ├── LocationSimulator.java
│   │   │           ├── LoggerUtil.java
│   │   │           └── ScreenshotUtil.java
│   │   └── resources/
│   │       ├── application.properties # Configuration
│   │       ├── allure.properties
│   │       └── logback.xml
│   │
│   └── test/
│       ├── java/
│       │   └── com/automation/tests/   # Test classes
│       │       ├── ExitGeofenceTest.java
│       │       └── GeofenceLocationSetupTest.java
│       └── resources/
│           └── testng.xml              # TestNG suite configuration
│
└── target/                             # Build output (ignored)
```

## ⚙️ Configuration

### Application Properties

Key configuration in `src/main/resources/application.properties`:

```properties
# Device Configuration
device.platform=Android
device.udid=emulator-5554
device.app.package=de.egi.geofence.geozone
device.app.activity=.MainEgiGeoZone

# Test Configuration
test.timeout.implicit=10
test.timeout.explicit=30

# Atlassian Integration
atlassian.base.url=https://ngantran0792.atlassian.net
atlassian.jira.project.key=DEV
atlassian.api.email=ngan.tran0792@gmail.com
```

### Environment Variables

Override properties via environment variables:
```bash
export DEVICE_UDID=emulator-5554
export ATLASSIAN_API_TOKEN=your_token_here
```

## 🏃 Running Tests

### Run All Tests
```bash
mvn clean test
```

### Run Specific Test Class
```bash
mvn test -Dtest=GeofenceLocationSetupTest
```

### Run with TestNG Suite
```bash
mvn test -DsuiteXmlFile=testng.xml
```

### Generate Allure Report
```bash
mvn allure:report
mvn allure:serve  # Opens report in browser
```

## 🔄 CI/CD Pipeline

### Jenkins Pipeline Stages

1. **Checkout**: Clone repository
2. **Environment Setup**: Verify tools and versions
3. **Create Jira Task**: Create tracking task for test run
4. **Start Appium Server**: Launch Appium service
5. **Build Project**: Compile source code
6. **Run Tests**: Execute test suite
7. **Generate Allure Report**: Create test reports
8. **Publish Allure Report**: Make reports available
9. **Create Jira Defects**: Auto-create defects for failures

### Pipeline Configuration

See `Jenkinsfile` for complete pipeline definition.

### Running Pipeline Locally

```bash
# Install Jenkins CLI (if available)
jenkins-cli build <job-name>
```

## 🔗 Atlassian Integration

### Jira Integration

**Automatic Defect Creation**:
- On test or configuration failure, `TestListener` automatically calls `AtlassianService` to create a Jira defect.
- Includes:
  - Test details (class, method, error message)
  - Stack trace
  - Screenshot attachment
  - Allure/CI build link as a Jira comment when CI environment variables are present.

**Configuration (recommended pattern)**:
- Configure Jira connection via `application.properties` **or** environment variables.
- For security, prefer environment variables so tokens are not checked into source control.

```properties
# application.properties
atlassian.base.url=https://<your-domain>.atlassian.net
atlassian.jira.project.key=DEV
atlassian.api.email=your_email@example.com

# Optional, but usually supplied via environment variable instead of file:
# atlassian.api.token=
```

```bash
# Environment variables override properties
export ATLASSIAN_API_TOKEN=your_token_here
export ATLASSIAN_API_EMAIL=your_email@example.com
```

> Note: Confluence integration is not currently implemented in the codebase. Any Confluence-related behavior should be treated as a future extension.

## 🤖 AI & Copilot Integration

### MCP (Model Context Protocol) Compatibility

The framework is structured for AI-assisted development:

1. **Clear Separation of Concerns**: AI can understand and modify specific layers
2. **Consistent Naming**: Predictable class and method names
3. **Comprehensive Documentation**: Inline comments and JavaDoc
4. **Standard Patterns**: Industry-standard design patterns

### IntelliJ IDEA Copilot Integration

1. Install GitHub Copilot plugin
2. Enable in IntelliJ Settings
3. Framework code is optimized for Copilot suggestions

### AI-Assisted Development Workflow

```bash
# AI can help with:
- Generating new test cases
- Creating page objects
- Updating locators
- Refactoring code
- Writing documentation
```

## 📚 Best Practices

### Code Organization
- ✅ One class per file
- ✅ Package structure follows domain model
- ✅ Clear naming conventions
- ✅ Comprehensive JavaDoc

### Test Design
- ✅ One test method per scenario
- ✅ Descriptive test names
- ✅ Clear test steps with Allure annotations
- ✅ Proper assertions with meaningful messages

### Error Handling
- ✅ Graceful degradation
- ✅ Comprehensive logging
- ✅ Screenshot capture on failures
- ✅ Automatic defect creation

### Maintainability
- ✅ DRY (Don't Repeat Yourself)
- ✅ SOLID principles
- ✅ Page Object Model
- ✅ Configuration externalization

## 🐛 Troubleshooting

### Common Issues

**Issue**: Driver initialization fails
- **Solution**: Verify Appium server is running (`appium`)
- **Solution**: Check device connection (`adb devices`)

**Issue**: APK installation fails
- **Solution**: Check SDK version compatibility
- **Solution**: Verify APK path in `application.properties`

**Issue**: Tests timeout
- **Solution**: Increase timeout values in `application.properties`
- **Solution**: Check device performance

**Issue**: Jira defect creation fails
- **Solution**: Verify API token and credentials
- **Solution**: Check network connectivity

### Debug Mode

Enable debug logging:
```properties
logging.level.com.automation=DEBUG
```

## 📝 License

This is a POC framework for demonstration purposes.

## 👥 Contributors

Automation Team

## 📞 Support

For issues or questions, please create a Jira ticket in the DEV project.

---

**Last Updated**: 2026-01-05  
**Framework Version**: 1.0.0-SNAPSHOT
