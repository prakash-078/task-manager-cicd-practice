# TaskFlow — Java + Spring Boot CI/CD Practice

A small task manager with a Java 21 / Spring Boot REST API and a plain HTML/CSS/JavaScript frontend served by Spring Boot. It is intentionally database-free so you can focus on Git, Maven, Docker, GitHub Actions and EC2 deployment.

**Note:** Tasks are stored in memory and reset whenever the application restarts. This is a learning project, not a production persistence design.

## Requirements

- JDK 21
- Maven 3.6.3+ (or use the Maven wrapper if you add one)
- Git
- Docker Desktop (local image/container practice)
- GitHub and Docker Hub accounts

## 1. Run locally

From the project root:

```bash
mvn clean verify
mvn spring-boot:run
```

Open `http://localhost:8080`.

REST endpoints:
- `GET /api/tasks` — list tasks
- `POST /api/tasks` — create task; JSON body: `{"title":"Practise Docker"}`
- `PATCH /api/tasks/{id}/toggle` — toggle completion
- `DELETE /api/tasks/{id}` — delete task

## 2. Practise Git

Create your own GitHub repository, then:

```bash
git init
git add .
git commit -m "Create TaskFlow application"
git branch -M main
git remote add origin YOUR_GITHUB_REPOSITORY_URL
git push -u origin main
```

Try making a small frontend change, committing it, and pushing again.

## 3. Practise Docker locally

Build the JAR first. The Dockerfile copies the JAR from `target/`.

```bash
mvn clean verify
docker build -t taskflow:local .
docker run --name taskflow-local -d -p 8080:8080 taskflow:local
docker ps
docker logs taskflow-local
```

Open `http://localhost:8080`.

Stop and remove the container:

```bash
docker stop taskflow-local
docker rm taskflow-local
```

## 4. Configure GitHub Actions

1. Create a Docker Hub repository named `taskflow`.
2. In GitHub, open **Settings → Secrets and variables → Actions**.
3. Add `DOCKER_USERNAME` with your Docker Hub username.
4. Add `DOCKER_PASSWORD` with a Docker Hub access token (not your account password).
5. In `.github/workflows/docker-ci.yml`, replace `YOUR_DOCKERHUB_USERNAME` with your Docker Hub username.
6. If your default GitHub branch is not `main`, update both branch filters to match its exact spelling and capitalization.
7. Commit and push the workflow.

The workflow runs Maven tests for pushes and pull requests. It publishes a Docker image only on pushes. GitHub Secrets are referenced in YAML; never put actual credentials in the file.

## 5. Pull and run on EC2

After the image appears in Docker Hub, connect to your Ubuntu EC2 instance and run:

```bash
sudo docker login -u YOUR_DOCKERHUB_USERNAME
sudo docker pull YOUR_DOCKERHUB_USERNAME/taskflow:latest
sudo docker run -d --name taskflow -p 8080:8080 YOUR_DOCKERHUB_USERNAME/taskflow:latest
sudo docker ps
sudo docker logs taskflow
```

For a private Docker Hub repository, authenticate using a Docker Hub access token. Do not paste the token into chat or commit it.

In the EC2 security group, allow inbound TCP port `8080` from your own IP for testing. Then visit:

`http://YOUR_EC2_PUBLIC_IP:8080`

## Files supplied

- `Dockerfile` — packages the already-built Spring Boot JAR into a Java 21 runtime image.
- `.dockerignore` — excludes unnecessary files from Docker build context. It intentionally does **not** exclude `target/`, because the Dockerfile copies the Maven-built JAR from there.
- `.gitignore` — excludes generated build output and IDE/local files from Git.
- `.github/workflows/docker-ci.yml` — Maven test/build plus Docker Hub image publishing.
- `compose.yml` — optional single-service Docker Compose configuration; replace the image username before using it.

## Learning challenge

Try to complete the project yourself before asking for the solution:
1. Run the app and test all four API operations.
2. Add a `priority` field to tasks.
3. Write a test for deleting a task.
4. Build and run the Docker image locally.
5. Push the project to GitHub and get the workflow green.
6. Verify the `latest` tag in Docker Hub.
7. Pull and run the image on EC2.

When you finish this, we can move to **Continuous Deployment**: automatically deploying a new image to EC2 whenever a successful GitHub Actions run publishes it.
