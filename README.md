# 🎿 Gestion Station de Ski — Backend REST & Pipeline CI/CD DevOps

[![OpenJDK](https://img.shields.io/badge/OpenJDK-Java_17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-2.7.5-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Jenkins](https://img.shields.io/badge/Jenkins-Automation-D24939?style=for-the-badge&logo=jenkins&logoColor=white)](https://www.jenkins.io/)
[![SonarQube](https://img.shields.io/badge/SonarQube-Community-4E9BCD?style=for-the-badge&logo=sonarqube&logoColor=white)](https://www.sonarqube.org/)
[![Kubernetes](https://img.shields.io/badge/Kubernetes-CNCF-326CE5?style=for-the-badge&logo=kubernetes&logoColor=white)](https://kubernetes.io/)
[![Prometheus](https://img.shields.io/badge/Prometheus-Monitoring-E6522C?style=for-the-badge&logo=prometheus&logoColor=white)](https://prometheus.io/)
[![Grafana](https://img.shields.io/badge/Grafana-Dashboards-F46800?style=for-the-badge&logo=grafana&logoColor=white)](https://grafana.com/)

---

## 📌 Présentation du Projet

**Gestion Station de Ski** est une application d'entreprise développée avec **Spring Boot 2.7.5** et **Java 17**. Elle expose une API REST complète pour orchestrer l'ensemble des activités d'un domaine skiable : la gestion des skieurs, des moniteurs, des cours, des inscriptions, des abonnements et des pistes.

Le projet intègre une **chaîne industrielle DevOps complète** et automatisée : intégration continue (CI), analyse statique de code et couverture de tests, déploiement d'artefacts binaires sur Nexus, conteneurisation Docker, provisionnement d'infrastructure as Code (IaC) avec Terraform sur AWS, orchestration de conteneurs avec Kubernetes, et supervision en temps réel avec Prometheus et Grafana.

---

## 📑 Sommaire

1. [Architecture & Technologies](#-architecture--technologies)
2. [Modèle Métier & API REST](#-modèle-métier--api-rest)
3. [Documentation OpenAPI & Swagger](#-documentation-openapi--swagger)
4. [Pipeline CI/CD Jenkins (12 Étapes)](#-pipeline-cicd-jenkins-12-étapes)
5. [Conteneurisation & Déploiement Kubernetes](#-conteneurisation--déploiement-kubernetes)
6. [Infrastructure as Code (Terraform) & Cloud AWS](#-infrastructure-as-code-terraform--cloud-aws)
7. [Observabilité & Monitoring (Prometheus & Grafana)](#-observabilité--monitoring-prometheus--grafana)
8. [Guide de Démarrage Rapide](#-guide-de-démarrage-rapide)
9. [Structure du Dépôt](#-structure-du-dépôt)
10. [Auteur](#-auteur)

---

## 🏗 Architecture & Technologies

### Backend Applicatif
* **Langage** : Java 17 / Java 8 compatibility
* **Framework** : Spring Boot 2.7.5
  * Spring Data JPA / Hibernate (ORM & Persistance)
  * Spring Web (API RESTful)
  * Spring Boot Actuator (Indicateurs d'état & métriques de santé)
  * Springdoc OpenAPI 1.6.12 (Documentation interactive Swagger)
* **Base de données** : MySQL 8.0 (Driver `mysql-connector-j`)
* **Tests & Qualité** : JUnit 5, Mockito, JaCoCo (Couverture de code)

### Écosystème DevOps & Cloud
* **Intégration Continue** : Jenkins (Pipeline Déclaratif as Code via `Jenkinsfile`)
* **Analyse de Qualité** : SonarQube (Quality Gate, détection des vulnérabilités, Code Smells et bugs)
* **Gestionnaire d'Artefacts** : Sonatype Nexus Repository Manager (Dépôts `maven-releases` et `maven-snapshots`)
* **Conteneurisation** : Docker & Dockerfile multi-stage
* **Infrastructure as Code** : Terraform (Providers AWS, HashiCorp)
* **Cloud & Orchestration** : AWS Academy Learner Lab (STS, IAM, VPC, EKS), Kubernetes (`k8s/deployment.yaml`, `k8s/service.yaml`)
* **Supervision & Observabilité** : Prometheus (Collecte des métriques Spring Boot & Jenkins) et Grafana (Dashboards JVM, HTTP et Pipelines)

---

## ⛷ Modèle Métier & API REST

L'application est structurée selon une architecture en couches standard (**Controller → Service → Repository → Entity**) :

| Entité | Description | Endpoints REST Principaux |
| :--- | :--- | :--- |
| **Skier** | Gestion des profils de skieurs et attribution d'abonnements | `/skier/add`, `/skier/all`, `/skier/get/{id}`, `/skier/addAndAssignToCourse` |
| **Instructor** | Moniteurs encadrant les sessions de ski | `/instructor/add`, `/instructor/all`, `/instructor/addAndAssignToCourse` |
| **Course** | Cours de ski (Collectifs, Individuels, Niveaux débutant à expert) | `/course/add`, `/course/all`, `/course/get/{id}` |
| **Registration** | Inscriptions des skieurs aux cours programmés | `/registration/addAndAssignToSkier`, `/registration/assignToCourse` |
| **Subscription** | Forfaits et abonnements (Mensuel, Saisonnier, Annuel) | `/subscription/all`, `/subscription/getByType` |
| **Piste** | Pistes du domaine skiable classées par couleur (Verte, Bleue, Rouge, Noire) | `/piste/add`, `/piste/all`, `/piste/get/{id}` |

---

## 📖 Documentation OpenAPI & Swagger

L'API est documentée selon la spécification OpenAPI 3. Une fois l'application démarrée, l'interface interactive Swagger UI est accessible sur :

```
http://localhost:8080/stationSki/swagger-ui/index.html
```

* **Spécification OpenAPI (JSON)** : `http://localhost:8080/stationSki/v3/api-docs`
* **Contexte Applicatif** : `/stationSki`

---

## 🚀 Pipeline CI/CD Jenkins (12 Étapes)

Le pipeline d'intégration et de livraison continue est entièrement automatisé via le `Jenkinsfile` présent à la racine du projet :

```
+--------------------------------------------------------------------------------------------------------------------+
|                                             JENKINS PIPELINE CI/CD (12 STAGES)                                     |
+--------------------------------------------------------------------------------------------------------------------+
|  1. CHECKOUT GIT          -> Récupération automatique du code source depuis GitHub                                  |
|  2. MVN CLEAN             -> Nettoyage de l'espace de travail Maven                                                |
|  3. ARTIFACT CONSTRUCTION -> Compilation et packaging du projet (JAR)                                             |
|  4. UNIT TESTS            -> Exécution des tests unitaires Mockito & JUnit (rapports surefire)                     |
|  5. MVN SONARQUBE         -> Analyse statique de code & passage du Quality Gate SonarQube                           |
|  6. PUBLISH TO NEXUS      -> Publication de l'artefact versionné sur Sonatype Nexus via settings.xml               |
|  7. BUILDING OUR IMAGE    -> Construction de l'image Docker (docker build)                                         |
|  8. DEPLOY OUR IMAGE      -> Tag et préparation de l'image Docker de version                                       |
|  9. Test AWS Credentials  -> Validation des identifiants et tokens AWS Academy (STS / IAM)                         |
| 10. SETUP TERRAFORM       -> Validation des templates d'infrastructure Terraform (main.tf, provider.tf)            |
| 11. UPDATE KUBECONFIG     -> Configuration du contexte Kubernetes pour le cluster AWS EKS                          |
| 12. DEPLOY TO KUBERNETES  -> Déploiement des manifests Kubernetes (k8s/deployment.yaml, k8s/service.yaml)             |
+--------------------------------------------------------------------------------------------------------------------+
```

### Configuration des Outils CI/CD :
* **SonarQube** : `http://localhost:9000` (Projet `gestion-station-skii`)
* **Sonatype Nexus** : `http://localhost:8081` (Dépôts Maven `maven-releases` et `maven-snapshots`)
* **Jenkins Controller** : `http://localhost:8090`

---

## 🐳 Conteneurisation & Déploiement Kubernetes

### Dockerfile
L'application est encapsulée dans une image Docker légère basée sur OpenJDK 17 :

```dockerfile
FROM openjdk:17-alpine
VOLUME /tmp
EXPOSE 8080
ADD target/gestion-station-skii-1.0.0.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
```

### Manifests Kubernetes (`k8s/`)
* **`k8s/deployment.yaml`** : Définit le déploiement du conteneur avec 2 réplicas, les sondes de disponibilité (`livenessProbe`, `readinessProbe`) et les limites de ressources.
* **`k8s/service.yaml`** : Expose l'application via un Service de type `NodePort` ou `LoadBalancer` routant vers le port interne `8080`.

---

## ☁️ Infrastructure as Code (Terraform) & Cloud AWS

Le dossier `terraform/` contient la configuration déclarative de l'infrastructure Cloud hébergée sur **Amazon Web Services (AWS)** :

* **`provider.tf`** : Déclaration du provider AWS et de la région cible (`us-east-1`).
* **`variables.tf`** : Paramétrage du cluster Kubernetes EKS, des tags et des sous-réseaux VPC.
* **`main.tf`** : Provisionnement des ressources AWS (VPC, Security Groups, Cluster AWS EKS et Node Groups).
* **Authentification** : Intégration sécurisée avec les sessions temporaires **AWS Academy Learner Lab** via `aws_access_key_id`, `aws_secret_access_key` et `aws_session_token`.

---

## 📊 Observabilité & Monitoring (Prometheus & Grafana)

Le dossier `monitoring/` contient l'infrastructure de surveillance prête à l'emploi via Docker Compose :

```
├── monitoring/
│   ├── docker-compose.yml
│   ├── prometheus.yml
│   └── grafana/
│       └── provisioning/
│           ├── datasources/
│           │   └── datasource.yml
│           └── dashboards/
│               └── dashboard.yml
```

* **Prometheus (`http://localhost:9090`)** : Scrape automatiquement :
  * Les métriques de performance de l'application Spring Boot via l'endpoint `/stationSki/actuator/prometheus`.
  * Les métriques du serveur d'intégration continue Jenkins via `/prometheus/`.
* **Grafana (`http://localhost:3000`)** : Tableaux de bord dynamiques visualisant le taux de requêtes, le temps de réponse HTTP, l'utilisation mémoire JVM (Heap/Non-Heap), les threads actifs et l'état des exécuteurs de build.

---

## 💻 Guide de Démarrage Rapide

### Prérequis
* **JDK 17** ou supérieur installé
* **Apache Maven 3.8+** (ou utilisation du wrapper `./mvnw`)
* **Docker & Docker Compose**
* **MySQL Server 8.0**

### 1. Cloner le Dépôt
```bash
git clone https://github.com/aziz11414/gestion-station-skii.git
cd gestion-station-skii
```

### 2. Configurer la Base de Données
Dans `src/main/resources/application.properties`, vérifiez vos paramètres de connexion MySQL :
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/station_ski?createDatabaseIfNotExist=true&useSSL=false
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update
```

### 3. Compiler et Exécuter les Tests
```bash
# Compilation et packaging
./mvnw clean package

# Exécution des tests unitaires
./mvnw test
```

### 4. Démarrer l'Application
```bash
./mvnw spring-boot:run
```
L'application est disponible sur : `http://localhost:8080/stationSki`

### 5. Lancer la Pile de Monitoring (Prometheus & Grafana)
```bash
cd monitoring
docker-compose up -d
```
* **Prometheus** : `http://localhost:9090`
* **Grafana** : `http://localhost:3000` *(Login par défaut : admin / admin)*

---

## 📁 Structure du Dépôt

```plaintext
gestion-station-skii/
├── .mvn/wrapper/                  # Fichiers du wrapper Maven
├── k8s/                           # Manifests Kubernetes (Deployment & Service)
│   ├── deployment.yaml
│   └── service.yaml
├── monitoring/                    # Stack de supervision & observabilité
│   ├── docker-compose.yml
│   ├── prometheus.yml
│   └── grafana/
├── src/                           # Code source Java et ressources Spring Boot
│   ├── main/
│   │   ├── java/com/example/gestionstationskii/
│   │   │   ├── controllers/       # Contrôleurs REST
│   │   │   ├── entities/          # Entités JPA du domaine skiable
│   │   │   ├── repositories/      # Interfaces Spring Data JPA
│   │   │   └── services/          # Logique métier & implémentations
│   │   └── resources/             # application.properties & configuration
│   └── test/                      # Tests unitaires & mocks Mockito
├── terraform/                     # Infrastructure as Code pour AWS
│   ├── main.tf
│   ├── provider.tf
│   └── variables.tf
├── Dockerfile                     # Image Docker de l'application
├── Jenkinsfile                    # Pipeline déclaratif CI/CD en 12 étapes
├── pom.xml                        # Descripteur de projet Maven & dépendances
└── settings.xml                   # Configuration des identifiants et dépôts Nexus
```

---

## 👤 Auteur

* **Mohamed Aziz Hammami** — *Étudiant en Génie Logiciel / DevOps & Cloud*
* **Email** : `hammamimohamedaziz@isikef.u-jendouba.tn`
* **GitHub** : [@aziz11414](https://github.com/aziz11414)
* **Projet Réalisé Dans le Cadre du Module** : DevOps, CI/CD & Cloud Computing (Enseignante : Mme Thouraya Louati)
