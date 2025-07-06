#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
Flask后端启动脚本
确保正确监听所有网络接口，允许Android模拟器访问
"""

from app import app

if __name__ == '__main__':
    print("启动Flask后端服务器...")
    print("服务器地址: http://0.0.0.0:5000")
    print("API文档: http://0.0.0.0:5000/api")
    print("图片上传接口: http://0.0.0.0:5000/api/upload_image")
    print("图片访问地址: http://0.0.0.0:5000/uploads/")
    print("Android模拟器访问地址: http://10.0.2.2:5000")
    print("按 Ctrl+C 停止服务器")
    
    # 启动Flask应用，监听所有网络接口
    app.run(
        debug=True,           # 开启调试模式
        host='0.0.0.0',      # 监听所有网络接口
        port=5000,           # 端口5000
        threaded=True        # 启用多线程
    ) 