# 二手物品交易平台 Android应用

这是一个基于Android开发的二手物品交易平台应用，提供商品浏览、发布、搜索等功能。

## 功能特性

### 已实现功能
- ✅ 商品浏览和搜索
- ✅ 商品发布（包含图片上传）
- ✅ 商品分类管理
- ✅ 本地数据库存储
- ✅ 现代化Material Design UI
- ✅ 底部导航栏

### 计划实现功能
- 🔄 用户注册和登录
- 🔄 商品详情页
- 🔄 聊天功能
- 🔄 收藏功能
- 🔄 商品评价
- 🔄 地理位置服务

## 技术架构

### 架构模式
- **MVVM架构模式**
- **Repository模式**
- **LiveData数据绑定**

### 主要技术栈
- **Android Jetpack Components**
  - Room Database (本地数据库)
  - ViewModel (视图模型)
  - LiveData (数据观察)
  - Fragment (界面组件)

- **UI组件**
  - Material Design Components
  - RecyclerView (列表展示)
  - BottomNavigationView (底部导航)
  - SwipeRefreshLayout (下拉刷新)

- **图片处理**
  - Glide (图片加载)

- **网络请求**
  - Retrofit (网络请求)
  - OkHttp (HTTP客户端)

## 项目结构

```
app/src/main/java/com/example/myapplication/
├── adapter/          # 适配器类
│   ├── ProductAdapter.java
│   └── ImageAdapter.java
├── database/         # 数据库相关
│   ├── AppDatabase.java
│   ├── UserDao.java
│   ├── ProductDao.java
│   └── MessageDao.java
├── fragment/         # Fragment界面
│   ├── HomeFragment.java
│   └── PublishFragment.java
├── model/           # 数据模型
│   ├── User.java
│   ├── Product.java
│   └── Message.java
├── repository/      # 数据仓库
│   ├── UserRepository.java
│   └── ProductRepository.java
├── viewmodel/       # 视图模型
│   ├── UserViewModel.java
│   └── ProductViewModel.java
├── MainActivity.java
└── SecondHandApp.java
```

## 数据库设计

### 用户表 (users)
- id: 主键
- username: 用户名
- email: 邮箱
- phone: 手机号
- avatar: 头像
- address: 地址
- rating: 评分
- itemCount: 商品数量
- createTime: 创建时间

### 商品表 (products)
- id: 主键
- title: 商品标题
- description: 商品描述
- price: 价格
- originalPrice: 原价
- category: 分类
- condition: 新旧程度
- images: 图片URL
- sellerId: 卖家ID
- sellerName: 卖家名称
- location: 交易地点
- status: 状态
- viewCount: 浏览次数
- favoriteCount: 收藏次数
- createTime: 创建时间
- updateTime: 更新时间

### 消息表 (messages)
- id: 主键
- senderId: 发送者ID
- receiverId: 接收者ID
- content: 消息内容
- messageType: 消息类型
- status: 消息状态
- createTime: 创建时间
- productId: 关联商品ID

## 安装和运行

1. 克隆项目到本地
2. 使用Android Studio打开项目
3. 同步Gradle依赖
4. 连接Android设备或启动模拟器
5. 点击运行按钮

## 开发环境

- Android Studio Hedgehog | 2023.1.1
- Android SDK 35
- Java 11
- Gradle 8.0

## 依赖库

```gradle
// 网络请求
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
implementation 'com.squareup.okhttp3:logging-interceptor:4.11.0'

// 图片加载
implementation 'com.github.bumptech.glide:glide:4.16.0'

// 数据库
implementation 'androidx.room:room-runtime:2.6.1'
implementation 'androidx.room:room-ktx:2.6.1'
annotationProcessor 'androidx.room:room-compiler:2.6.1'

// 生命周期组件
implementation 'androidx.lifecycle:lifecycle-viewmodel:2.7.0'
implementation 'androidx.lifecycle:lifecycle-livedata:2.7.0'

// UI组件
implementation 'androidx.recyclerview:recyclerview:1.3.2'
implementation 'com.google.android.material:material:1.11.0'
```

## 贡献指南

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 联系方式

如有问题或建议，请通过以下方式联系：
- 提交 Issue
- 发送邮件

---

**注意**: 这是一个演示项目，实际部署时需要添加更多的安全措施和错误处理。 