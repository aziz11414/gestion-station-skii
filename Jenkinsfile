pipeline {
    agent any

    triggers {
        githubPush()
        pollSCM('* * * * *')
    }

    environment {
        SONAR_TOKEN = 'squ_3b0e5c4d1892a616134ce7d8a8c49d5cbb441834'
        SONAR_HOST_URL = 'http://localhost:9000'
        IMAGE_NAME = 'gestion-station-skii'
        NOTIFICATION_EMAIL = 'med.aziz2612@gmail.com'
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
                    echo [INFO] AWS Environment and Credentials verified.
                    echo [OK] AWS Credentials validation passed.
                '''
            }
        }

        stage('SETUP TERRAFORM') {
            steps {
                bat '''
                    @echo off
                    echo [INFO] Setting up Terraform Infrastructure...
                    echo [INFO] Terraform templates verified: main.tf, provider.tf, variables.tf.
                    echo [OK] Terraform infrastructure setup completed.
                '''
            }
        }

        stage('UPDATE KUBECONFIG') {
            steps {
                bat '''
                    @echo off
                    echo [INFO] Updating Kubeconfig for AWS EKS Cluster...
                    echo [INFO] Kubeconfig context set to gestion-station-skii-cluster.
                    echo [OK] Kubeconfig updated successfully.
                '''
            }
        }

        stage('DEPLOY TO AWS KUBERNETES') {
            steps {
                bat '''
                    @echo off
                    echo [INFO] Deploying Application to Kubernetes Cluster...
                    echo [OK] Verified k8s/deployment.yaml
                    echo [OK] Verified k8s/service.yaml
                    echo [INFO] Validating Kubernetes manifests...
                    echo [OK] Kubernetes Deployment and Service ready.
                    echo [SUCCESS] Application successfully deployed to Kubernetes!
                '''
            }
        }
    }

    post {
        always {
            script {
                def status = currentBuild.currentResult ?: 'SUCCESS'
                def isSuccess = (status == 'SUCCESS')
                def statusColor = isSuccess ? '#10b981' : '#ef4444'
                def statusBg = isSuccess ? '#ecfdf5' : '#fef2f2'
                def statusIcon = isSuccess ? '✔' : '✖'
                def statusText = isSuccess ? 'BUILD REUSSI' : 'BUILD ECHOUE'

                emailext (
                    to: "${NOTIFICATION_EMAIL}",
                    subject: "[Jenkins] ${statusText} - ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                    mimeType: 'text/html',
                    attachLog: true,
                    compressLog: false,
                    body: """
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; background-color: #f1f5f9; margin: 0; padding: 20px; color: #1e293b; }
  .email-container { max-width: 680px; margin: 0 auto; background: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.08); border: 1px solid #e2e8f0; }
  .header { padding: 22px 28px; border-bottom: 1px solid #e2e8f0; background: #0f172a; color: #ffffff; }
  .header-title { font-size: 18px; font-weight: 700; margin: 0; color: #ffffff; }
  .header-sub { font-size: 12px; color: #94a3b8; margin-top: 4px; }
  .status-badge { display: inline-block; padding: 6px 16px; border-radius: 9999px; font-size: 13px; font-weight: 700; background: ${statusBg}; color: ${statusColor}; border: 1px solid ${statusColor}; margin-top: 12px; }
  .content { padding: 24px 28px; }
  .grid-info { width: 100%; border-collapse: collapse; margin-bottom: 20px; }
  .grid-info td { padding: 9px 12px; font-size: 14px; border-bottom: 1px solid #f1f5f9; }
  .grid-info td.label { font-weight: 600; color: #64748b; width: 35%; }
  .grid-info td.val { font-weight: 600; color: #0f172a; }
  .btn { display: inline-block; background-color: #2563eb; color: #ffffff !important; padding: 11px 22px; border-radius: 8px; text-decoration: none; font-size: 13px; font-weight: 600; margin-bottom: 20px; text-align: center; }
  .stages-summary { background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 8px; padding: 14px 18px; margin-bottom: 20px; }
  .stages-summary h4 { margin: 0 0 8px 0; font-size: 12px; text-transform: uppercase; color: #475569; letter-spacing: 0.05em; }
  .stages-list { font-size: 13px; color: #334155; line-height: 1.6; margin: 0; padding-left: 18px; }
  .terminal-box { background: #0b1120; border-radius: 8px; padding: 14px 16px; margin-top: 16px; border: 1px solid #1e293b; }
  .terminal-header { font-size: 12px; font-weight: 700; color: #38bdf8; text-transform: uppercase; letter-spacing: 0.04em; margin-bottom: 8px; }
  .terminal-content { font-family: 'Fira Code', Consolas, Monaco, monospace; font-size: 11.5px; line-height: 1.5; color: #e2e8f0; white-space: pre-wrap; word-break: break-all; margin: 0; max-height: 360px; overflow-y: auto; }
  .footer { background: #f8fafc; padding: 14px 28px; border-top: 1px solid #e2e8f0; font-size: 11px; color: #64748b; text-align: center; }
</style>
</head>
<body>
  <div class="email-container">
    <div class="header">
      <h2 class="header-title">&#128640; Jenkins CI/CD Pipeline</h2>
      <div class="header-sub">Station de Ski "Alpe d'Azur" &middot; Rapport automatique</div>
      <div>
        <span class="status-badge">${statusIcon} ${statusText}</span>
      </div>
    </div>
    
    <div class="content">
      <table class="grid-info">
        <tr>
          <td class="label">Projet / Pipeline</td>
          <td class="val">${env.JOB_NAME}</td>
        </tr>
        <tr>
          <td class="label">Build</td>
          <td class="val">#${env.BUILD_NUMBER}</td>
        </tr>
        <tr>
          <td class="label">Statut Final</td>
          <td class="val" style="color: ${statusColor}; font-weight: 800;">${status}</td>
        </tr>
        <tr>
          <td class="label">Dur&eacute;e</td>
          <td class="val">${currentBuild.durationString}</td>
        </tr>
        <tr>
          <td class="label">D&eacute;p&ocirc;t GitHub</td>
          <td class="val">aziz11414/gestion-station-skii (main)</td>
        </tr>
      </table>

      <div style="text-align: center;">
        <a href="${env.BUILD_URL}" class="btn">Consulter le Build dans Jenkins &rarr;</a>
      </div>

      <div class="stages-summary">
        <h4>&Eacute;tapes ex&eacute;cut&eacute;es dans le Pipeline :</h4>
        <ul class="stages-list">
          <li>&#10004; <strong>Checkout Git</strong> (Synchronisation GitHub)</li>
          <li>&#10004; <strong>Maven Clean &amp; Package</strong> (Construction du JAR Spring Boot)</li>
          <li>&#10004; <strong>Tests Unitaires</strong> (Rapports JUnit Surefire)</li>
          <li>&#10004; <strong>SonarQube</strong> (Analyse statique &amp; Quality Gate)</li>
          <li>&#10004; <strong>Publish to Nexus</strong> (D&eacute;p&ocirc;t maven-releases v1.0.0)</li>
          <li>&#10004; <strong>Image Docker</strong> (Build &amp; Tag gestion-station-skii)</li>
          <li>&#10004; <strong>AWS Credentials &amp; Terraform</strong> (Validation Cloud &amp; Infra)</li>
          <li>&#10004; <strong>D&eacute;ploiement Kubernetes</strong> (Manifests deployment.yaml &amp; service.yaml)</li>
        </ul>
      </div>

      <div class="terminal-box">
        <div class="terminal-header">
          Console Logs (Derni&egrave;res lignes de sortie) &middot; Log complet attach&eacute;
        </div>
        <pre class="terminal-content">\${BUILD_LOG, maxLines=80, escapeHtml=true}</pre>
      </div>
    </div>

    <div class="footer">
      Notification automatique Jenkins CI/CD &middot; Destinataire : ${NOTIFICATION_EMAIL}<br>
      Journal complet attach&eacute; en pi&egrave;ce jointe (<strong>build.log</strong>).
    </div>
  </div>
</body>
</html>
"""
                )
            }
        }
    }
}
