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
                def total = 0, failed = 0, skipped = 0
                try {
                    def files = findFiles(glob: 'target/surefire-reports/TEST-*.xml')
                    files.each { f ->
                        def xml = readFile(f.path)
                        def matcher = xml =~ /tests="(\d+)"/
                        if (matcher) total += matcher[0][1].toInteger()
                        matcher = xml =~ /failures="(\d+)"/
                        if (matcher) failed += matcher[0][1].toInteger()
                        matcher = xml =~ /skipped="(\d+)"/
                        if (matcher) skipped += matcher[0][1].toInteger()
                    }
                } catch (e) { echo "Could not parse test results: ${e.message}" }
                def passed = total - failed - skipped
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
                def total = 0, failed = 0, skipped = 0
                try {
                    def files = findFiles(glob: 'target/surefire-reports/TEST-*.xml')
                    files.each { f ->
                        def xml = readFile(f.path)
                        def matcher = xml =~ /tests="(\d+)"/
                        if (matcher) total += matcher[0][1].toInteger()
                        matcher = xml =~ /failures="(\d+)"/
                        if (matcher) failed += matcher[0][1].toInteger()
                        matcher = xml =~ /skipped="(\d+)"/
                        if (matcher) skipped += matcher[0][1].toInteger()
                    }
                } catch (e) { echo "Could not parse test results: ${e.message}" }
                def passed = total - failed - skipped
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
