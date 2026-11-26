# Gestion Station de Ski — Full-Stack (Spring Boot & Angular) & DevOps CI/CD

Projet d'ingénierie logicielle et d'intégration continue / déploiement continu (DevOps) pour la gestion d'un domaine skiable avec Spring Boot, Angular 17, Docker, Docker Compose, Jenkins, SonarQube, Nexus, Kubernetes, Terraform et observabilité Prometheus / Grafana.

---

## Présentation du Projet

**Gestion Station de Ski** est une application d'entreprise Full-Stack industrielle :
- **Backend Spring Boot 2.7.5 & Java 17** : expose une API REST complète pour orchestrer l'ensemble des activités d'un domaine skiable (gestion des skieurs, moniteurs, cours, inscriptions, abonnements et pistes).
- **Frontend Angular 17** : tableau de bord opérationnel moderne et responsive avec KPI temps réel, gestion interactive des skieurs, inscriptions aux cours et surveillance de la station (Design System Manrope, palette Ink & Mist, profil de piste SVG).

Le projet intègre une **chaîne industrielle DevOps complète** et automatisée : intégration continue (CI), analyse statique de code et couverture de tests, déploiement d'artefacts binaires sur Nexus, conteneurisation Docker, orchestration multi-conteneurs Docker Compose, notifications e-mails automatiques, provisionnement d'infrastructure as Code (IaC) avec Terraform sur AWS, orchestration de conteneurs avec Kubernetes sur AWS EKS, et supervision en temps réel avec Prometheus et Grafana.

---

## Sommaire

