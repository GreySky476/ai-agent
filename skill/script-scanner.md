
---

## 5. `script-scanner.md`（新增）

```markdown
---
name: script-scanner
version: 1.0
description: 在输出脚本前自动扫描危险模式，防止生成恶意或危险的命令。可作为 local-executor 的前置步骤。
author: AI Agent Expert
---

# Skill: script-scanner

## 触发条件
在 `local-executor` 或任何需要生成脚本的场景中自动调用。

## 扫描规则

### 高危模式（必须拒绝）
- `rm -rf /`、`rm -rf /*`、`del /F /S C:\*`
- `dd if=/dev/zero of=/dev/sda`
- `mkfs.*`、`format C:`
- `chmod 777 /`、`chmod -R 777`
- `:(){ :|:& };:`（fork bomb）
- 任何包含 `curl | bash` 且未检查来源的管道命令

### 中危模式（需要警告并要求用户确认）
- `kill -9` 未先尝试 `kill -15`
- `sudo` 命令未说明原因
- 未加引号的变量（可能引发注入）
- 临时文件使用不安全路径（如 `/tmp/$$`）

### 低危模式（建议改进）
- 缺少 `set -e`（bash）
- 缺少错误处理
- 硬编码密码或 token

## 工作流程

### Step 1: 接收待检查的脚本内容
AI 在生成脚本后，自动调用此 skill 进行扫描。

### Step 2: 逐行扫描并报告
输出格式：
```markdown
## 脚本安全扫描报告

| 行号 | 内容 | 风险等级 | 建议 |
|------|------|----------|------|
| 5 | `rm -rf /tmp/*` | 中危 | 使用 `rm -rf /tmp/*` 需要谨慎，建议指定具体文件 |
| 12 | `kill -9 $PID` | 中危 | 建议先使用 `kill -15` 优雅终止 |
| 无 | - | 安全 | 未发现高危模式 |

### 扫描结论
- 高危：0
- 中危：2
- 低危：0

是否继续输出脚本？请确认。
```
### Step 3: 自动修正（可选）
对于中危问题，AI 可以自动修正后再输出。例如将 kill -9 替换为优雅终止逻辑。

### 集成说明
local-executor 在生成脚本后必须调用此 skill 进行扫描，扫描通过后方可提供给用户。
