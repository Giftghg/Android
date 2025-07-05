# 二手交易平台 API 文档

## 基础信息

- **基础URL**: `http://localhost:5000/api`
- **认证方式**: JWT Bearer Token
- **数据格式**: JSON

## 认证

大部分接口需要认证，请在请求头中添加：
```
Authorization: Bearer YOUR_TOKEN
```

## 用户相关接口

### 1. 用户注册

**接口**: `POST /api/register`

**请求参数**:
```json
{
    "username": "用户名",
    "password": "密码",
    "email": "邮箱"
}
```

**响应示例**:
```json
{
    "message": "注册成功",
    "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...",
    "user_id": 1,
    "username": "用户名"
}
```

**错误响应**:
```json
{
    "error": "用户名已存在"
}
```

### 2. 用户登录

**接口**: `POST /api/login`

**请求参数**:
```json
{
    "username": "用户名",
    "password": "密码"
}
```

**响应示例**:
```json
{
    "message": "登录成功",
    "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...",
    "user_id": 1,
    "username": "用户名"
}
```

**错误响应**:
```json
{
    "error": "用户名或密码错误"
}
```

### 3. 获取用户信息

**接口**: `GET /api/user/profile`

**请求头**: `Authorization: Bearer YOUR_TOKEN`

**响应示例**:
```json
{
    "id": 1,
    "username": "用户名",
    "email": "user@example.com",
    "created_at": "2024-01-01T00:00:00"
}
```

### 4. 获取用户发布的商品

**接口**: `GET /api/user/products`

**请求头**: `Authorization: Bearer YOUR_TOKEN`

**响应示例**:
```json
{
    "products": [
        {
            "id": 1,
            "title": "商品标题",
            "description": "商品描述",
            "price": 100.0,
            "category": "电子产品",
            "condition": "九成新",
            "seller_name": "卖家用户名",
            "image_url": "图片URL",
            "created_at": "2024-01-01T00:00:00",
            "status": "available"
        }
    ]
}
```

## 商品相关接口

### 1. 获取商品列表

**接口**: `GET /api/products`

**查询参数**:
- `page`: 页码（默认1）
- `per_page`: 每页数量（默认10）
- `category`: 商品分类
- `search`: 搜索关键词

**响应示例**:
```json
{
    "products": [
        {
            "id": 1,
            "title": "商品标题",
            "description": "商品描述",
            "price": 100.0,
            "category": "电子产品",
            "condition": "九成新",
            "seller_id": 1,
            "seller_name": "卖家用户名",
            "image_url": "图片URL",
            "created_at": "2024-01-01T00:00:00"
        }
    ],
    "total": 50,
    "pages": 5,
    "current_page": 1
}
```

### 2. 发布商品

**接口**: `POST /api/products`

**请求头**: `Authorization: Bearer YOUR_TOKEN`

**请求参数**:
```json
{
    "title": "商品标题",
    "description": "商品描述",
    "price": 100.0,
    "category": "电子产品",
    "condition": "九成新",
    "image_url": "图片URL（可选）"
}
```

**响应示例**:
```json
{
    "message": "商品发布成功",
    "product_id": 1
}
```

### 3. 获取商品详情

**接口**: `GET /api/products/{product_id}`

**响应示例**:
```json
{
    "id": 1,
    "title": "商品标题",
    "description": "商品描述",
    "price": 100.0,
    "category": "电子产品",
    "condition": "九成新",
    "seller_id": 1,
    "seller_name": "卖家用户名",
    "image_url": "图片URL",
    "created_at": "2024-01-01T00:00:00",
    "status": "available"
}
```

### 4. 更新商品信息

**接口**: `PUT /api/products/{product_id}`

**请求头**: `Authorization: Bearer YOUR_TOKEN`

**请求参数**:
```json
{
    "title": "新标题",
    "description": "新描述",
    "price": 150.0,
    "category": "新分类",
    "condition": "八成新",
    "image_url": "新图片URL",
    "status": "sold"
}
```

**响应示例**:
```json
{
    "message": "商品信息更新成功"
}
```

### 5. 删除商品

**接口**: `DELETE /api/products/{product_id}`

**请求头**: `Authorization: Bearer YOUR_TOKEN`

**响应示例**:
```json
{
    "message": "商品删除成功"
}
```

## 其他接口

### 1. 获取商品分类

**接口**: `GET /api/categories`

**响应示例**:
```json
{
    "categories": [
        "电子产品",
        "服装鞋帽",
        "图书音像",
        "家居用品",
        "运动户外",
        "美妆护肤",
        "食品饮料",
        "其他"
    ]
}
```

### 2. 获取商品成色

**接口**: `GET /api/conditions`

**响应示例**:
```json
{
    "conditions": [
        "全新",
        "九成新",
        "八成新",
        "七成新",
        "六成新",
        "五成新及以下"
    ]
}
```

## 错误码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 201 | 创建成功 |
| 400 | 请求参数错误 |
| 401 | 未认证或认证失败 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 常见错误响应

### 参数错误
```json
{
    "error": "用户名、密码和邮箱都是必填项"
}
```

### 认证失败
```json
{
    "error": "需要认证token"
}
```

### 权限不足
```json
{
    "error": "只有卖家可以修改商品信息"
}
```

### 资源不存在
```json
{
    "error": "商品不存在"
}
```

## 使用示例

### 完整的商品发布流程

1. **注册用户**
```bash
curl -X POST http://localhost:5000/api/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "seller1",
    "password": "123456",
    "email": "seller1@example.com"
  }'
```

2. **登录获取token**
```bash
curl -X POST http://localhost:5000/api/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "seller1",
    "password": "123456"
  }'
```

3. **发布商品**
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

4. **查看商品列表**
```bash
curl http://localhost:5000/api/products
```

## 注意事项

1. 所有时间格式为 ISO 8601 标准
2. 价格使用浮点数表示
3. Token 有效期为 7 天
4. 图片URL 为可选字段
5. 商品状态包括：available（可售）、sold（已售）、reserved（预订） 