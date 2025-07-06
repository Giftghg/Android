#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
测试Flask后端是否正常工作
"""

import requests
import json

def test_flask_backend():
    """测试Flask后端"""
    base_url = "http://localhost:5000"
    
    print("=== Flask后端测试 ===")
    
    # 测试1: 获取商品列表
    print("\n1. 测试获取商品列表...")
    try:
        response = requests.get(f"{base_url}/api/products", timeout=5)
        print(f"响应码: {response.status_code}")
        if response.status_code == 200:
            data = response.json()
            print(f"商品数量: {len(data.get('products', []))}")
            for product in data.get('products', [])[:3]:  # 只显示前3个商品
                print(f"  - {product.get('title', 'N/A')} (ID: {product.get('id', 'N/A')})")
                print(f"    图片URL: {product.get('image_url', 'N/A')}")
        else:
            print(f"响应内容: {response.text}")
    except Exception as e:
        print(f"测试失败: {e}")
    
    # 测试2: 测试图片上传接口
    print("\n2. 测试图片上传接口...")
    try:
        # 创建一个简单的测试图片
        test_image_data = b'\xff\xd8\xff\xe0\x00\x10JFIF\x00\x01\x01\x01\x00H\x00H\x00\x00\xff\xdb\x00C\x00\x08\x06\x06\x07\x06\x05\x08\x07\x07\x07\t\t\x08\n\x0c\x14\r\x0c\x0b\x0b\x0c\x19\x12\x13\x0f\x14\x1d\x1a\x1f\x1e\x1d\x1a\x1c\x1c $.\' ",#\x1c\x1c(7),01444\x1f\'9=82<.342\xff\xc0\x00\x11\x08\x00\x01\x00\x01\x01\x01\x11\x00\x02\x11\x01\x03\x11\x01\xff\xc4\x00\x14\x00\x01\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x08\xff\xc4\x00\x14\x10\x01\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\xff\xda\x00\x0c\x03\x01\x00\x02\x11\x03\x11\x00\x3f\x00\xaa\xff\xd9'
        
        files = {'image': ('test.jpg', test_image_data, 'image/jpeg')}
        response = requests.post(f"{base_url}/api/upload_image", files=files, timeout=10)
        print(f"响应码: {response.status_code}")
        if response.status_code == 200:
            data = response.json()
            print(f"上传成功!")
            print(f"图片URL: {data.get('image_url', 'N/A')}")
            print(f"文件名: {data.get('filename', 'N/A')}")
        else:
            print(f"响应内容: {response.text}")
    except Exception as e:
        print(f"测试失败: {e}")
    
    # 测试3: 测试图片访问
    print("\n3. 测试图片访问...")
    try:
        # 先上传一张图片
        test_image_data = b'\xff\xd8\xff\xe0\x00\x10JFIF\x00\x01\x01\x01\x00H\x00H\x00\x00\xff\xdb\x00C\x00\x08\x06\x06\x07\x06\x05\x08\x07\x07\x07\t\t\x08\n\x0c\x14\r\x0c\x0b\x0b\x0c\x19\x12\x13\x0f\x14\x1d\x1a\x1f\x1e\x1d\x1a\x1c\x1c $.\' ",#\x1c\x1c(7),01444\x1f\'9=82<.342\xff\xc0\x00\x11\x08\x00\x01\x00\x01\x01\x01\x11\x00\x02\x11\x01\x03\x11\x01\xff\xc4\x00\x14\x00\x01\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x08\xff\xc4\x00\x14\x10\x01\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\xff\xda\x00\x0c\x03\x01\x00\x02\x11\x03\x11\x00\x3f\x00\xaa\xff\xd9'
        files = {'image': ('test.jpg', test_image_data, 'image/jpeg')}
        upload_response = requests.post(f"{base_url}/api/upload_image", files=files, timeout=10)
        
        if upload_response.status_code == 200:
            upload_data = upload_response.json()
            image_url = upload_data.get('image_url')
            filename = upload_data.get('filename')
            
            if image_url and filename:
                # 测试直接访问图片
                response = requests.get(f"{base_url}/uploads/{filename}", timeout=5)
                print(f"图片访问响应码: {response.status_code}")
                if response.status_code == 200:
                    print("图片访问成功!")
                else:
                    print("图片访问失败")
            else:
                print("无法获取图片URL")
        else:
            print("图片上传失败，无法测试访问")
    except Exception as e:
        print(f"测试失败: {e}")
    
    print("\n=== 测试完成 ===")

if __name__ == "__main__":
    test_flask_backend() 