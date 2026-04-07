pipeline {
    agent any

    tools {
        maven 'Maven-3.9'
        jdk   'JDK-11'
    }

    options {
        // Keep only the last 10 builds
        buildDiscarder(logRotator(numToKeepStr: '10'))
        // Fail the build if it runs longer than 15 minutes
        timeout(time: 15, unit: 'MINUTES')
        // Add timestamps to console output
        timestamps()
    }

    triggers {
        // Trigger pipeline automatically on GitHub push via webhook
        githubPush()
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
                echo "Checked out branch: ${env.BRANCH_NAME}"
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean compile -q'
            }
        }

        stage('Run API Tests') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    // Archive raw Surefire XML results
                    junit testResults: 'target/surefire-reports/*.xml',
                          allowEmptyResults: false
                    // Stash reports before workspace is cleaned
                    stash name: 'surefire-reports', includes: 'target/surefire-reports/TEST-*.xml', allowEmpty: true
                }
            }
        }

        stage('Archive Artifacts') {
            steps {
                archiveArtifacts artifacts: 'target/surefire-reports/**',
                                 allowEmptyArchive: false
            }
        }

        stage('Publish Report') {
            steps {
                // Generate the HTML Surefire report
                sh 'mvn surefire-report:report site:site -DgenerateReports=false -q'
                archiveArtifacts artifacts: 'target/site/surefire-report.html',
                                 allowEmptyArchive: true
            }
        }
    }

    post {
        success {
            echo "All tests passed. Build #${env.BUILD_NUMBER} succeeded."
            script {
                unstash 'surefire-reports'
                def total   = sh(script: "grep -h 'testsuite ' target/surefire-reports/TEST-*.xml | sed 's/.*tests=\"//;s/\".*//' | awk '{s+=\$1} END {print s+0}'", returnStdout: true).trim()
                def failed  = sh(script: "grep -h 'testsuite ' target/surefire-reports/TEST-*.xml | sed 's/.*failures=\"//;s/\".*//' | awk '{s+=\$1} END {print s+0}'", returnStdout: true).trim()
                def skipped = sh(script: "grep -h 'testsuite ' target/surefire-reports/TEST-*.xml | sed 's/.*skipped=\"//;s/\".*//' | awk '{s+=\$1} END {print s+0}'", returnStdout: true).trim()
                def passed  = (total.toInteger() - failed.toInteger() - skipped.toInteger()).toString()
                slackSend(
                    channel: '#jenkins-builds',
                    color: 'good',
                    message: "*BUILD PASSED* :white_check_mark:\n*Job:* ${env.JOB_NAME} | *Build:* #${env.BUILD_NUMBER} | *Branch:* ${env.BRANCH_NAME ?: 'main'}\n*Tests:* ${total} run | ${passed} passed | ${failed} failed | ${skipped} skipped\n*Details:* ${env.BUILD_URL}"
                )
            }
        }
        failure {
            echo "Build #${env.BUILD_NUMBER} failed. Check the test report for details."
            script {
                try { unstash 'surefire-reports' } catch (e) { echo 'No stash found' }
                def total   = sh(script: "grep -h 'testsuite ' target/surefire-reports/TEST-*.xml 2>/dev/null | sed 's/.*tests=\"//;s/\".*//' | awk '{s+=\$1} END {print s+0}'", returnStdout: true).trim()
                def failed  = sh(script: "grep -h 'testsuite ' target/surefire-reports/TEST-*.xml 2>/dev/null | sed 's/.*failures=\"//;s/\".*//' | awk '{s+=\$1} END {print s+0}'", returnStdout: true).trim()
                def skipped = sh(script: "grep -h 'testsuite ' target/surefire-reports/TEST-*.xml 2>/dev/null | sed 's/.*skipped=\"//;s/\".*//' | awk '{s+=\$1} END {print s+0}'", returnStdout: true).trim()
                def passed  = (total.toInteger() - failed.toInteger() - skipped.toInteger()).toString()
                slackSend(
                    channel: '#jenkins-builds',
                    color: 'danger',
                    message: "*BUILD FAILED* :x:\n*Job:* ${env.JOB_NAME} | *Build:* #${env.BUILD_NUMBER} | *Branch:* ${env.BRANCH_NAME ?: 'main'}\n*Tests:* ${total} run | ${passed} passed | ${failed} failed | ${skipped} skipped\n*Details:* ${env.BUILD_URL}\n*Console:* ${env.BUILD_URL}console"
                )
            }
        }
        always {
            cleanWs()
        }
    }
}
