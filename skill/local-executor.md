---
name: local-executor
version: 1.0
description: 在用户一次性授权后，自动执行项目初始化、依赖下载、进程管理等脚本命令。支持跨平台（Linux/macOS/Windows）。
author: AI Agent Expert
---

# Skill: local-executor

## 触发条件
用户请求包含：“自动安装依赖”、“执行初始化脚本”、“运行项目”。

## 工作流程

### Step 1: 请求一次性权限
首次使用时，向用户说明：
> 我将创建一个脚本文件用于自动执行以下命令：`mvn clean install`、`docker-compose up -d` 等。您只需运行一次该脚本，后续所有命令将自动完成。是否同意？

### Step 2: 生成跨平台脚本

#### Linux/macOS (`init.sh`)
```bash
#!/bin/bash
set -e
trap 'echo "错误发生，已停止"; exit 1' ERR

echo "=== 下载依赖 ==="
mvn dependency:resolve

echo "=== 编译项目 ==="
mvn clean compile

echo "=== 启动数据库（如需要） ==="
docker-compose up -d mysql redis

echo "=== 运行测试 ==="
mvn test

echo "=== 启动应用 ==="
nohup mvn spring-boot:run > app.log 2>&1 &
echo $! > app.pid
echo "应用已启动，PID: $(cat app.pid)"
```

#### Windows (init.ps1)
```powershell
# 需要以管理员身份运行
Write-Host "下载依赖..." -ForegroundColor Green
mvn dependency:resolve

Write-Host "编译项目..." -ForegroundColor Green
mvn clean compile

Write-Host "启动数据库..." -ForegroundColor Green
docker-compose up -d mysql redis

Write-Host "启动应用..." -ForegroundColor Green
Start-Process -NoNewWindow "mvn" "spring-boot:run" -RedirectStandardOutput "app.log"
```

### Step 3: 安全约束（AI 必须遵守）
- 脚本中不得包含 rm -rf /、del /F /S、format、dd 等危险命令。

- 所有 kill 操作必须先发送 SIGTERM（kill -15），无效后再用 SIGKILL。

- 必须记录启动进程的 PID 以便后续停止。

- 脚本执行前提示用户阅读内容。

### Step 4: 用户执行
用户执行 chmod +x init.sh && ./init.sh（Linux/macOS）或以管理员身份运行 PowerShell 脚本。

### 集成说明
此 skill 可被 orchestrator 在项目初始化阶段自动调用。