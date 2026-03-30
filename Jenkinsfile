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
                sh 'mvn surefire-report:report -q'
            }
            post {
                always {
                    publishHTML(target: [
                        allowMissing         : false,
                        alwaysLinkToLastBuild: true,
                        keepAll              : true,
                        reportDir            : 'target/site',
                        reportFiles          : 'surefire-report.html',
                        reportName           : 'API Test Report'
                    ])
                }
            }
        }
    }

    post {
        success {
            echo "All tests passed. Build #${env.BUILD_NUMBER} succeeded."
        }
        failure {
            echo "Build #${env.BUILD_NUMBER} failed. Check the test report for details."
        }
        always {
            // Clean workspace after build to save disk space
            cleanWs()
        }
    }
}
