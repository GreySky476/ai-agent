---
name: frontend-dev-manager
version: 1.0
description: 自动管理前端开发服务器（Vue/React/Angular），支持端口清理、优雅重启、日志查看，跨平台。
author: AI Agent Expert
---

# Skill: frontend-dev-manager

## 触发条件
用户请求：“启动前端”、“重启前端”、“前端端口被占用”。

## 工作流程

### Step 1: 检测项目类型
扫描目录中的 `package.json`、`vite.config.js`、`vue.config.js` 等，判断框架类型。

### Step 2: 生成管理脚本

#### Linux/macOS (`frontend-manager.sh`)
```bash
#!/bin/bash
PORT=${1:-3000}
ACTION=${2:-restart}

# 优雅终止函数
kill_pid() {
  local pid=$1
  kill -15 $pid 2>/dev/null
  sleep 2
  if kill -0 $pid 2>/dev/null; then
    echo "进程未响应，强制终止"
    kill -9 $pid
  fi
}

# 查找占用端口的 PID
if [[ "$OSTYPE" == "darwin"* ]] || [[ "$OSTYPE" == "linux"* ]]; then
  PID=$(lsof -ti:$PORT 2>/dev/null)
fi

stop() {
  if [ -n "$PID" ]; then
    echo "Killing process on port $PORT: $PID"
    kill_pid $PID
    sleep 1
  else
    echo "No process found on port $PORT"
  fi
}

start() {
  echo "Starting frontend on port $PORT"
  npm run dev -- --port $PORT > frontend.log 2>&1 &
  echo $! > frontend.pid
  echo "Started with PID: $(cat frontend.pid)"
}

restart() {
  stop
  start
}

case "$ACTION" in
  stop) stop ;;
  start) start ;;
  restart) restart ;;
  *) echo "Usage: $0 [port] {start|stop|restart}" ;;
esac
```

#### Windows (frontend-manager.ps1)

```powershell
param(
    [int]$Port = 3000,
    [string]$Action = "restart"
)

function Stop-ProcessByPort {
    $connection = netstat -ano | findstr ":$Port"
    if ($connection) {
        $pid = ($connection -split '\s+')[-1]
        Write-Host "Killing process $pid on port $Port"
        Stop-Process -Id $pid -Force
        Start-Sleep -Seconds 1
    } else {
        Write-Host "No process found on port $Port"
    }
}

function Start-Frontend {
    Write-Host "Starting frontend on port $Port"
    $process = Start-Process -NoNewWindow "npm" "run dev -- --port $Port" -PassThru
    $process.Id | Out-File -FilePath "frontend.pid"
    Write-Host "Started with PID: $($process.Id)"
}

switch ($Action) {
    "stop" { Stop-ProcessByPort }
    "start" { Start-Frontend }
    "restart" { Stop-ProcessByPort; Start-Frontend }
    default { Write-Host "Usage: .\frontend-manager.ps1 -Port 3000 -Action start|stop|restart" }
}
```
### Step 3: 提供使用说明

>- Linux/macOS: chmod +x frontend-manager.sh && ./frontend-manager.sh 3000 restart
>
>- Windows: .\frontend-manager.ps1 -Port 3000 -Action restart
>
>- 查看日志: tail -f frontend.log (Linux/macOS) 或 Get-Content frontend.log -Wait (Windows)

### 集成到 orchestrator
当用户选择前端项目时，orchestrator 自动调用此 skill 生成管理脚本。