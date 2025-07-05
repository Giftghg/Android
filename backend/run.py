import os
from app import app, db

if __name__ == '__main__':
    # 确保上传目录存在
    upload_folder = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'uploads')
    if not os.path.exists(upload_folder):
        os.makedirs(upload_folder)
    
    # 创建数据库表
    with app.app_context():
        db.create_all()
    
    # 运行应用
    app.run(
        host='0.0.0.0',
        port=5000,
        debug=True
    ) 