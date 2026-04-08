---
name: test-strategy-designer
version: 1.2
description: 根据架构和业务风险设计测试策略，输出测试计划、工具选型、关键测试用例模板，包含AI专项测试建议和测试数据工厂。
author: AI Agent Expert
---

# Skill: test-strategy-designer

## 触发条件
用户请求“设计测试策略”、“如何测试我的应用”。

## 工作流程

### Step 1: 提取风险点
从架构方案和需求中识别高风险模块：如支付、并发、AI 调用、外部集成。

### Step 2: 测试分层建议
- **单元测试**：覆盖率目标70%，重点测试 Service 层和工具类。
- **集成测试**：测试数据库、缓存、消息队列、外部 API。
- **E2E 测试**：关键用户场景。
- **性能测试**：针对高 QPS 接口进行压测。

### Step 3: 工具选型
- 单元测试：JUnit 5 + Mockito + AssertJ
- 集成测试：Testcontainers
- E2E：RestAssured
- 性能：JMeter 或 Gatling

### Step 4: AI 专项测试建议
- **提示词回归测试**：维护一组输入-期望输出的用例，每次修改提示词后自动运行。
- **RAG 质量评估**：计算召回率、精确率，使用人工标注的问答对进行评估。
- **Agent 工具调用测试**：模拟外部 API，验证 Agent 是否按预期调用工具。

### Step 5: 测试数据管理
- 单元测试：使用 `@Sql` 注解加载测试数据。
- 集成测试：使用 Testcontainers 启动真实数据库并初始化数据。
- 外部 API Mock：使用 WireMock。

### Step 6: 生成测试数据工厂
提供 `TestDataFactory.java` 示例：
```java
public class TestDataFactory {
    public static User createDefaultUser() {
        return User.builder()
            .username("testuser")
            .email("test@example.com")
            .build();
    }
}
```
### Step 7: 输出测试计划模板
```markdown
## 测试策略

### 1. 测试范围与优先级
| 模块 | 风险等级 | 测试类型 | 建议覆盖 |
|------|----------|----------|----------|
| 用户认证 | 高 | 单元+集成 | JWT 解析、过期、刷新 |
| 支付回调 | 高 | 集成+E2E | 幂等性、异常处理 |
| AI 对话 | 中 | 集成+Mock | 提示词注入、超时重试 |

### 2. 测试工具配置
（pom.xml 依赖片段，Testcontainers 配置示例）

### 3. 示例测试类
```java
// UserServiceTest.java（单元测试）
// UserControllerIT.java（集成测试，使用 @SpringBootTest）
```
### 4. 持续集成建议
- 在 CI 中运行单元和集成测试

- 合并前要求测试通过
```text

## 注意事项
- 测试类应与业务代码同步生成，不能只生成业务代码。
- 每个 Service 方法至少包含一个成功测试和一个异常测试。
```