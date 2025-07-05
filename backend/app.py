from flask import Flask, request, jsonify
from flask_sqlalchemy import SQLAlchemy
from flask_cors import CORS
from werkzeug.security import generate_password_hash, check_password_hash
import jwt
import datetime
import os

app = Flask(__name__)
app.config['SECRET_KEY'] = 'your-secret-key-here'
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///secondhand.db'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

db = SQLAlchemy(app)
CORS(app)

# 用户模型
class User(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(80), unique=True, nullable=False)
    password = db.Column(db.String(120), nullable=False)
    email = db.Column(db.String(120), unique=True, nullable=False)
    created_at = db.Column(db.DateTime, default=datetime.datetime.utcnow)

# 商品模型
class Product(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    title = db.Column(db.String(200), nullable=False)
    description = db.Column(db.Text, nullable=False)
    price = db.Column(db.Float, nullable=False)
    category = db.Column(db.String(100), nullable=False)
    condition = db.Column(db.String(50), nullable=False)
    seller_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False)
    seller_name = db.Column(db.String(80), nullable=False)
    image_url = db.Column(db.String(500))
    created_at = db.Column(db.DateTime, default=datetime.datetime.utcnow)
    status = db.Column(db.String(20), default='available')  # available, sold, reserved

# 创建数据库表
with app.app_context():
    db.create_all()

def generate_token(user_id):
    """生成JWT token"""
    payload = {
        'user_id': user_id,
        'exp': datetime.datetime.utcnow() + datetime.timedelta(days=7)
    }
    return jwt.encode(payload, app.config['SECRET_KEY'], algorithm='HS256')

def verify_token(token):
    """验证JWT token"""
    try:
        payload = jwt.decode(token, app.config['SECRET_KEY'], algorithms=['HS256'])
        return payload['user_id']
    except jwt.ExpiredSignatureError:
        return None
    except jwt.InvalidTokenError:
        return None

@app.route('/api/register', methods=['POST'])
def register():
    """用户注册"""
    try:
        data = request.get_json()
        username = data.get('username')
        password = data.get('password')
        email = data.get('email')
        
        if not username or not password or not email:
            return jsonify({'error': '用户名、密码和邮箱都是必填项'}), 400
        
        # 检查用户名是否已存在
        if User.query.filter_by(username=username).first():
            return jsonify({'error': '用户名已存在'}), 400
        
        # 检查邮箱是否已存在
        if User.query.filter_by(email=email).first():
            return jsonify({'error': '邮箱已被注册'}), 400
        
        # 创建新用户
        hashed_password = generate_password_hash(password)
        new_user = User(username=username, password=hashed_password, email=email)
        db.session.add(new_user)
        db.session.commit()
        
        # 生成token
        token = generate_token(new_user.id)
        
        return jsonify({
            'message': '注册成功',
            'token': token,
            'user_id': new_user.id,
            'username': new_user.username
        }), 201
        
    except Exception as e:
        return jsonify({'error': f'注册失败: {str(e)}'}), 500

@app.route('/api/login', methods=['POST'])
def login():
    """用户登录"""
    try:
        data = request.get_json()
        username = data.get('username')
        password = data.get('password')
        
        if not username or not password:
            return jsonify({'error': '用户名和密码都是必填项'}), 400
        
        user = User.query.filter_by(username=username).first()
        
        if user and check_password_hash(user.password, password):
            token = generate_token(user.id)
            return jsonify({
                'message': '登录成功',
                'token': token,
                'user_id': user.id,
                'username': user.username
            }), 200
        else:
            return jsonify({'error': '用户名或密码错误'}), 401
            
    except Exception as e:
        return jsonify({'error': f'登录失败: {str(e)}'}), 500

