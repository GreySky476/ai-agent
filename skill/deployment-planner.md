---
name: deployment-planner
version: 1.1
description: 生成 Dockerfile、K8s 部署文件、CI/CD 流水线配置模板，支持多环境配置和健康检查。
author: AI Agent Expert
---

# Skill: deployment-planner

## 触发条件
用户请求“部署方案”、“生成 Dockerfile”、“配置 CI/CD”。

## 工作流程

### Step 1: 确认部署环境
从架构方案中获取：部署环境（单机 Docker / K8s / Serverless）、是否需要 CI/CD。

### Step 2: 生成 Dockerfile（多阶段构建）
```dockerfile
FROM openjdk:17-jdk-slim AS build
...
FROM openjdk:17-jdk-slim
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```
### Step 3: 生成 docker-compose.yml（开发/测试环境）
```yaml
version: '3'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=dev
  mysql:
    image: mysql:8.0
    ...
  redis:
    image: redis:7-alpine
```
### Step 4: 多环境配置文件
提供 application-dev.yml 和 application-prod.yml 模板，使用环境变量区分。

### Step 5: K8s 部署文件（生产环境）
- Deployment.yaml（含 livenessProbe 和 readinessProbe）

- Service.yaml

- ConfigMap.yaml

- Ingress.yaml（可选）

### Step 6: CI/CD 流水线模板
- GitLab CI（.gitlab-ci.yml）：包含 test, build, deploy, rollback 阶段

- GitHub Actions（.github/workflows/build.yml）

### Step 7: 监控与日志建议
- 关键指标：QPS、错误率、响应时间

- 日志格式：JSON，输出到 stdout

- 推荐工具：Prometheus + Grafana + Loki