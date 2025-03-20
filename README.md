# SwiftMock

SwiftMock 是一个基于 **Mockito** 的轻量级 **Spy Mocking** 框架，专为单元测试设计。它能够 **自动解析被测试类的依赖关系，并为其依赖项创建 Spy 实例**，无需用户手动管理依赖 **Mock**，只需直接定义 **Mock** 行为，提高测试编写效率。
## ✨ 特性
- **自动创建 Spy Mock**：递归扫描测试对象，自动创建 **Spy**（部分真实、部分 Mock）实例。
- **支持 Mock 继承关系**：可以自动 Mock 继承层级中的依赖对象。
- **自定义忽略规则**：支持使用 `@IgnoreSpy` 注解排除特定字段、类或类后缀。
- **自动解析接口实现**：可根据 `@ModuleInfo` 自动匹配接口的具体实现类。

## 📦 安装

在你的 **测试项目** 中添加 Maven 依赖：

```xml
<dependency>
    <groupId>io.github.jaylondev</groupId>
    <artifactId>swiftmock</artifactId>
    <version>1.0.0</version>
    <scope>test</scope>
</dependency>
```

## 🚀 快速开始

### 1️⃣ **创建测试类并继承 `SpyMockTest`**

```java
public class UserServiceTest extends SpyMockTest {

    private UserService userService = new UserService();
    
    @Override
    public Object targetTestBean() {
        return userService;
    }

    @Test
    public void testGetUser() {
        UserDao userDaoMock = getSpyInstance(UserDao.class);
        when(userDaoMock.getUserById(1)).thenReturn(new User(1, "Jaylon"));
        
        User user = userService.getUserById(1);

        assertEquals("Jaylon", user.getName());
    }
}
```

### 2️⃣ **自动 Mock 依赖**
- 继承 `SpyMockTest`
- 实现 `targetTestBean()` 返回测试对象
- 通过 `getSpyInstance(Class<T> clazz)` 获取 Spy Mock 实例

### 3️⃣ **自定义忽略规则（可选）**
使用 `@IgnoreSpy` 注解排除不需要 Mock 的类或字段：

```java
@IgnoreSpy(fields = {Logger.class}, classes = {SensitiveDataHandler.class}, classSuffixs = {"Repository"})
public class PaymentServiceTest extends SpyMockTest {
    ...
}
```

## 🏗️ 主要组件
| 类名 | 作用 |
|------|------|
| `SpyMockTest` | 测试基类，提供 `getSpyInstance` 方法获取 Spy Mock |
| `MockRegistry` | 维护已创建的 Mock 实例，避免重复创建 |
| `MockInitializer` | 递归扫描测试对象的依赖，并自动创建 Spy Mock |
| `IgnoreRules` | 处理 `@IgnoreSpy` 规则，跳过指定字段或类 |
| `TargetFilesClassesContainer` | 扫描 `target/classes` 目录，自动匹配接口实现 |
| `BeanUtils` | 反射工具类，获取字段信息 |

## 📌 兼容性
- JDK 1.8+
- JUnit 4 / JUnit 5
- Mockito 4.x

## 📝 贡献
欢迎贡献代码，提交 Issue 或 PR 改进 SwiftMock！

## 📜 许可证
SwiftMock 基于 **MIT License** 发布。

---

🚀 **SwiftMock：让单元测试更简单！**

