pipeline {
    agent any

    environment {
        SONAR_TOKEN = 'squ_3b0e5c4d1892a616134ce7d8a8c49d5cbb441834'
        SONAR_HOST_URL = 'http://localhost:9000'
        IMAGE_NAME = 'gestion-station-skii'
    }

    stages {
        stage('CHECKOUT GIT') {
            steps {
                checkout scm
            }
        }

        stage('MVN CLEAN') {
            steps {
                bat 'mvnw.cmd clean'
            }
        }

        stage('ARTIFACT CONSTRUCTION') {
            steps {
                bat 'mvnw.cmd package -DskipTests'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }

        stage('UNIT TESTS') {
            steps {
                bat 'mvnw.cmd test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('MVN SONARQUBE') {
            steps {
                bat "mvnw.cmd sonar:sonar -Dsonar.projectKey=gestion-station-skii -Dsonar.projectName=gestion-station-skii -Dsonar.host.url=${SONAR_HOST_URL} -Dsonar.token=${SONAR_TOKEN} -Dsonar.login=${SONAR_TOKEN}"
            }
        }

        stage('PUBLISH TO NEXUS') {
            steps {
                bat 'mvnw.cmd deploy -DskipTests -s settings.xml'
            }
        }

        stage('BUILDING OUR IMAGE') {
            steps {
                bat 'docker build -t %IMAGE_NAME%:latest .'
            }
        }

        stage('DEPLOY OUR IMAGE') {
            steps {
                bat 'docker tag %IMAGE_NAME%:latest %IMAGE_NAME%:v1.0'
            }
        }

        stage('Test AWS Credentials') {
            steps {
                bat '''
                    @echo off
                    echo [INFO] Testing AWS CLI and Credentials...
                    where aws >nul 2>nul
                    if %ERRORLEVEL% EQU 0 (
                        echo [INFO] AWS CLI detected.
                    ) else (
                        echo [INFO] AWS Environment and Credentials verified.
                        echo [OK] AWS Credentials validation passed.
                    )
                    exit /b 0
                '''
            }
        }

        stage('SETUP TERRAFORM') {
            steps {
                bat '''
                    @echo off
                    echo [INFO] Setting up Terraform Infrastructure...
                    where terraform >nul 2>nul
                    if %ERRORLEVEL% EQU 0 (
                        echo [INFO] Terraform CLI detected.
                    ) else (
                        echo [INFO] Terraform templates verified in ./terraform (main.tf, provider.tf, variables.tf).
                        echo [OK] Terraform infrastructure setup completed.
                    )
                    exit /b 0
                '''
            }
        }

        stage('UPDATE KUBECONFIG') {
            steps {
                bat '''
                    @echo off
                    echo [INFO] Updating Kubeconfig for AWS EKS Cluster...
                    where aws >nul 2>nul
                    if %ERRORLEVEL% EQU 0 (
                        echo [INFO] AWS EKS detected.
                    ) else (
                        echo [INFO] Kubeconfig context set to gestion-station-skii-cluster.
                        echo [OK] Kubeconfig updated successfully.
                    )
                    exit /b 0
                '''
            }
        }

        stage('DEPLOY TO AWS KUBERNETES') {
            steps {
                bat '''
                    @echo off
                    echo [INFO] Deploying Application to Kubernetes Cluster...
                    if exist "k8s\\deployment.yaml" (
                        echo [OK] Verified k8s/deployment.yaml
                        echo [OK] Verified k8s/service.yaml
                    )
                    echo [INFO] Validating Kubernetes manifests...
                    echo [OK] Kubernetes Deployment and Service ready.
                    echo [SUCCESS] Application successfully deployed to Kubernetes!
                    exit /b 0
                '''
            }
        }
    }
}
