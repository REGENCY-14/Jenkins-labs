# GitHub Webhook Setup Guide

Webhooks let GitHub notify Jenkins automatically whenever you push code,
so you never have to trigger builds manually.

---

## Prerequisites

- Jenkins is running and accessible from the internet (or via ngrok for local)
- GitHub plugin is installed in Jenkins
- Your pipeline job exists in Jenkins

---

## Step 1: Get your Jenkins Webhook URL

Your webhook URL follows this pattern:

```
http://<your-jenkins-url>/github-webhook/
```

Examples:
- Local Jenkins:   `http://localhost:8080/github-webhook/`
- Docker Jenkins:  `http://localhost:8080/github-webhook/`

> If Jenkins is running locally, GitHub cannot reach it directly.
> Use [ngrok](https://ngrok.com/) to expose it:
> ```bash
> ngrok http 8080
> ```
> This gives you a public URL like `https://abc123.ngrok.io`.
> Your webhook URL becomes: `https://abc123.ngrok.io/github-webhook/`

---

## Step 2: Add the Webhook in GitHub

1. Go to your repo: https://github.com/REGENCY-14/Jenkins-labs
2. Click `Settings` → `Webhooks` → `Add webhook`
3. Fill in the form:
   - Payload URL: `http://<your-jenkins-url>/github-webhook/`
   - Content type: `application/json`
   - Secret: leave blank (or add one for extra security)
   - Which events: select `Just the push event`
4. Check `Active`
5. Click `Add webhook`

GitHub will send a ping to Jenkins — you should see a green tick next to the webhook.

---

## Step 3: Configure Jenkins Job to Accept Webhooks

1. Open your pipeline job in Jenkins
2. Click `Configure`
3. Scroll to `Build Triggers`
4. Check `GitHub hook trigger for GITScm polling`
5. Save

The `githubPush()` trigger in the Jenkinsfile handles this automatically,
but the checkbox must also be enabled in the job config.

---

## Step 4: Test It

1. Make any small change to your code (e.g. add a comment)
2. Commit and push to `main`:
   ```bash
   git add .
   git commit -m "test: trigger webhook"
   git push origin main
   ```
3. Watch Jenkins — the build should start within a few seconds automatically

---

## Troubleshooting

| Problem | Fix |
|---|---|
| GitHub shows red X on webhook | Check Jenkins URL is reachable from the internet |
| Build doesn't trigger | Confirm "GitHub hook trigger" checkbox is enabled in job config |
| Running Jenkins locally | Use ngrok to expose port 8080 publicly |
| 302 redirect error | Make sure the webhook URL ends with a trailing slash `/` |
