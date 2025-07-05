import requests
import json

# 服务器地址
BASE_URL = 'http://localhost:5000/api'

def test_register():
    """测试用户注册"""
    url = f'{BASE_URL}/register'
    data = {
        'username': 'testuser',
        'password': '123456',
        'email': 'test@example.com'
    }
    response = requests.post(url, json=data)
    print('注册测试:', response.status_code, response.json())
    return response.json()

def test_login():
    """测试用户登录"""
    url = f'{BASE_URL}/login'
    data = {
        'username': 'testuser',
        'password': '123456'
    }
    response = requests.post(url, json=data)
    print('登录测试:', response.status_code, response.json())
    return response.json()

def test_create_product(token):
    """测试发布商品"""
    url = f'{BASE_URL}/products'
    headers = {'Authorization': f'Bearer {token}'}
    data = {
        'title': 'iPhone 12',
        'description': '九成新iPhone 12，无划痕，配件齐全',
        'price': 3999.0,
        'category': '电子产品',
        'condition': '九成新',
        'image_url': 'https://example.com/iphone12.jpg'
    }
    response = requests.post(url, json=data, headers=headers)
    print('发布商品测试:', response.status_code, response.json())
    return response.json()

def test_get_products():
    """测试获取商品列表"""
    url = f'{BASE_URL}/products'
    response = requests.get(url)
    print('获取商品列表测试:', response.status_code, response.json())
    return response.json()

def test_get_product(product_id):
    """测试获取商品详情"""
    url = f'{BASE_URL}/products/{product_id}'
    response = requests.get(url)
    print('获取商品详情测试:', response.status_code, response.json())
    return response.json()

def test_get_user_profile(token):
    """测试获取用户信息"""
    url = f'{BASE_URL}/user/profile'
    headers = {'Authorization': f'Bearer {token}'}
    response = requests.get(url, headers=headers)
    print('获取用户信息测试:', response.status_code, response.json())
    return response.json()

def test_get_user_products(token):
    """测试获取用户发布的商品"""
    url = f'{BASE_URL}/user/products'
    headers = {'Authorization': f'Bearer {token}'}
    response = requests.get(url, headers=headers)
    print('获取用户商品测试:', response.status_code, response.json())
    return response.json()

def test_get_categories():
    """测试获取商品分类"""
    url = f'{BASE_URL}/categories'
    response = requests.get(url)
    print('获取分类测试:', response.status_code, response.json())
    return response.json()

def test_get_conditions():
    """测试获取商品成色"""
    url = f'{BASE_URL}/conditions'
    response = requests.get(url)
    print('获取成色测试:', response.status_code, response.json())
    return response.json()

def main():
    """运行所有测试"""
    print('=== 二手交易平台API测试 ===\n')
    
    # 1. 测试注册
    print('1. 测试用户注册')
    register_result = test_register()
    
    # 2. 测试登录
    print('\n2. 测试用户登录')
    login_result = test_login()
    token = login_result.get('token')
    
    if token:
        # 3. 测试发布商品
        print('\n3. 测试发布商品')
        product_result = test_create_product(token)
        product_id = product_result.get('product_id')
        
        # 4. 测试获取商品列表
        print('\n4. 测试获取商品列表')
        test_get_products()
        
        if product_id:
            # 5. 测试获取商品详情
            print('\n5. 测试获取商品详情')
            test_get_product(product_id)
        
        # 6. 测试获取用户信息
        print('\n6. 测试获取用户信息')
        test_get_user_profile(token)
        
        # 7. 测试获取用户商品
        print('\n7. 测试获取用户商品')
        test_get_user_products(token)
    
    # 8. 测试获取分类
    print('\n8. 测试获取商品分类')
    test_get_categories()
    
    # 9. 测试获取成色
    print('\n9. 测试获取商品成色')
    test_get_conditions()
    
    print('\n=== 测试完成 ===')

if __name__ == '__main__':
    main() 