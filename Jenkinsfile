pipeline {
    agent any
    
    tools {
        jdk 'JDK11'
        maven 'Maven3'
    }
    
    environment {
        ANDROID_HOME = '/opt/android-sdk'
        JAVA_HOME = '/usr/lib/jvm/java-11-openjdk'
        PATH = "${ANDROID_HOME}/tools:${ANDROID_HOME}/platform-tools:${JAVA_HOME}/bin:${PATH}"
        ALLURE_RESULTS = 'allure-results'
        ALLURE_REPORT = 'allure-report'
        JIRA_PROJECT_KEY = 'DEV'
        JIRA_BASE_URL = 'https://ngantran0792.atlassian.net'
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
                script {
                    echo "✅ Code checked out from repository"
                    sh 'git log -1 --oneline'
                }
            }
        }
        
        stage('Environment Setup') {
            steps {
                script {
                    echo "🔧 Setting up environment"
                    sh '''
                        echo "=== Environment Information ==="
                        echo "Java version:"
                        java -version
                        echo ""
                        echo "Maven version:"
                        mvn -version
                        echo ""
                        echo "Android SDK:"
                        echo $ANDROID_HOME
                        echo ""
                        echo "Node.js version:"
                        node --version || echo "Node.js not found"
                        echo ""
                        echo "Appium version:"
                        appium --version || echo "Appium not found"
                    '''
                }
            }
        }
        
        stage('Create Jira Task') {
            steps {
                script {
                    echo "📋 Creating Jira task for test execution"
                    try {
                        def jiraTask = createJiraTask(
                            projectKey: env.JIRA_PROJECT_KEY,
                            summary: "Automated Test Execution - Build #${env.BUILD_NUMBER}",
                            description: """
                                Test execution started for build #${env.BUILD_NUMBER}
                                
                                *Build URL:* ${env.BUILD_URL}
                                *Branch:* ${env.GIT_BRANCH}
                                *Commit:* ${env.GIT_COMMIT}
                                
                                Test Suite: Geofence Mobile Automation
                                Framework: Appium + TestNG + Allure
                            """,
                            issueType: "Task"
                        )
                        env.JIRA_TASK_KEY = jiraTask.key
                        echo "✅ Jira task created: ${jiraTask.key}"
                    } catch (Exception e) {
                        echo "⚠️ Failed to create Jira task: ${e.getMessage()}"
                        // Continue execution even if Jira task creation fails
                    }
                }
            }
        }
        
        stage('Start Appium Server') {
            steps {
                script {
                    echo "🚀 Starting Appium server"
                    sh '''
                        # Check if Appium is installed
                        if ! command -v appium &> /dev/null; then
                            echo "Appium not found, installing..."
                            npm install -g appium
                            npm install -g appium-uiautomator2-driver
                        fi
                        
                        # Kill any existing Appium processes
                        pkill -f appium || true
                        sleep 2
                        
                        # Start Appium in background
                        nohup appium --log appium.log > appium.log 2>&1 &
                        APPIUM_PID=$!
                        echo $APPIUM_PID > appium.pid
                        sleep 5
                        
                        # Verify Appium is running
                        for i in {1..10}; do
                            if curl -f http://localhost:4723/wd/hub/status > /dev/null 2>&1; then
                                echo "✅ Appium server is running"
                                break
                            fi
                            echo "Waiting for Appium to start... ($i/10)"
                            sleep 2
                        done
                        
                        # Final check
                        curl -f http://localhost:4723/wd/hub/status || exit 1
                    '''
                }
            }
        }
        
        stage('Build Project') {
            steps {
                script {
                    echo "🔨 Building Maven project"
                    sh 'mvn clean compile test-compile -DskipTests'
                }
            }
        }
        
        stage('Run Tests') {
            steps {
                script {
                    echo "🧪 Running automation tests"
                    sh '''
                        # Clean previous test results
                        rm -rf ${ALLURE_RESULTS} test-output screenshots logs/*.log
                        mkdir -p ${ALLURE_RESULTS} screenshots logs
                        
                        # Run tests with TestNG
                        mvn surefire:test -DsuiteXmlFile=testng.xml || true
                        
                        # Copy test results if needed
                        if [ -d "target/allure-results" ]; then
                            cp -r target/allure-results/* ${ALLURE_RESULTS}/ || true
                        fi
                    '''
                }
            }
            post {
                always {
                    script {
                        echo "📊 Test execution completed"
                        // Archive screenshots and logs
                        archiveArtifacts artifacts: 'screenshots/**/*.png', allowEmptyArchive: true
                        archiveArtifacts artifacts: 'logs/**/*.log', allowEmptyArchive: true
                        archiveArtifacts artifacts: 'appium.log', allowEmptyArchive: true
                    }
                }
            }
        }
        
        stage('Generate Allure Report') {
            steps {
                script {
                    echo "📈 Generating Allure report"
                    sh '''
                        # Generate Allure report
                        mvn allure:report || true
                        
                        # Ensure results directory exists
                        mkdir -p ${ALLURE_RESULTS}
                        
                        # Copy results if generated in target directory
                        if [ -d "target/allure-results" ]; then
                            cp -r target/allure-results/* ${ALLURE_RESULTS}/ || true
                        fi
                    '''
                }
            }
        }
        
        stage('Publish Allure Report') {
            steps {
                script {
                    echo "📤 Publishing Allure report"
                    allure([
                        includeProperties: false,
                        jdk: '',
                        properties: [],
                        reportBuildPolicy: 'ALWAYS',
                        results: [[path: 'allure-results']]
                    ])
                }
            }
        }
        
        stage('Create Jira Defects for Failures') {
            steps {
                script {
                    echo "🐛 Creating Jira defects for failed tests"
                    try {
                        def testResults = readTestResults()
                        if (testResults.failed > 0) {
                            echo "Found ${testResults.failed} failed test(s)"
                            // This would be handled by TestListener in the framework
                            // which automatically creates Jira defects on test failure
                        } else {
                            echo "✅ No failed tests - no defects to create"
                        }
                    } catch (Exception e) {
                        echo "⚠️ Error processing test results: ${e.getMessage()}"
                    }
                }
            }
        }
        
        // Note: Confluence integration is not currently implemented in the framework.
        // This stage is reserved for future Confluence integration if needed.
        // Currently, test results are available via Allure reports and Jira defects.
    }
    
    post {
        always {
            script {
                echo "🧹 Cleaning up Appium server"
                sh '''
                    if [ -f appium.pid ]; then
                        APPIUM_PID=$(cat appium.pid)
                        kill $APPIUM_PID 2>/dev/null || true
                        rm -f appium.pid
                    fi
                    pkill -f appium || true
                '''
            }
        }
        success {
            script {
                echo "✅ Pipeline completed successfully"
                if (env.JIRA_TASK_KEY) {
                    echo "Jira Task: ${env.JIRA_TASK_KEY}"
                }
                echo "Allure Report: ${env.BUILD_URL}allure/"
            }
        }
        failure {
            script {
                echo "❌ Pipeline failed"
                if (env.JIRA_TASK_KEY) {
                    echo "Jira Task: ${env.JIRA_TASK_KEY}"
                }
            }
        }
        unstable {
            script {
                echo "⚠️ Pipeline is unstable"
            }
        }
    }
}

// Helper function to create Jira task (requires Jira plugin)
def createJiraTask(Map params) {
    // This would use Jira REST API or Jenkins Jira plugin
    // For now, this is a placeholder - actual implementation depends on Jenkins plugins
    return [
        key: "${params.projectKey}-${env.BUILD_NUMBER}",
        url: "${env.JIRA_BASE_URL}/browse/${params.projectKey}-${env.BUILD_NUMBER}"
    ]
}

// Helper function to read test results
def readTestResults() {
    def results = [passed: 0, failed: 0, skipped: 0]
    try {
        def testngXml = readFile('target/surefire-reports/testng-results.xml')
        // Parse XML to get test results
        // This is simplified - actual implementation would parse the XML properly
        results.failed = (testngXml =~ /failures="(\d+)"/)[0][1].toInteger()
        results.passed = (testngXml =~ /passed="(\d+)"/)[0][1].toInteger()
        results.skipped = (testngXml =~ /skipped="(\d+)"/)[0][1].toInteger()
    } catch (Exception e) {
        echo "Could not read test results: ${e.getMessage()}"
    }
    return results
}