@app.route('/api/products', methods=['GET'])
def get_products():
    """获取商品列表"""
    try:
        page = request.args.get('page', 1, type=int)
        per_page = request.args.get('per_page', 10, type=int)
        category = request.args.get('category')
        search = request.args.get('search')
        
        query = Product.query.filter_by(status='available')
        
        if category:
            query = query.filter_by(category=category)
        
        if search:
            query = query.filter(Product.title.contains(search) | Product.description.contains(search))
        
        products = query.order_by(Product.created_at.desc()).paginate(
            page=page, per_page=per_page, error_out=False
        )
        
        product_list = []
        for product in products.items:
            product_list.append({
                'id': product.id,
                'title': product.title,
                'description': product.description,
                'price': product.price,
                'category': product.category,
                'condition': product.condition,
                'seller_id': product.seller_id,
                'seller_name': product.seller_name,
                'image_url': product.image_url,
                'created_at': product.created_at.isoformat()
            })
        
        return jsonify({
            'products': product_list,
            'total': products.total,
            'pages': products.pages,
            'current_page': page
        }), 200
        
    except Exception as e:
        return jsonify({'error': f'获取商品列表失败: {str(e)}'}), 500

@app.route('/api/products', methods=['POST'])
def create_product():
    """发布商品"""
    try:
        # 验证token
        auth_header = request.headers.get('Authorization')
        if not auth_header or not auth_header.startswith('Bearer '):
            return jsonify({'error': '需要认证token'}), 401
        
        token = auth_header.split(' ')[1]
        user_id = verify_token(token)
        if not user_id:
            return jsonify({'error': '无效的token'}), 401
        
        user = User.query.get(user_id)
        if not user:
            return jsonify({'error': '用户不存在'}), 404
        
        data = request.get_json()
        title = data.get('title')
        description = data.get('description')
        price = data.get('price')
        category = data.get('category')
        condition = data.get('condition')
        image_url = data.get('image_url', '')
        
        if not all([title, description, price, category, condition]):
            return jsonify({'error': '商品标题、描述、价格、分类和成色都是必填项'}), 400
        
        new_product = Product(
            title=title,
            description=description,
            price=float(price),
            category=category,
            condition=condition,
            seller_id=user_id,
            seller_name=user.username,
            image_url=image_url
        )
        
        db.session.add(new_product)
        db.session.commit()
        
        return jsonify({
            'message': '商品发布成功',
            'product_id': new_product.id
        }), 201
        
    except Exception as e:
        return jsonify({'error': f'发布商品失败: {str(e)}'}), 500

@app.route('/api/products/<int:product_id>', methods=['GET'])
def get_product(product_id):
    """获取商品详情"""
    try:
        product = Product.query.get(product_id)
        if not product:
            return jsonify({'error': '商品不存在'}), 404
        
        return jsonify({
            'id': product.id,
            'title': product.title,
            'description': product.description,
            'price': product.price,
            'category': product.category,
            'condition': product.condition,
            'seller_id': product.seller_id,
            'seller_name': product.seller_name,
            'image_url': product.image_url,
            'created_at': product.created_at.isoformat(),
            'status': product.status
        }), 200
        
    except Exception as e:
        return jsonify({'error': f'获取商品详情失败: {str(e)}'}), 500

@app.route('/api/products/<int:product_id>', methods=['PUT'])
def update_product(product_id):
    """更新商品信息"""
    try:
        # 验证token
        auth_header = request.headers.get('Authorization')
        if not auth_header or not auth_header.startswith('Bearer '):
            return jsonify({'error': '需要认证token'}), 401
        
        token = auth_header.split(' ')[1]
        user_id = verify_token(token)
        if not user_id:
            return jsonify({'error': '无效的token'}), 401
        
        product = Product.query.get(product_id)
        if not product:
            return jsonify({'error': '商品不存在'}), 404
        
        # 检查是否是商品卖家
        if product.seller_id != user_id:
            return jsonify({'error': '只有卖家可以修改商品信息'}), 403
        
        data = request.get_json()
        
        if 'title' in data:
            product.title = data['title']
        if 'description' in data:
            product.description = data['description']
        if 'price' in data:
            product.price = float(data['price'])
        if 'category' in data:
            product.category = data['category']
        if 'condition' in data:
            product.condition = data['condition']
        if 'image_url' in data:
            product.image_url = data['image_url']
        if 'status' in data:
            product.status = data['status']
        
        db.session.commit()
        
        return jsonify({'message': '商品信息更新成功'}), 200
        
    except Exception as e:
        return jsonify({'error': f'更新商品信息失败: {str(e)}'}), 500

