#!/bin/bash

# Test Jira API Connection
# This script helps verify Jira credentials and permissions

echo "=== Testing Jira API Connection ==="
echo ""

# Read configuration from application.properties
BASE_URL=$(grep "atlassian.base.url" src/main/resources/application.properties | cut -d'=' -f2)
API_EMAIL=$(grep "atlassian.api.email" src/main/resources/application.properties | cut -d'=' -f2)
API_TOKEN=$(grep "atlassian.api.token" src/main/resources/application.properties | cut -d'=' -f2)
PROJECT_KEY=$(grep "atlassian.jira.project.key" src/main/resources/application.properties | cut -d'=' -f2)

echo "Base URL: $BASE_URL"
echo "Email: $API_EMAIL"
echo "Project Key: $PROJECT_KEY"
echo ""

# Test 1: Verify API authentication
echo "Test 1: Verifying API authentication..."
AUTH_TEST=$(curl -s -u "$API_EMAIL:$API_TOKEN" \
    -X GET \
    "$BASE_URL/rest/api/3/myself" \
    -w "\nHTTP_STATUS:%{http_code}")

HTTP_STATUS=$(echo "$AUTH_TEST" | grep "HTTP_STATUS" | cut -d':' -f2)
if [ "$HTTP_STATUS" = "200" ]; then
    echo "✅ Authentication successful!"
    echo "User info:"
    echo "$AUTH_TEST" | grep -v "HTTP_STATUS" | jq -r '.displayName + " (" + .emailAddress + ")"' 2>/dev/null || echo "$AUTH_TEST" | grep -v "HTTP_STATUS"
else
    echo "❌ Authentication failed (Status: $HTTP_STATUS)"
    echo "Response: $AUTH_TEST"
    exit 1
fi
echo ""

# Test 2: Check if project exists
echo "Test 2: Checking if project '$PROJECT_KEY' exists..."
PROJECT_TEST=$(curl -s -u "$API_EMAIL:$API_TOKEN" \
    -X GET \
    "$BASE_URL/rest/api/3/project/$PROJECT_KEY" \
    -w "\nHTTP_STATUS:%{http_code}")

HTTP_STATUS=$(echo "$PROJECT_TEST" | grep "HTTP_STATUS" | cut -d':' -f2)
if [ "$HTTP_STATUS" = "200" ]; then
    echo "✅ Project '$PROJECT_KEY' exists!"
    echo "Project info:"
    echo "$PROJECT_TEST" | grep -v "HTTP_STATUS" | jq -r '.name + " (" + .key + ")"' 2>/dev/null || echo "$PROJECT_TEST" | grep -v "HTTP_STATUS"
else
    echo "❌ Project '$PROJECT_KEY' not found (Status: $HTTP_STATUS)"
    echo "Response: $PROJECT_TEST"
    exit 1
fi
echo ""

# Test 3: Check user permissions in project
echo "Test 3: Checking user permissions in project '$PROJECT_KEY'..."
PERM_TEST=$(curl -s -u "$API_EMAIL:$API_TOKEN" \
    -X GET \
    "$BASE_URL/rest/api/3/project/$PROJECT_KEY/permissions" \
    -w "\nHTTP_STATUS:%{http_code}")

HTTP_STATUS=$(echo "$PERM_TEST" | grep "HTTP_STATUS" | cut -d':' -f2)
if [ "$HTTP_STATUS" = "200" ]; then
    echo "✅ Permission check successful!"
    echo "Permissions:"
    echo "$PERM_TEST" | grep -v "HTTP_STATUS" | jq '.' 2>/dev/null || echo "$PERM_TEST" | grep -v "HTTP_STATUS"
else
    echo "⚠️ Could not check permissions (Status: $HTTP_STATUS)"
    echo "Response: $PERM_TEST"
fi
echo ""

# Test 4: Try to create a test issue
echo "Test 4: Attempting to create a test issue..."
ISSUE_PAYLOAD='{
  "fields": {
    "project": {
      "key": "'$PROJECT_KEY'"
    },
    "summary": "Test Issue - API Connection Test",
    "description": "This is a test issue to verify API connection and permissions.",
    "issuetype": {
      "name": "Task"
    }
  }
}'

CREATE_TEST=$(curl -s -u "$API_EMAIL:$API_TOKEN" \
    -X POST \
    -H "Content-Type: application/json" \
    -d "$ISSUE_PAYLOAD" \
    "$BASE_URL/rest/api/3/issue" \
    -w "\nHTTP_STATUS:%{http_code}")

HTTP_STATUS=$(echo "$CREATE_TEST" | grep "HTTP_STATUS" | cut -d':' -f2)
if [ "$HTTP_STATUS" = "201" ]; then
    ISSUE_KEY=$(echo "$CREATE_TEST" | grep -v "HTTP_STATUS" | jq -r '.key' 2>/dev/null)
    echo "✅ Test issue created successfully: $ISSUE_KEY"
    echo "Issue URL: $BASE_URL/browse/$ISSUE_KEY"
    echo ""
    echo "You can delete this test issue from Jira if needed."
else
    echo "❌ Failed to create test issue (Status: $HTTP_STATUS)"
    echo "Response:"
    echo "$CREATE_TEST" | grep -v "HTTP_STATUS" | jq '.' 2>/dev/null || echo "$CREATE_TEST" | grep -v "HTTP_STATUS"
    echo ""
    echo "Troubleshooting:"
    echo "1. Check if 'Task' issue type exists in project '$PROJECT_KEY'"
    echo "2. Verify user '$API_EMAIL' has 'Create Issues' permission"
    echo "3. Try a different issue type (Bug, Story, etc.)"
fi

echo ""
echo "=== Test Complete ==="

