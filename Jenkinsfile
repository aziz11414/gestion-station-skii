pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Compile') {
            steps {
                bat 'mvnw.cmd clean compile'
            }
        }

        stage('Unit & Mock Tests') {
            steps {
                bat 'mvnw.cmd test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                bat 'mvnw.cmd sonar:sonar -Dsonar.projectKey=gestion-station-skii -Dsonar.projectName=gestion-station-skii -Dsonar.host.url=http://localhost:9000 -Dsonar.token=squ_3b0e5c4d1892a616134ce7d8a8c49d5cbb441834 -Dsonar.login=squ_3b0e5c4d1892a616134ce7d8a8c49d5cbb441834'
            }
        }

        stage('Package JAR') {
            steps {
                bat 'mvnw.cmd package -DskipTests'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }
    }
}
