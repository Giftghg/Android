#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
数据库清理脚本
清理包含content URI的旧商品数据
"""

import sqlite3
import os

def clean_database():
    """清理数据库中的旧数据"""
    db_path = os.path.join('instance', 'secondhand.db')
    
    if not os.path.exists(db_path):
        print("数据库文件不存在")
        return
    
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    
    try:
        # 查看当前商品数据
        print("=== 当前商品数据 ===")
        cursor.execute("SELECT id, title, image_url FROM product")
        products = cursor.fetchall()
        
        for product in products:
            print(f"ID: {product[0]}, 标题: {product[1]}, 图片URL: {product[2]}")
        
        # 查找包含content URI的商品
        cursor.execute("SELECT id, title FROM product WHERE image_url LIKE 'content://%'")
        old_products = cursor.fetchall()
        
        if old_products:
            print(f"\n=== 发现 {len(old_products)} 个包含content URI的商品 ===")
            for product in old_products:
                print(f"ID: {product[0]}, 标题: {product[1]}")
            
            # 询问是否删除
            response = input("\n是否删除这些旧商品数据？(y/n): ")
            if response.lower() == 'y':
                cursor.execute("DELETE FROM product WHERE image_url LIKE 'content://%'")
                conn.commit()
                print(f"已删除 {len(old_products)} 个旧商品")
            else:
                print("取消删除操作")
        else:
            print("\n没有发现包含content URI的商品数据")
        
        # 显示清理后的数据
        print("\n=== 清理后的商品数据 ===")
        cursor.execute("SELECT id, title, image_url FROM product")
        products = cursor.fetchall()
        
        for product in products:
            print(f"ID: {product[0]}, 标题: {product[1]}, 图片URL: {product[2]}")
            
    except Exception as e:
        print(f"清理数据库时出错: {e}")
    finally:
        conn.close()

if __name__ == '__main__':
    clean_database() 