@app.route('/api/products/<int:product_id>', methods=['DELETE'])
def delete_product(product_id):
    """删除商品"""
    try:
        # 验证token
        auth_header = request.headers.get('Authorization')
        if not auth_header or not auth_header.startswith('Bearer '):
            return jsonify({'error': '需要认证token'}), 401
        
        token = auth_header.split(' ')[1]
        user_id = verify_token(token)
        if not user_id:
            return jsonify({'error': '无效的token'}), 401
        
        product = Product.query.get(product_id)
        if not product:
            return jsonify({'error': '商品不存在'}), 404
        
        # 检查是否是商品卖家
        if product.seller_id != user_id:
            return jsonify({'error': '只有卖家可以删除商品'}), 403
        
        db.session.delete(product)
        db.session.commit()
        
        return jsonify({'message': '商品删除成功'}), 200
        
    except Exception as e:
        return jsonify({'error': f'删除商品失败: {str(e)}'}), 500

@app.route('/api/user/profile', methods=['GET'])
def get_user_profile():
    """获取用户信息"""
    try:
        # 验证token
        auth_header = request.headers.get('Authorization')
        if not auth_header or not auth_header.startswith('Bearer '):
            return jsonify({'error': '需要认证token'}), 401
        
        token = auth_header.split(' ')[1]
        user_id = verify_token(token)
        if not user_id:
            return jsonify({'error': '无效的token'}), 401
        
        user = User.query.get(user_id)
        if not user:
            return jsonify({'error': '用户不存在'}), 404
        
        return jsonify({
            'id': user.id,
            'username': user.username,
            'email': user.email,
            'created_at': user.created_at.isoformat()
        }), 200
        
    except Exception as e:
        return jsonify({'error': f'获取用户信息失败: {str(e)}'}), 500

@app.route('/api/user/products', methods=['GET'])
def get_user_products():
    """获取用户发布的商品"""
    try:
        # 验证token
        auth_header = request.headers.get('Authorization')
        if not auth_header or not auth_header.startswith('Bearer '):
            return jsonify({'error': '需要认证token'}), 401
        
        token = auth_header.split(' ')[1]
        user_id = verify_token(token)
        if not user_id:
            return jsonify({'error': '无效的token'}), 401
        
        products = Product.query.filter_by(seller_id=user_id).order_by(Product.created_at.desc()).all()
        
        product_list = []
        for product in products:
            product_list.append({
                'id': product.id,
                'title': product.title,
                'description': product.description,
                'price': product.price,
                'category': product.category,
                'condition': product.condition,
                'seller_name': product.seller_name,
                'image_url': product.image_url,
                'created_at': product.created_at.isoformat(),
                'status': product.status
            })
        
        return jsonify({'products': product_list}), 200
        
    except Exception as e:
        return jsonify({'error': f'获取用户商品失败: {str(e)}'}), 500

@app.route('/api/categories', methods=['GET'])
def get_categories():
    """获取商品分类列表"""
    categories = [
        '电子产品',
        '服装鞋帽',
        '图书音像',
        '家居用品',
        '运动户外',
        '美妆护肤',
        '食品饮料',
        '其他'
    ]
    return jsonify({'categories': categories}), 200

@app.route('/api/conditions', methods=['GET'])
def get_conditions():
    """获取商品成色列表"""
    conditions = [
        '全新',
        '九成新',
        '八成新',
        '七成新',
        '六成新',
        '五成新及以下'
    ]
    return jsonify({'conditions': conditions}), 200

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000) 