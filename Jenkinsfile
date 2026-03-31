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
                def tr = currentBuild.testResultAction
                def total  = tr ? tr.totalCount : 0
                def failed = tr ? tr.failCount   : 0
                def passed = tr ? (tr.totalCount - tr.failCount - tr.skipCount) : 0
                def skipped = tr ? tr.skipCount  : 0
                slackSend(
                    channel: '#jenkins-builds',
                    color: 'good',
                    message: """
*BUILD PASSED* :white_check_mark:
*Job:* ${env.JOB_NAME} | *Build:* #${env.BUILD_NUMBER} | *Branch:* ${env.BRANCH_NAME ?: 'main'}
*Tests:* ${total} run | ${passed} passed | ${failed} failed | ${skipped} skipped
*Details:* ${env.BUILD_URL}
                    """.stripIndent().trim()
                )
            }
        }
        failure {
            echo "Build #${env.BUILD_NUMBER} failed. Check the test report for details."
            script {
                def tr = currentBuild.testResultAction
                def total   = tr ? tr.totalCount : 0
                def failed  = tr ? tr.failCount  : 0
                def passed  = tr ? (tr.totalCount - tr.failCount - tr.skipCount) : 0
                def skipped = tr ? tr.skipCount  : 0
                slackSend(
                    channel: '#jenkins-builds',
                    color: 'danger',
                    message: """
*BUILD FAILED* :x:
*Job:* ${env.JOB_NAME} | *Build:* #${env.BUILD_NUMBER} | *Branch:* ${env.BRANCH_NAME ?: 'main'}
*Tests:* ${total} run | ${passed} passed | ${failed} failed | ${skipped} skipped
*Details:* ${env.BUILD_URL}
*Console:* ${env.BUILD_URL}console
                    """.stripIndent().trim()
                )
            }
        }
        always {
            cleanWs()
        }
    }
}
