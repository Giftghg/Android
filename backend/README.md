# 二手交易平台 Flask 后端

这是一个基于 Flask 的二手交易平台后端 API 服务。

## 功能特性

- 用户注册和登录
- JWT 身份认证
- 商品发布、查询、更新、删除
- 用户信息管理
- 商品分类和成色管理
- SQLite 数据库存储

## API 接口

### 用户相关
- `POST /api/register` - 用户注册
- `POST /api/login` - 用户登录
- `GET /api/user/profile` - 获取用户信息
- `GET /api/user/products` - 获取用户发布的商品

### 商品相关
- `GET /api/products` - 获取商品列表
- `POST /api/products` - 发布商品
- `GET /api/products/<id>` - 获取商品详情
- `PUT /api/products/<id>` - 更新商品信息
- `DELETE /api/products/<id>` - 删除商品

### 其他
- `GET /api/categories` - 获取商品分类
- `GET /api/conditions` - 获取商品成色

## 安装和运行

### 1. 安装依赖

```bash
pip install -r requirements.txt
```

### 2. 运行服务

```bash
python run.py
```

或者直接运行：

```bash
python app.py
```

服务将在 `http://localhost:5000` 启动。

### 3. 测试 API

运行测试脚本：

```bash
python test_api.py
```

## 数据库

- 使用 SQLite 数据库
- 数据库文件：`secondhand.db`
- 自动创建表结构

## 配置

主要配置在 `config.py` 文件中：

- `SECRET_KEY` - Flask 密钥
- `SQLALCHEMY_DATABASE_URI` - 数据库连接
- `JWT_SECRET_KEY` - JWT 密钥
- `UPLOAD_FOLDER` - 文件上传目录

## 环境变量

可以设置以下环境变量：

- `SECRET_KEY` - Flask 密钥
- `DATABASE_URL` - 数据库连接 URL
- `JWT_SECRET_KEY` - JWT 密钥

## 项目结构

```
backend/
├── app.py              # 主应用文件
├── config.py           # 配置文件
├── run.py              # 启动文件
├── test_api.py         # API 测试文件
├── requirements.txt    # Python 依赖
├── README.md          # 说明文档
├── secondhand.db      # SQLite 数据库文件
└── uploads/           # 文件上传目录
```

## 使用示例

### 用户注册

```bash
curl -X POST http://localhost:5000/api/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "123456",
    "email": "test@example.com"
  }'
```

### 用户登录

```bash
curl -X POST http://localhost:5000/api/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "123456"
  }'
```

### 发布商品

```bash
curl -X POST http://localhost:5000/api/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "title": "iPhone 12",
    "description": "九成新iPhone 12",
    "price": 3999.0,
    "category": "电子产品",
    "condition": "九成新"
  }'
```

### 获取商品列表

```bash
curl http://localhost:5000/api/products
```

## 注意事项

1. 确保 Python 3.7+ 环境
2. 生产环境请修改 `SECRET_KEY`
3. 建议使用 PostgreSQL 或 MySQL 替代 SQLite
4. 添加适当的日志记录
5. 配置 CORS 策略
6. 添加请求频率限制
7. 实现文件上传功能

## 开发计划

- [ ] 文件上传功能
- [ ] 商品搜索和筛选
- [ ] 用户收藏功能
- [ ] 消息通知系统
- [ ] 支付集成
- [ ] 评价系统
- [ ] 管理员后台 