package com.automation.services;

import com.automation.core.ConfigManager;
import com.automation.utils.LoggerUtil;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.testng.ITestResult;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class AtlassianService {

    private static final Logger logger = LoggerUtil.getLogger(AtlassianService.class);

    private final ConfigManager config;
    private final String baseUrl;
    private final String apiToken;
    private final String apiEmail;
    private final String jiraProjectKey;
    private final boolean autoCreateDefect;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public AtlassianService() {
        // 🔒 GIỮ NGUYÊN CONFIG CỦA BẠN
        this.config = ConfigManager.getInstance();
        this.baseUrl = config.getProperty("atlassian.base.url", "");
        this.apiToken = config.getProperty("atlassian.api.token", "");
        this.apiEmail = config.getProperty("atlassian.api.email", "");
        this.jiraProjectKey = config.getProperty("atlassian.jira.project.key", "DEV");
        this.autoCreateDefect = config.getBooleanProperty("atlassian.auto.create.defect", true);

        if (baseUrl.isEmpty() || apiToken.isEmpty()) {
            logger.warn("Atlassian credentials not configured. Integration will be skipped.");
        }
    }

    /**
     * Create Jira defect when test fails
     */
    public String createDefect(ITestResult testResult, String screenshotPath) {

        if (!autoCreateDefect) return null;
        if (baseUrl.isEmpty() || apiToken.isEmpty()) return null;

        try {
            String summary = String.format("Test Failure - %s",
                    testResult.getMethod().getMethodName());

            StringBuilder rawDesc = new StringBuilder();
            rawDesc.append("Test Failure Details\n\n");
            rawDesc.append("Test Class: ").append(testResult.getTestClass().getName()).append("\n");
            rawDesc.append("Test Method: ").append(testResult.getMethod().getMethodName()).append("\n");
            rawDesc.append("Execution Time: ")
                    .append(LocalDateTime.now().format(DATE_FORMATTER)).append("\n\n");

            if (testResult.getThrowable() != null) {
                rawDesc.append("Error Message:\n")
                        .append(testResult.getThrowable().getMessage()).append("\n\n");
                rawDesc.append("Stack Trace:\n")
                        .append(getStackTrace(testResult.getThrowable()));
            }

            // ========== BUILD PAYLOAD ==========
            Map<String, Object> fields = new HashMap<>();
            fields.put("project", Map.of("key", jiraProjectKey));
            fields.put("summary", summary);
            fields.put("issuetype",
                    Map.of("name",
                            config.getProperty("atlassian.jira.issue.type", "Bug")));

            // ⚠️ Jira Cloud requires ADF format for description
            fields.put("description", buildAdf(rawDesc.toString()));

            Map<String, Object> payload = Map.of("fields", fields);

            // ========== CALL API ==========
            Response response = RestAssured.given()
                    .baseUri(baseUrl)
                    .auth().preemptive().basic(apiEmail, apiToken)
                    .contentType(ContentType.JSON)
                    .body(payload)
                    .post("/rest/api/3/issue");

            if (response.statusCode() != 201) {
                logger.error("❌ Create Jira issue failed. Status: {}", response.statusCode());
                logger.error("Response: {}", response.asString());
                return null;
            }

            String issueKey = response.jsonPath().getString("key");
            logger.info("✅ Jira defect created: {}", issueKey);

            // Attach screenshot if exists
            if (screenshotPath != null && new File(screenshotPath).exists()) {
                attachFileToJira(issueKey, screenshotPath);
            }

            // Add Allure link as comment
            addAllureLinkToJira(issueKey);

            return issueKey;

        } catch (Exception e) {
            logger.error("Error while creating Jira defect", e);
            return null;
        }
    }

    // =========================================================
    // ===================== ATTACH FILE =======================
    // =========================================================
    private void attachFileToJira(String issueKey, String filePath) {
        try {
            File file = new File(filePath);

            Response response = RestAssured.given()
                    .baseUri(baseUrl)
                    .auth().preemptive().basic(apiEmail, apiToken)
                    .header("X-Atlassian-Token", "no-check")
                    .multiPart("file", file)
                    .post("/rest/api/3/issue/" + issueKey + "/attachments");

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                logger.info("📎 Screenshot attached to {}", issueKey);
            } else {
                logger.warn("Attach failed. Status: {}", response.statusCode());
            }

        } catch (Exception e) {
            logger.warn("Attach screenshot failed (non-blocking)", e);
        }
    }

    // =========================================================
    // ===================== ADD COMMENT =======================
    // =========================================================
    private void addAllureLinkToJira(String issueKey) {
        try {
            String buildUrl = System.getenv("BUILD_URL");

            if (buildUrl == null || buildUrl.isEmpty()) {
                // GitHub Actions fallback
                String server = System.getenv("GITHUB_SERVER_URL");
                String repo = System.getenv("GITHUB_REPOSITORY");
                String runId = System.getenv("GITHUB_RUN_ID");

                if (server != null && repo != null && runId != null) {
                    buildUrl = server + "/" + repo + "/actions/runs/" + runId;
                }
            }

            if (buildUrl == null || buildUrl.isEmpty()) {
                logger.debug("No CI build URL found. Skip adding Allure link.");
                return;
            }

            String commentText = "🔗 Allure report: " + buildUrl + "/allure/";

            Map<String, Object> payload = Map.of(
                    "body", buildAdf(commentText)
            );

            Response response = RestAssured.given()
                    .baseUri(baseUrl)
                    .auth().preemptive().basic(apiEmail, apiToken)
                    .contentType(ContentType.JSON)
                    .body(payload)
                    .post("/rest/api/3/issue/" + issueKey + "/comment");

            if (response.statusCode() == 201) {
                logger.info("💬 Allure link added to {}", issueKey);
            } else {
                logger.warn("Add Allure comment failed. Status: {}", response.statusCode());
            }

        } catch (Exception e) {
            logger.warn("Failed to add Allure link (non-blocking)", e);
        }
    }

    // =========================================================
    // ===================== ADF BUILDER =======================
    // =========================================================
    private Map<String, Object> buildAdf(String text) {
        return Map.of(
                "type", "doc",
                "version", 1,
                "content", new Object[]{
                        Map.of(
                                "type", "paragraph",
                                "content", new Object[]{
                                        Map.of(
                                                "type", "text",
                                                "text", text
                                        )
                                }
                        )
                }
        );
    }

    // =========================================================
    // ===================== STACK TRACE ======================
    // =========================================================
    private String getStackTrace(Throwable throwable) {
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }
}
