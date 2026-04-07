// =============================================================================
// Jenkinsfile — Jenkins-labs Declarative CI/CD Pipeline
// =============================================================================
// Defines the full CI/CD pipeline for the REST Assured API test project.
// Triggered automatically via GitHub webhook on every push to main,
// or manually via "Build Now" in the Jenkins UI.
//
// Pipeline stages:
//   1. Checkout   — pulls latest code from GitHub
//   2. Build      — compiles the Maven project
//   3. Run API Tests — executes all JUnit/REST Assured tests
//   4. Archive Artifacts — saves Surefire XML reports
//   5. Publish Report — generates and archives the HTML test report
//
// Post-build actions:
//   - Sends a Slack notification with build status and test counts
//   - Cleans the workspace to free disk space
// =============================================================================

pipeline {
    // Run on any available Jenkins agent
    agent any

    // -------------------------------------------------------------------------
    // Tool configuration
    // -------------------------------------------------------------------------
    // References the Maven and JDK installations configured in:
    // Manage Jenkins → Tools → Maven/JDK installations
    // Names must match exactly what is configured there.
    // -------------------------------------------------------------------------
    tools {
        maven 'Maven-3.9'
        jdk   'JDK-11'
    }

    // -------------------------------------------------------------------------
    // Pipeline-level options
    // -------------------------------------------------------------------------
    options {
        // Retain only the last 10 builds to save disk space
        buildDiscarder(logRotator(numToKeepStr: '10'))

        // Abort the build if it runs longer than 15 minutes (prevents hung builds)
        timeout(time: 15, unit: 'MINUTES')

        // Prefix every console log line with a timestamp for easier debugging
        timestamps()
    }

    // -------------------------------------------------------------------------
    // Triggers
    // -------------------------------------------------------------------------
    triggers {
        // Automatically trigger this pipeline when GitHub sends a push webhook.
        // Requires "GitHub hook trigger for GITScm polling" enabled in job config.
        githubPush()
    }

    // =========================================================================
    // Stages
    // =========================================================================
    stages {

        // ---------------------------------------------------------------------
        // Stage 1: Checkout
        // ---------------------------------------------------------------------
        // Pulls the latest code from the GitHub repository branch that
        // triggered the build. Uses the SCM configuration defined in the job.
        // ---------------------------------------------------------------------
        stage('Checkout') {
            steps {
                checkout scm
                echo "Checked out branch: ${env.BRANCH_NAME}"
            }
        }

        // ---------------------------------------------------------------------
        // Stage 2: Build
        // ---------------------------------------------------------------------
        // Compiles all Java source files using Maven.
        // Fails fast here if there are any compilation errors before running tests.
        // The -q flag suppresses verbose Maven output for cleaner logs.
        // ---------------------------------------------------------------------
        stage('Build') {
            steps {
                sh 'mvn clean compile -q'
            }
        }

        // ---------------------------------------------------------------------
        // Stage 3: Run API Tests
        // ---------------------------------------------------------------------
        // Executes the full REST Assured test suite via Maven Surefire.
        // The post block always runs (even on failure) to:
        //   - Publish JUnit XML results so Jenkins shows a test trend graph
        //   - Stash the XML files before the workspace is cleaned, so the
        //     post-build Slack notification can read the test counts
        // ---------------------------------------------------------------------
        stage('Run API Tests') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    // Parse and publish JUnit XML results to Jenkins test dashboard
                    junit testResults: 'target/surefire-reports/*.xml',
                          allowEmptyResults: false

                    // Stash XML reports so they survive workspace cleanup
                    // and can be read in the global post block for Slack counts
                    stash name: 'surefire-reports', includes: 'target/surefire-reports/TEST-*.xml', allowEmpty: true
                }
            }
        }

        // ---------------------------------------------------------------------
        // Stage 4: Archive Artifacts
        // ---------------------------------------------------------------------
        // Saves the raw Surefire XML reports as downloadable build artifacts.
        // Useful for debugging failures or keeping an audit trail.
        // This stage is skipped automatically if the test stage fails.
        // ---------------------------------------------------------------------
        stage('Archive Artifacts') {
            steps {
                archiveArtifacts artifacts: 'target/surefire-reports/**',
                                 allowEmptyArchive: false
            }
        }

        // ---------------------------------------------------------------------
        // Stage 5: Publish Report
        // ---------------------------------------------------------------------
        // Generates a human-readable HTML test report using the Maven Surefire
        // report plugin, then archives it as a build artifact.
        // The site:site goal is required to render the HTML correctly.
        // ---------------------------------------------------------------------
        stage('Publish Report') {
            steps {
                sh 'mvn surefire-report:report site:site -DgenerateReports=false -q'
                archiveArtifacts artifacts: 'target/site/surefire-report.html',
                                 allowEmptyArchive: true
            }
        }
    }

    // =========================================================================
    // Post-build actions
    // =========================================================================
    post {

        success {
            echo "All tests passed. Build #${env.BUILD_NUMBER} succeeded."
            script {
                unstash 'surefire-reports'

                def total   = sh(script: "grep -h 'testsuite ' target/surefire-reports/TEST-*.xml | sed 's/.*tests=\"//;s/\".*//' | awk '{s+=\$1} END {print s+0}'", returnStdout: true).trim()
                def failed  = sh(script: "grep -h 'testsuite ' target/surefire-reports/TEST-*.xml | sed 's/.*failures=\"//;s/\".*//' | awk '{s+=\$1} END {print s+0}'", returnStdout: true).trim()
                def skipped = sh(script: "grep -h 'testsuite ' target/surefire-reports/TEST-*.xml | sed 's/.*skipped=\"//;s/\".*//' | awk '{s+=\$1} END {print s+0}'", returnStdout: true).trim()
                def passed  = (total.toInteger() - failed.toInteger() - skipped.toInteger()).toString()

                // Calculate build duration in seconds
                def duration = currentBuild.durationString.replace(' and counting', '')

                // Get the commit message and author from git log
                def commitMsg    = sh(script: "git log -1 --pretty=%s 2>/dev/null || echo 'N/A'", returnStdout: true).trim()
                def commitAuthor = sh(script: "git log -1 --pretty=%an 2>/dev/null || echo 'N/A'", returnStdout: true).trim()
                def commitHash   = sh(script: "git log -1 --pretty=%h 2>/dev/null || echo 'N/A'", returnStdout: true).trim()

                slackSend(
                    channel: '#jenkins-builds',
                    color: 'good',
                    message: """
:white_check_mark: *BUILD PASSED* :white_check_mark:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
*Job:*      ${env.JOB_NAME}
*Build:*    #${env.BUILD_NUMBER}
*Branch:*   ${env.BRANCH_NAME ?: 'main'}
*Duration:* ${duration}
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
:bar_chart: *Test Results*
• Total:   ${total}
• Passed:  ${passed} :large_green_circle:
• Failed:  ${failed} :red_circle:
• Skipped: ${skipped} :white_circle:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
:git: *Commit*
• Author:  ${commitAuthor}
• Message: ${commitMsg}
• Hash:    \`${commitHash}\`
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
:link: *Links*
• <${env.BUILD_URL}|Build #${env.BUILD_NUMBER}>
• <${env.BUILD_URL}testReport|Test Report>
• <${env.BUILD_URL}console|Console Output>
                    """.stripIndent().trim()
                )
            }
        }

        failure {
            echo "Build #${env.BUILD_NUMBER} failed."
            script {
                try { unstash 'surefire-reports' } catch (e) { echo 'No stash found' }

                def total   = sh(script: "grep -h 'testsuite ' target/surefire-reports/TEST-*.xml 2>/dev/null | sed 's/.*tests=\"//;s/\".*//' | awk '{s+=\$1} END {print s+0}'", returnStdout: true).trim()
                def failed  = sh(script: "grep -h 'testsuite ' target/surefire-reports/TEST-*.xml 2>/dev/null | sed 's/.*failures=\"//;s/\".*//' | awk '{s+=\$1} END {print s+0}'", returnStdout: true).trim()
                def skipped = sh(script: "grep -h 'testsuite ' target/surefire-reports/TEST-*.xml 2>/dev/null | sed 's/.*skipped=\"//;s/\".*//' | awk '{s+=\$1} END {print s+0}'", returnStdout: true).trim()
                def passed  = (total.toInteger() - failed.toInteger() - skipped.toInteger()).toString()

                def duration     = currentBuild.durationString.replace(' and counting', '')
                def commitMsg    = sh(script: "git log -1 --pretty=%s 2>/dev/null || echo 'N/A'", returnStdout: true).trim()
                def commitAuthor = sh(script: "git log -1 --pretty=%an 2>/dev/null || echo 'N/A'", returnStdout: true).trim()
                def commitHash   = sh(script: "git log -1 --pretty=%h 2>/dev/null || echo 'N/A'", returnStdout: true).trim()

                // Extract names of failed tests from XML for quick visibility
                def failedTests = sh(
                    script: "grep -h 'testcase.*failure' target/surefire-reports/TEST-*.xml 2>/dev/null | sed 's/.*name=\"//;s/\".*//' | head -5 | sed 's/^/• /' || echo '• N/A'",
                    returnStdout: true
                ).trim()

                slackSend(
                    channel: '#jenkins-builds',
                    color: 'danger',
                    message: """
:x: *BUILD FAILED* :x:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
*Job:*      ${env.JOB_NAME}
*Build:*    #${env.BUILD_NUMBER}
*Branch:*   ${env.BRANCH_NAME ?: 'main'}
*Duration:* ${duration}
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
:bar_chart: *Test Results*
• Total:   ${total}
• Passed:  ${passed} :large_green_circle:
• Failed:  ${failed} :red_circle:
• Skipped: ${skipped} :white_circle:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
:warning: *Failed Tests* (up to 5)
${failedTests}
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
:git: *Commit*
• Author:  ${commitAuthor}
• Message: ${commitMsg}
• Hash:    \`${commitHash}\`
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
:link: *Links*
• <${env.BUILD_URL}|Build #${env.BUILD_NUMBER}>
• <${env.BUILD_URL}testReport|Test Report>
• <${env.BUILD_URL}console|Console Output>
                    """.stripIndent().trim()
                )
            }
        }

        always {
            cleanWs()
        }
    }
}
