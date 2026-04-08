---
name: code-generator
version: 1.2
description: 根据架构设计输出项目骨架代码、关键类模板、配置文件，支持 Spring Boot + LangChain4j 等常见组合。自动生成测试类，包含常见问题预防配置。
author: AI Agent Expert
---

# Skill: code-generator

## 触发条件
用户在架构设计完成后请求“生成代码”、“生成骨架”、“给我代码模板”。

## 工作流程

### Step 1: 解析架构方案
从上下文中提取：技术栈、项目包名、模块划分。

### Step 2: 让用户选择要生成的模板
请选择您需要的代码模板（可多选，回复编号）：
1. 项目初始化命令
2. 主类与配置文件
3. 全局异常处理 + 统一响应
4. 示例 CRUD 模块（含测试类）
5. LangChain4j 集成（含 RAG 配置）
6. Agent + @Tool 示例

默认会生成测试类，无需额外选择。

### Step 3: 生成选中模板

#### 3.1 项目初始化命令
```bash
curl https://start.spring.io/starter.zip \
  -d bootVersion=3.3.0 \
  -d dependencies=web,data-jpa,validation,security,lombok \
  -d name={{projectName}} -o {{projectName}}.zip
```
#### 3.2 主类与配置文件
提供 Application.java 和 application.yml，包含常见问题预防配置：
```yaml
spring:
  jackson:
    time-zone: Asia/Shanghai
    default-property-inclusion: non_null
  http:
    encoding:
      charset: UTF-8
      force: true
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB
  datasource:
    hikari:
      connection-timeout: 30000
      maximum-pool-size: 10
  web:
    cors:
      allowed-origins: "*"  # 生产环境务必替换为具体域名
      allowed-methods: GET,POST,PUT,DELETE,OPTIONS
      allow-credentials: false
```
#### 3.3 全局异常处理 + 统一响应
提供 Result.java 和 GlobalExceptionHandler.java。

#### 3.4 示例 CRUD 模块（含测试类）
生成 UserController, UserService, UserRepository, User, UserDTO，以及对应的 UserServiceTest（包含成功和异常两个测试用例）。

测试类模板：
```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock private UserRepository userRepository;
    @InjectMocks private UserService userService;

    @Test
    void shouldCreateUser_whenValidInput() {
        // given
        UserDTO input = UserDTO.builder().username("john").build();
        User savedUser = new User(1L, "john");
        when(userRepository.save(any())).thenReturn(savedUser);
        // when
        UserDTO result = userService.createUser(input);
        // then
        assertThat(result.getId()).isEqualTo(1L);
        verify(userRepository).save(any());
    }

    @Test
    void shouldThrowException_whenUsernameExists() {
        // given
        UserDTO input = UserDTO.builder().username("john").build();
        when(userRepository.existsByUsername("john")).thenReturn(true);
        // when + then
        assertThatThrownBy(() -> userService.createUser(input))
            .isInstanceOf(DuplicateUserException.class);
    }
}
```
#### 3.5 LangChain4j 集成
```java
@Configuration
public class AiConfig {
    @Bean
    public ChatLanguageModel chatModel() {
        return OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .build();
    }
}
```
#### 3.6 Agent + @Tool 示例
```java
@Tool
public String searchWeb(String query) { ... }
```
### Step 4: 批量替换占位符建议
提供 sed 命令帮助用户替换 {{package}}、{{ProjectName}} 等占位符。

### 注意事项
- 不生成完整业务逻辑，只生成结构骨架。

- 所有代码模板使用占位符，便于批量替换。

- 测试类默认生成，用户可删除不需要的部分。

