# Slack Notifications Setup Guide

## Step 1: Install the Slack Plugin in Jenkins

`Manage Jenkins` → `Plugins` → `Available plugins` → search `Slack Notification` → Install

---

## Step 2: Create a Slack App and get a token

1. Go to https://api.slack.com/apps → `Create New App` → `From scratch`
2. Name it `Jenkins` and pick your workspace
3. In the left menu go to `OAuth & Permissions`
4. Under `Bot Token Scopes` add: `chat:write`
5. Click `Install to Workspace` → Allow
6. Copy the `Bot User OAuth Token` (starts with `xoxb-`)

---

## Step 3: Invite the bot to your channel

In Slack, open the channel you want notifications in (e.g. `#jenkins-builds`) and run:
```
/invite @Jenkins
```

---

## Step 4: Configure Slack in Jenkins

`Manage Jenkins` → `System` → scroll to `Slack`

Fill in:
- Workspace: your Slack workspace name (e.g. `my-team`)
- Credential: click `Add` → `Jenkins`
  - Kind: `Secret text`
  - Secret: paste your `xoxb-` token
  - ID: `slack-token`
- Default channel: `#jenkins-builds`
- Click `Test Connection` — you should see a green success message
- Save

---

## Step 5: Verify the Jenkinsfile

The `slackSend` steps are already configured in the Jenkinsfile `post` block.
No further changes needed — just trigger a build and check your Slack channel.

---

## Notification Format

Success message:
```
BUILD PASSED ✅
Job: Jenkins-labs
Build: #12
Branch: main
Tests: All passed
Details: http://localhost:8080/job/Jenkins-labs/12/
```

Failure message:
```
BUILD FAILED ❌
Job: Jenkins-labs
Build: #12
Branch: main
Details: http://localhost:8080/job/Jenkins-labs/12/
Console: http://localhost:8080/job/Jenkins-labs/12/console
```
