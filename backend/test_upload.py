#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
测试图片上传功能
"""

import requests
import os

def test_upload_image():
    """测试图片上传"""
    url = "http://localhost:5000/api/upload_image"
    
    # 创建一个测试图片文件
    test_image_path = "test_image.jpg"
    with open(test_image_path, "wb") as f:
        # 创建一个简单的测试图片（1x1像素的JPEG）
        f.write(b'\xff\xd8\xff\xe0\x00\x10JFIF\x00\x01\x01\x01\x00H\x00H\x00\x00\xff\xdb\x00C\x00\x08\x06\x06\x07\x06\x05\x08\x07\x07\x07\t\t\x08\n\x0c\x14\r\x0c\x0b\x0b\x0c\x19\x12\x13\x0f\x14\x1d\x1a\x1f\x1e\x1d\x1a\x1c\x1c $.\' ",#\x1c\x1c(7),01444\x1f\'9=82<.342\xff\xc0\x00\x11\x08\x00\x01\x00\x01\x01\x01\x11\x00\x02\x11\x01\x03\x11\x01\xff\xc4\x00\x14\x00\x01\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x08\xff\xc4\x00\x14\x10\x01\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\xff\xda\x00\x0c\x03\x01\x00\x02\x11\x03\x11\x00\x3f\x00\xaa\xff\xd9')
    
    try:
        # 上传图片
        with open(test_image_path, "rb") as f:
            files = {"image": f}
            response = requests.post(url, files=files)
        
        print(f"响应状态码: {response.status_code}")
        print(f"响应内容: {response.text}")
        
        if response.status_code == 200:
            data = response.json()
            print(f"图片上传成功!")
            print(f"图片URL: {data.get('image_url')}")
            print(f"文件名: {data.get('filename')}")
        else:
            print("图片上传失败")
            
    except Exception as e:
        print(f"测试失败: {e}")
    
    finally:
        # 清理测试文件
        if os.path.exists(test_image_path):
            os.remove(test_image_path)

if __name__ == "__main__":
    print("开始测试图片上传功能...")
    test_upload_image() 