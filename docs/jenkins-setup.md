# Jenkins Setup Guide

## Option 1: Local Installation (Windows)

### Prerequisites
- Java 11 or 17 installed
- At least 2GB RAM free

### Steps

1. Download the Jenkins Windows installer from https://www.jenkins.io/download/
   - Choose "Windows" under the LTS column

2. Run the installer and follow the wizard
   - Default port is 8080
   - Jenkins installs as a Windows service (runs automatically on startup)

3. Open Jenkins in your browser:
   ```
   http://localhost:8080
   ```

4. Unlock Jenkins
   - The installer shows you the path to the initial admin password, e.g.:
     ```
     C:\ProgramData\Jenkins\.jenkins\secrets\initialAdminPassword
     ```
   - Open that file, copy the password, paste it into the browser

5. Select "Install suggested plugins" when prompted

6. Create your first admin user and finish setup

---

## Option 2: Docker (Recommended)

This keeps Jenkins isolated and easy to reset. Since you already have Docker Desktop, this is the easier path.

### Run Jenkins container

```bash
docker run -d \
  --name jenkins \
  -p 8080:8080 \
  -p 50000:50000 \
  -v jenkins_home:/var/jenkins_home \
  jenkins/jenkins:lts
```

- `-p 8080:8080` — Jenkins UI
- `-p 50000:50000` — agent communication port
- `-v jenkins_home:/var/jenkins_home` — persists Jenkins data (jobs, plugins, config) in a Docker volume

### Get the initial admin password

```bash
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

### Open Jenkins
```
http://localhost:8080
```

Paste the password, then select "Install suggested plugins".

---

## Required Plugins

After the initial setup, install these plugins via:
`Manage Jenkins` → `Plugins` → `Available plugins`

| Plugin | Purpose |
|---|---|
| Git | Lets Jenkins pull your code from GitHub |
| Pipeline | Enables Jenkinsfile-based pipelines |
| JUnit | Parses and displays Maven test results |
| HTML Publisher | Publishes Surefire HTML reports |

### How to install

1. Go to `Manage Jenkins` → `Plugins` → `Available plugins`
2. Search for each plugin by name
3. Check the box next to it
4. Click `Install` (no restart needed for most plugins)
5. Verify they appear under `Installed plugins`

---

## Basic Configuration

### 1. Configure JDK
`Manage Jenkins` → `Tools` → `JDK installations`
- Click `Add JDK`
- Name: `JDK-11`
- Uncheck "Install automatically" if Java is already on your machine
- Set the path, e.g. `C:\Program Files\Eclipse Adoptium\jdk-11` (local)
- For Docker Jenkins, leave "Install automatically" checked and pick a version

### 2. Configure Maven
`Manage Jenkins` → `Tools` → `Maven installations`
- Click `Add Maven`
- Name: `Maven-3.9`
- Check "Install automatically" and select version `3.9.6`

### 3. Set up GitHub credentials (for private repos)
`Manage Jenkins` → `Credentials` → `System` → `Global credentials` → `Add Credentials`
- Kind: `Username with password`
- Username: your GitHub username
- Password: a GitHub Personal Access Token (not your password)
- ID: `github-credentials`

> For public repos like this project, credentials are not required.

---

## Verify Setup

Once Jenkins is running and plugins are installed, you should see:

- Jenkins dashboard at `http://localhost:8080`
- No red warnings in `Manage Jenkins`
- Git, Pipeline, JUnit, HTML Publisher listed under installed plugins

Pipeline creation comes in the next phase.
