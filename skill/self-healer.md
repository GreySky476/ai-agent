
---
name: self-healer
version: 1.0
description: 监控项目启动日志，识别常见错误并自动执行修复脚本。支持规则库匹配和 AI 智能分析回退。
author: AI Agent Expert
---

# Skill: self-healer

## 触发条件
用户启动项目后出现错误，或主动请求“自动修复启动问题”。

## 工作流程

### Step 1: 读取错误日志
用户提供错误日志片段，或 AI 从 `app.log` 中读取最后 50 行。

### Step 2: 规则库匹配
使用内置规则表识别错误：

| 错误模式 | 检测关键词 | 修复动作 |
|----------|------------|----------|
| 端口被占用 | `Address already in use` | 生成 `kill-port.sh` 脚本 |
| 数据库连接失败 | `Communications link failure` | `docker-compose restart mysql` |
| Maven 依赖失败 | `Could not resolve dependencies` | `mvn clean install -U` |
| Redis 连接超时 | `Redis connection timeout` | `docker-compose up -d redis` |
| 文件权限错误 | `Permission denied` | `chmod +x *.sh` |
| 内存不足 | `OutOfMemoryError` | 建议 JVM 参数 `-Xmx1024m` |
| 类未找到 | `ClassNotFoundException` | 检查依赖版本，建议 `mvn dependency:tree` |

### Step 3: 智能分析回退（当规则库无匹配时）
AI 应：
1. 提取关键错误类型（如 `Connection refused`、`UnknownHostException`）。
2. 分析可能原因（服务未启动、配置错误、网络问题）。
3. 生成具体的修复命令建议。
4. 将新规则建议输出给用户，用户可确认后加入规则库。

示例输出：
> 检测到 `Connection refused: connect` 错误，可能原因：MySQL 未启动。请执行 `docker-compose up -d mysql` 或检查 `application.yml` 中的数据库 URL。

### Step 4: 自动生成修复脚本
生成 `fix.sh` 并提示用户执行：
```bash
#!/bin/bash
echo "=== 自动修复脚本 ==="
# 根据匹配的规则生成具体命令
docker-compose restart mysql
echo "修复完成，请重新启动应用"
```

### Step 5: 验证修复结果
再次读取日志，若问题解决则输出“启动成功”，否则建议用户提供更多信息。

### 扩展性
用户可自定义规则：提供错误日志样本和期望的修复命令，AI 将其加入规则库。