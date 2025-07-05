# HTTP流量问题解决方案

## 问题描述
错误信息：`Cleartext HTTP traffic to 10.34.68.221 not permitted`

这是因为Android 9.0（API 28）及以上版本默认不允许明文HTTP流量。

## 解决方案

### 1. 网络安全配置
已创建 `app/src/main/res/xml/network_security_config.xml`：
```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="true">
        <trust-anchors>
            <certificates src="system"/>
        </trust-anchors>
    </base-config>
</network-security-config>
```

### 2. AndroidManifest.xml配置
已在 `app/src/main/AndroidManifest.xml` 中添加：
```xml
android:networkSecurityConfig="@xml/network_security_config"
android:usesCleartextTraffic="true"
```

### 3. 验证步骤

#### 步骤1：重新编译应用
```bash
./gradlew clean
./gradlew assembleDebug
```

#### 步骤2：卸载并重新安装应用
```bash
adb uninstall com.example.myapplication
adb install app/build/outputs/apk/debug/app-debug.apk
```

#### 步骤3：测试网络连接
1. 打开应用
2. 长按登录按钮测试网络连接
3. 查看日志输出

### 4. 调试命令

#### 查看应用日志
```bash
adb logcat | grep "ApiClient\|NetworkTest"
```

#### 测试网络连接
```bash
adb shell ping 10.34.68.221
```

#### 检查防火墙
确保Windows防火墙允许端口5000的入站连接

### 5. 替代解决方案

如果上述方法仍然不工作，可以尝试：

#### 方案A：使用HTTPS
将后端服务器配置为HTTPS，并更新BASE_URL为：
```java
private static final String BASE_URL = "https://10.34.68.221:5000/api";
```

#### 方案B：使用本地测试
将BASE_URL改为本地地址：
```java
private static final String BASE_URL = "http://127.0.0.1:5000/api";
```

#### 方案C：使用模拟器网络
如果使用Android模拟器，可以使用：
```java
private static final String BASE_URL = "http://10.0.2.2:5000/api";
```

### 6. 常见问题排查

#### 问题1：仍然出现HTTP流量错误
- 检查网络安全配置文件是否正确
- 确认AndroidManifest.xml中的配置
- 重新编译并安装应用

#### 问题2：网络连接超时
- 检查后端服务器是否运行
- 确认IP地址是否正确
- 检查防火墙设置

#### 问题3：应用崩溃
- 查看完整日志：`adb logcat`
- 检查代码中的异常处理

### 7. 测试网络连接

使用应用内置的网络测试功能：
1. 打开应用
2. 长按登录按钮
3. 查看测试结果

或者使用命令行测试：
```bash
curl -X GET http://10.34.68.221:5000/api/health
```

### 8. 最终验证

成功配置后，应该能够：
1. 正常注册用户
2. 正常登录
3. 查看商品列表
4. 发布商品

如果仍有问题，请检查：
- 后端服务器状态
- 网络连接
- 防火墙设置
- 应用权限 