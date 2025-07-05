from app import app, db, User, Product
import datetime

def init_database():
    """初始化数据库"""
    with app.app_context():
        # 删除所有表
        db.drop_all()
        # 创建所有表
        db.create_all()
        print("数据库初始化完成")

def create_sample_data():
    """创建示例数据"""
    with app.app_context():
        # 创建示例用户
        users = [
            User(username='user1', password='123456', email='user1@example.com'),
            User(username='user2', password='123456', email='user2@example.com'),
            User(username='user3', password='123456', email='user3@example.com')
        ]
        
        for user in users:
            db.session.add(user)
        db.session.commit()
        
        # 创建示例商品
        products = [
            Product(
                title='iPhone 12',
                description='九成新iPhone 12，无划痕，配件齐全',
                price=3999.0,
                category='电子产品',
                condition='九成新',
                seller_id=1,
                seller_name='user1',
                image_url='https://example.com/iphone12.jpg'
            ),
            Product(
                title='MacBook Pro 2020',
                description='八成新MacBook Pro，性能良好',
                price=6999.0,
                category='电子产品',
                condition='八成新',
                seller_id=2,
                seller_name='user2',
                image_url='https://example.com/macbook.jpg'
            ),
            Product(
                title='Nike 运动鞋',
                description='全新Nike运动鞋，尺码42',
                price=299.0,
                category='服装鞋帽',
                condition='全新',
                seller_id=3,
                seller_name='user3',
                image_url='https://example.com/nike.jpg'
            )
        ]
        
        for product in products:
            db.session.add(product)
        db.session.commit()
        
        print("示例数据创建完成")

def show_database_info():
    """显示数据库信息"""
    with app.app_context():
        user_count = User.query.count()
        product_count = Product.query.count()
        
        print(f"数据库信息:")
        print(f"用户数量: {user_count}")
        print(f"商品数量: {product_count}")
        
        if user_count > 0:
            print("\n用户列表:")
            users = User.query.all()
            for user in users:
                print(f"  ID: {user.id}, 用户名: {user.username}, 邮箱: {user.email}")
        
        if product_count > 0:
            print("\n商品列表:")
            products = Product.query.all()
            for product in products:
                print(f"  ID: {product.id}, 标题: {product.title}, 价格: {product.price}, 卖家: {product.seller_name}")

def clear_database():
    """清空数据库"""
    with app.app_context():
        db.drop_all()
        db.create_all()
        print("数据库已清空")

def main():
    """主函数"""
    print("=== 二手交易平台数据库管理工具 ===\n")
    print("1. 初始化数据库")
    print("2. 创建示例数据")
    print("3. 显示数据库信息")
    print("4. 清空数据库")
    print("5. 退出")
    
    while True:
        choice = input("\n请选择操作 (1-5): ").strip()
        
        if choice == '1':
            init_database()
        elif choice == '2':
            create_sample_data()
        elif choice == '3':
            show_database_info()
        elif choice == '4':
            confirm = input("确定要清空数据库吗？(y/N): ").strip().lower()
            if confirm == 'y':
                clear_database()
            else:
                print("操作已取消")
        elif choice == '5':
            print("退出程序")
            break
        else:
            print("无效选择，请重新输入")

if __name__ == '__main__':
    main() 