1. [Architecture & Technologies](#architecture--technologies)
2. [Modèle Métier & API REST](#modèle-métier--api-rest)
3. [Documentation OpenAPI & Swagger](#documentation-openapi--swagger)
4. [Pipeline CI/CD Jenkins & Notifications E-mail](#pipeline-cicd-jenkins--notifications-e-mail)
5. [Conteneurisation Docker & Orchestration Multi-Conteneurs](#conteneurisation-docker--orchestration-multi-conteneurs)
6. [Déploiement Kubernetes sur AWS EKS (Full-Stack)](#déploiement-kubernetes-sur-aws-eks-full-stack)
7. [Infrastructure as Code (Terraform) & Cloud AWS](#infrastructure-as-code-terraform--cloud-aws)
8. [Observabilité & Monitoring (Prometheus & Grafana)](#observabilité--monitoring-prometheus--grafana)
9. [Guide de Démarrage Rapide](#guide-de-démarrage-rapide)
10. [Structure du Dépôt](#structure-du-dépôt)
11. [Auteur](#auteur)

---

## Architecture & Technologies

### Backend Applicatif
* **Langage** : Java 17 / Compatibilité Java 8
* **Framework** : Spring Boot 2.7.5
  * Spring Data JPA / Hibernate (ORM & Persistance)
  * Spring Web (API RESTful)
  * Spring Boot Actuator (Indicateurs d'état & métriques de santé)
  * Springdoc OpenAPI 1.6.12 (Documentation interactive Swagger)
* **Base de données** : MySQL 8.0 (Driver `mysql-connector-j`)
* **Tests & Qualité** : JUnit 5, Mockito, JaCoCo (Couverture de code)
* **Logging** : SLF4J structuré et normalisé

### Frontend Applicatif
* **Framework** : Angular 17 (Architecture Standalone, TypeScript, RxJS)
* **Design & Expérience Utilisateur** : Design System Alpe d'Azur (Typographie Manrope, palette Ink & Mist, KPI cards animées, profil d'altitude SVG)
* **Conteneurisation Frontend** : Docker multi-stage (`node:20-alpine` build + `nginx:1.27-alpine` runtime, image ultra-légère de 21 Mo)
* **Orchestration Multi-conteneurs** : Docker Compose (`docker-compose.yml`) avec réseau unifié reliant Frontend (`:8082`) et Backend (`:8080`)

### Écosystème DevOps & Cloud
* **Intégration Continue** : Jenkins (Pipeline Déclaratif as Code via `Jenkinsfile` en 12 étapes)
* **Déclenchement Automatisé** : Polling SCM et Webhook GitHub (Ngrok) pour builds automatiques à chaque commit
* **Analyse de Qualité** : SonarQube (Quality Gate, détection des vulnérabilités, Code Smells et bugs)
* **Gestionnaire d'Artefacts** : Sonatype Nexus Repository Manager (Dépôts `maven-releases` et `maven-snapshots`)
* **Conteneurisation** : Docker & Dockerfile multi-stage pour Backend et Frontend
* **Infrastructure as Code** : Terraform (Providers AWS, HashiCorp)
* **Cloud & Orchestration** : AWS Academy Learner Lab (STS, IAM, VPC, EKS), Kubernetes Full-Stack Backend & Frontend (`k8s/`)
* **Supervision & Observabilité** : Prometheus (Collecte des métriques Spring Boot & Jenkins) et Grafana (Dashboards JVM, HTTP et Pipelines)
* **Notifications** : Système d'e-mails étendu Jenkins (Extended E-mail avec template HTML responsive et logs)

---

## Modèle Métier & API REST

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

## Documentation OpenAPI & Swagger

L'API est documentée selon la spécification OpenAPI 3. Une fois l'application démarrée, l'interface interactive Swagger UI est accessible sur :

```
http://localhost:8080/stationSki/swagger-ui/index.html
```

* **Spécification OpenAPI (JSON)** : `http://localhost:8080/stationSki/v3/api-docs`
* **Contexte Applicatif** : `/stationSki`

---

## Pipeline CI/CD Jenkins & Notifications E-mail

Le pipeline d'intégration et de livraison continue est entièrement automatisé via le `Jenkinsfile` présent à la racine du projet :

```
+--------------------------------------------------------------------------------------------------------------------+
|                                             JENKINS PIPELINE CI/CD (12 STAGES)                                     |
+--------------------------------------------------------------------------------------------------------------------+
|  1. CHECKOUT GIT          -> Récupération automatique du code source depuis GitHub                                  |
|  2. MVN CLEAN             -> Nettoyage de l'espace de travail Maven                                                |
|  3. ARTIFACT CONSTRUCTION -> Compilation et packaging du projet (JAR)                                             |
|  4. UNIT TESTS            -> Exécution des tests unitaires Mockito & JUnit (rapports Surefire XML)                 |
|  5. MVN SONARQUBE         -> Analyse statique de code & passage du Quality Gate SonarQube                           |
|  6. PUBLISH TO NEXUS      -> Publication de l'artefact versionné sur Sonatype Nexus via settings.xml               |
|  7. BUILDING OUR IMAGE    -> Construction de l'image Docker (docker build)                                         |
|  8. DEPLOY OUR IMAGE      -> Tag et préparation de l'image Docker de version (v1.0)                                |
|  9. Test AWS Credentials  -> Validation des identifiants et tokens AWS Academy (STS / IAM)                         |
| 10. SETUP TERRAFORM       -> Validation de l'infrastructure Terraform (Security Group Ports 80 & 8080)             |
| 11. UPDATE KUBECONFIG     -> Configuration du contexte Kubernetes pour le cluster AWS EKS                          |
| 12. DEPLOY TO KUBERNETES  -> Déploiement Full-Stack Kubernetes EKS (Backend API & Frontend Angular)                |
+--------------------------------------------------------------------------------------------------------------------+
```

### Notifications E-mail Automatisées :
À chaque exécution du pipeline, un rapport complet au format HTML responsive est expédié automatiquement à `med.aziz2612@gmail.com` :
* Badge d'état dynamique (`✔ BUILD REUSSI` / `✖ BUILD ECHOUE`).
* Métadonnées du build (Numéro, durée, commit GitHub).
* Checklist visuelle des 12 étapes validées.
* Extrait des dernières lignes de la console et journal complet attaché en pièce jointe (`build.log`).

### Configuration des Outils CI/CD :
* **Jenkins Controller** : `http://localhost:8090` (Job : `gestion-station-skii-pipeline`)
* **SonarQube** : `http://localhost:9000` (Projet `gestion-station-skii`)
* **Sonatype Nexus** : `http://localhost:8081` (Dépôts Maven `maven-releases` et `maven-snapshots`)

---

## Conteneurisation Docker & Orchestration Multi-Conteneurs

### 1. Dockerfile Backend (`Dockerfile`)
L'application Spring Boot est encapsulée dans une image basée sur Eclipse Temurin 21 JRE Alpine :
```dockerfile
FROM eclipse-temurin:21-jre-alpine
VOLUME /tmp
EXPOSE 8080
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

### 2. Dockerfile Frontend (`station-ski-ui/Dockerfile`)
L'application Angular 17 est construite via un build multi-stage garantissant une taille minimale de **21 Mo** :
```dockerfile
FROM node:20-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build -- --configuration production

FROM nginx:1.27-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/dist/station-ski-ui/browser /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

### 3. Orchestration Docker Compose (`docker-compose.yml`)
Permet de déployer et de relier l'ensemble de la pile applicative avec une seule commande :
```bash
docker compose up -d --build
```
* **Frontend Angular** : `http://localhost:8082`
* **Backend Spring Boot** : `http://localhost:8080/stationSki`

---

## Déploiement Kubernetes sur AWS EKS (Full-Stack)

Le dossier `k8s/` contient les manifestes déclaratifs prêts pour le cluster Amazon EKS :

* **Backend Spring Boot :**
  * `k8s/deployment.yaml` : Déploiement de l'API avec **2 réplicas** en haute disponibilité, sondes et limites de ressources (512 Mo à 1024 Mo RAM).
  * `k8s/service.yaml` : Service de type `NodePort` exposant le port interne `8080` sur le port externe **`30080`**.
* **Frontend Angular :**
  * `k8s/frontend-deployment.yaml` : Déploiement du serveur Nginx avec **2 réplicas**, optimisé pour les charges Web.
  * `k8s/frontend-service.yaml` : Service de type `NodePort` exposant le port Web `80` sur le port externe **`30082`**.

---

## Infrastructure as Code (Terraform) & Cloud AWS

Le dossier `terraform/` contient la configuration déclarative de l'infrastructure Cloud hébergée sur **Amazon Web Services (AWS)** :

* **`provider.tf`** : Déclaration du provider AWS et de la région cible (`us-east-1` pour AWS Academy Learner Lab).
* **`variables.tf`** : Paramétrage du cluster Kubernetes EKS (`gestion-station-skii-cluster`).
* **`main.tf`** : Création du groupe de sécurité (`gestion-station-skii-sg`) autorisant :
  * Le port **`80`** : Trafic Web HTTP entrant pour l'interface Angular.
  * Le port **`8080`** : Trafic API REST entrant pour le backend Spring Boot.
  * Tout le trafic sortant (Egress).

---

## Observabilité & Monitoring (Prometheus & Grafana)

Le dossier `monitoring/` contient l'infrastructure de surveillance prête à l'emploi :

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

* **Prometheus (`http://localhost:9090`)** : Scrape automatiquement les métriques de santé JVM de Spring Boot (`/stationSki/actuator/prometheus`) et de Jenkins (`/prometheus/`).
* **Grafana (`http://localhost:3000`)** : Tableaux de bord dynamiques (temps de réponse HTTP, utilisation CPU/RAM de la JVM, statut des builds CI/CD).

---

## Guide de Démarrage Rapide

### Prérequis
* **JDK 17** ou supérieur
* **Node.js 20+** & **npm** (pour le frontend en développement)
* **Docker & Docker Compose**
* **MySQL Server 8.0**

### 1. Cloner le Dépôt
```bash
git clone https://github.com/aziz11414/gestion-station-skii.git
cd gestion-station-skii
```

### 2. Démarrage Rapide avec Docker Compose (Recommandé)
Pour lancer instantanément le Frontend Angular et le Backend Spring Boot :
```bash
docker compose up -d --build
```
* **Accès Frontend** : `http://localhost:8082`
* **Accès Backend API** : `http://localhost:8080/stationSki`
* **Documentation Swagger** : `http://localhost:8080/stationSki/swagger-ui/index.html`

### 3. Démarrage en Mode Développement
* **Backend Spring Boot :**
  ```bash
  ./mvnw clean package
  ./mvnw spring-boot:run
  ```
* **Frontend Angular :**
  ```bash
  cd station-ski-ui
  npm install
  npm start
  ```
  Accessible sur : `http://localhost:4200`

### 4. Lancer la Pile de Monitoring
```bash
cd monitoring
docker-compose up -d
```
* **Prometheus** : `http://localhost:9090`
* **Grafana** : `http://localhost:3000` *(Login : admin / admin)*

---

## Structure du Dépôt

```plaintext
gestion-station-skii/
├── .mvn/wrapper/                  # Fichiers du wrapper Maven
├── k8s/                           # Manifests Kubernetes Full-Stack (AWS EKS)
│   ├── deployment.yaml            # Déploiement Backend API (2 réplicas)
│   ├── service.yaml               # Service Backend (NodePort 30080)
│   ├── frontend-deployment.yaml   # Déploiement Frontend Angular (2 réplicas)
│   └── frontend-service.yaml      # Service Frontend (NodePort 30082)
├── monitoring/                    # Stack de supervision & observabilité
│   ├── docker-compose.yml
│   ├── prometheus.yml
│   └── grafana/
├── src/                           # Code source Java et ressources Spring Boot
│   ├── main/java/...              # Contrôleurs, Entités, Services, WebConfig
│   └── test/java/...              # Tests unitaires JUnit 5 & Mockito
├── station-ski-ui/                # Frontend Angular 17 complet
│   ├── src/                       # Composants Standalone, modèles, CSS Manrope
│   ├── Dockerfile                 # Build multi-stage Node + Nginx (21 Mo)
│   ├── nginx.conf                 # Configuration serveur Nginx SPA
│   ├── angular.json
│   └── package.json
├── terraform/                     # Infrastructure as Code pour AWS
│   ├── main.tf                    # Security Group Ports 80 & 8080
│   ├── provider.tf                # Provider AWS us-east-1
│   └── variables.tf               # Variables EKS
├── Dockerfile                     # Image Docker du Backend Spring Boot
├── docker-compose.yml             # Orchestration Multi-conteneurs Frontend + Backend
├── Jenkinsfile                    # Pipeline déclaratif CI/CD en 12 étapes + Notifications
├── pom.xml                        # Descripteur de projet Maven & dépendances
├── settings.xml                   # Configuration des identifiants et dépôts Nexus
└── README.md                      # Documentation complète du projet
```

---

## Auteur

* **Mohamed Aziz Hammami** — *Étudiant en Génie Logiciel / DevOps & Cloud*
* **Email** : `med.aziz2612@gmail.com`
* **GitHub** : [@aziz11414](https://github.com/aziz11414)
* **Projet Réalisé Dans le Cadre du Module** : DevOps, CI/CD & Cloud Computing (Enseignante : Mme Thouraya Louati)
