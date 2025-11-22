<template>
  <div class="admin-container">
    <!-- 左侧导航 -->
    <div class="sidebar">
      <h2>后台管理</h2>
      <nav>
        <button
          @click="setView('products')"
          :class="{ active: currentView === 'products' }"
        >商品管理</button>
        <button
          @click="setView('orders')"
          :class="{ active: currentView === 'orders' }"
        >订单管理</button>
        <button
          @click="setView('users')"
          :class="{ active: currentView === 'users' }"
        >用户管理</button>
      </nav>
    </div>

    <!-- 内容区域 -->
    <div class="content">
      <!-- 商品管理 -->
      <div v-if="currentView === 'products'" class="products-management">
        <h3>商品管理</h3>
        <button class="add-button" @click="openAddProductForm">添加商品</button>
        <table>
          <thead>
            <tr>
              <th>商品名称</th>
              <th>图片</th>
              <th>价格</th>
              <th>库存</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="product in products" :key="product.id">
              <td>{{ product.name }}</td>
              <td>
                <div v-if="product.imgUrl" style="text-align: center;">
                  <img :src="getImageUrl(product.imgUrl)" alt="商品图片" style="width: 80px; height: 80px; object-fit: cover; display: block; margin: 0 auto; margin-left: -28px;">
                </div>
                <span v-else>无图片</span>
              </td>
              <td>¥{{ product.price }}</td>
              <td>{{ product.stock }}</td>
              <td>
                <button @click="editProduct(product)">编辑</button>
                <button @click="deleteProduct(product.id)" class="delete-button">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 订单管理 -->
      <div v-if="currentView === 'orders'" class="orders-management">
        <h3>订单管理</h3>
        <table>
          <thead>
            <tr>
              <th>订单号</th>
              <th>用户</th>
              <th>总金额</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="order in orders" :key="order.id">
              <td>{{ order.id }}</td>
              <td>{{ order.user ? order.user.username : '' }}</td>
              <td>¥{{ order.totalPrice }}</td>
              <td>{{ getStatusText(order.status) }}</td>
              <td>
                <button v-if="order.status === 'PAID'" @click="shipOrder(order.id)">发货</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 用户管理 -->
      <div v-if="currentView === 'users'" class="users-management">
        <h3>用户管理</h3>
        <table>
          <thead>
            <tr>
              <th>用户名</th>
              <th>邮箱</th>
              <th>角色</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in users" :key="user.id">
              <td>{{ user.username }}</td>
              <td>{{ user.email }}</td>
              <td>{{ user.role === 'ADMIN' ? '管理员' : '用户' }}</td>
              <td>
                <button @click="deleteUser(user.id)" class="delete-button">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 添加/编辑商品对话框 -->
    <div v-if="showAddProductForm" class="modal">
      <div class="modal-content">
        <h3>{{ editingProduct ? '编辑商品' : '添加商品' }}</h3>
        <form @submit.prevent="saveProduct">
          <div class="form-group">
            <label>商品名称：</label>
            <input v-model="productForm.name" required />
          </div>
          <div class="form-group">
            <label>价格：</label>
            <input type="number" v-model="productForm.price" min="0" step="0.01" required />
          </div>
          <div class="form-group">
            <label>库存：</label>
            <input type="number" v-model="productForm.stock" min="0" required />
          </div>

          <div class="form-group">
            <label>商品图片：</label>
            <div class="image-upload">
              <!-- 图片预览 -->
              <div v-if="productForm.imgUrl" class="image-preview">
                <img :src="getImageUrl(productForm.imgUrl)" alt="商品图片">
                <button type="button" class="remove-image" @click="removeImage">×</button>
              </div>
              <!-- 上传按钮 -->
              <div v-else class="upload-placeholder">
                <input 
                  type="file" 
                  ref="fileInput" 
                  @change="handleFileChange" 
                  accept="image/*"
                  style="display: none;"
                />
                <button type="button" class="upload-btn" @click="$refs.fileInput.click()">
                  <span class="upload-icon">📷</span>
                  <span>点击上传图片</span>
                </button>
              </div>
              <!-- 上传中状态 -->
              <div v-if="uploading" class="uploading">上传中...</div>
            </div>
          </div>
          <div class="modal-buttons">
            <button type="submit">保存</button>
            <button type="button" @click="showAddProductForm = false">取消</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { adminApi, fileApi } from '@/utils/api';
import { getImageUrl, getStatusText, showError, showSuccess } from '@/utils/helpers';

export default {
  name: 'Admin',
  data() {
    return {
      currentView: 'products',
      products: [],
      orders: [],
      users: [],
      showAddProductForm: false,
      editingProduct: null,
      productForm: { name: '', price: 0, stock: 0, imgUrl: '' },
      uploading: false
    }
  },
  async created() {
    await this.fetchData()
  },
  methods: {
    getStatusText,
    getImageUrl,
    
    // 打开添加商品表单
    openAddProductForm() {
      this.editingProduct = null
      this.productForm = { name: '', price: 0, stock: 0, imgUrl: '' }
      this.showAddProductForm = true
    },
    
    async setView(view) {
      this.currentView = view
      await this.fetchData()
    },
    
    async fetchData() {
      if (this.currentView === 'products') await this.fetchProducts()
      else if (this.currentView === 'orders') await this.fetchOrders()
      else if (this.currentView === 'users') await this.fetchUsers()
    },

    // 商品管理
    async fetchProducts() {
      try {
        const res = await adminApi.getProducts()
        this.products = res.data.data
      } catch (err) {
        showError(err, '获取商品列表失败')
      }
    },
    
    editProduct(product) {
      this.editingProduct = product
      this.productForm = { 
        ...product,
        imgUrl: product.imgUrl || '' 
      }
      this.showAddProductForm = true
    },
    
    async handleFileChange(event) {
      const file = event.target.files[0]
      if (!file) return

      // 检查文件类型
      if (!file.type.startsWith('image/')) {
        showError({ message: '请选择图片文件' })
        return
      }

      // 检查文件大小（5MB）
      if (file.size > 5 * 1024 * 1024) {
        showError({ message: '图片大小不能超过 5MB' })
        return
      }

      try {
        this.uploading = true
        const res = await fileApi.upload(file)
        this.productForm.imgUrl = res.data.data
        showSuccess('图片上传成功')
      } catch (err) {
        showError(err, '图片上传失败')
      } finally {
        this.uploading = false
        // 重置文件输入
        if (this.$refs.fileInput) {
          this.$refs.fileInput.value = ''
        }
      }
    },
    
    removeImage() {
      this.productForm.imgUrl = ''
    },
    
    async saveProduct() {
      try {
        // 准备要发送的数据，确保使用 imgUrl 字段
        const productData = {
          name: this.productForm.name,
          price: this.productForm.price,
          stock: this.productForm.stock,
          imgUrl: this.productForm.imgUrl || ''
        }
        
        if (this.editingProduct) {
          await adminApi.updateProduct(this.editingProduct.id, productData)
        } else {
          await adminApi.createProduct(productData)
        }
        this.showAddProductForm = false
        this.editingProduct = null
        this.productForm = { name: '', price: 0, stock: 0, imgUrl: '' }
        await this.fetchProducts()
        showSuccess('商品保存成功')
      } catch (err) {
        showError(err, '保存商品失败')
      }
    },
    
    async deleteProduct(id) {
      if (!confirm('确定要删除这个商品吗？')) return
      try {
        await adminApi.deleteProduct(id)
        await this.fetchProducts()
      } catch (err) {
        showError(err, '删除商品失败')
      }
    },

    // 订单管理
    async fetchOrders() {
      try {
        const res = await adminApi.getOrders()
        this.orders = res.data.data
      } catch (err) {
        showError(err, '获取订单列表失败')
      }
    },
    
    async shipOrder(orderId) {
      try {
        await adminApi.shipOrder(orderId)
        await this.fetchOrders()
      } catch (err) {
        showError(err, '发货失败')
      }
    },

    // 用户管理
    async fetchUsers() {
      try {
        const res = await adminApi.getUsers()
        this.users = res.data.data
      } catch (err) {
        showError(err, '获取用户列表失败')
      }
    },
    
    async deleteUser(userId) {
      if (!confirm('确定要删除这个用户吗？')) return
      try {
        await adminApi.deleteUser(userId)
        await this.fetchUsers()
      } catch (err) {
        showError(err, '删除用户失败')
      }
    }
  }
}
</script>

<style scoped>
.admin-container {
  display: flex;
  min-height: 100vh;
  font-family: Arial, sans-serif;
  background-color: #f9f9f9;
}

/* 左侧导航 */
.sidebar {
  width: 200px;
  background-color: #f5f5f5;
  padding: 20px;
}

.sidebar nav {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.sidebar button {
  width: 100%;
  padding: 14px;
  text-align: left;
  border: none;
  background-color: #ffffff;
  color: #34495e;
  cursor: pointer;
  font-size: 15px;
  font-weight: 500;
  border-radius: 8px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.sidebar button:hover {
  background-color: #f8f9fa;
  color: #3498db;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.sidebar button.active {
  background: linear-gradient(135deg, #3498db, #64b5f6);
  color: white;
  font-weight: 600;
  padding: 14px;
  box-shadow: 0 4px 15px rgba(52, 152, 219, 0.4);
  transform: translateY(-1px);
}

/* 内容区域 */
.content {
  flex: 1;
  padding: 20px;
  overflow-x: auto;
}

/* 表格样式 */
table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
  background-color: #fff;
  border-radius: 6px;
  overflow: hidden;
}

th, td {
  padding: 12px 15px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

th {
  background-color: #f5f5f5;
  font-weight: 600;
}

tr:hover {
  background-color: #f1f1f1;
}

/* 按钮样式 */
.add-button {
  background: linear-gradient(135deg, #3498db, #64b5f6);
  color: white;
  padding: 10px 20px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  margin-bottom: 10px;
  transition: all 0.3s ease;
  font-weight: 500;
  box-shadow: 0 3px 10px rgba(52, 152, 219, 0.3);
}

.add-button:hover {
  background: linear-gradient(135deg, #2980b9, #42a5f5);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.4);
}

.delete-button {
  background: linear-gradient(135deg, #e74c3c, #ff7675);
  color: white;
  padding: 8px 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 500;
  box-shadow: 0 2px 8px rgba(231, 76, 60, 0.3);
}

.delete-button:hover {
  background: linear-gradient(135deg, #c0392b, #e17055);
  transform: translateY(-1px);
  box-shadow: 0 3px 10px rgba(231, 76, 60, 0.4);
}

button {
  padding: 8px 16px;
  margin: 0 5px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
  font-weight: 500;
  background: linear-gradient(135deg, #64b5f6, #90caf9);
  color: white;
  box-shadow: 0 2px 8px rgba(100, 181, 246, 0.3);
}

button:hover {
  background: linear-gradient(135deg, #42a5f5, #64b5f6);
  transform: translateY(-1px);
  box-shadow: 0 3px 10px rgba(100, 181, 246, 0.4);
}

/* 弹窗样式 */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: #fff;
  padding: 25px 30px;
  border-radius: 8px;
  width: 500px;
  max-width: 90%;
  box-shadow: 0 4px 12px rgba(0,0,0,0.2);
}

/* 表单 */
.form-group {
  margin-bottom: 15px;
}

.image-upload {
  border: 2px dashed #ddd;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  background-color: #fafafa;
  transition: all 0.3s ease;
}

.image-upload:hover {
  border-color: #4CAF50;
  background-color: #f5f5f5;
}

.image-preview {
  position: relative;
  display: inline-block;
}

.image-preview img {
  max-width: 200px;
  max-height: 200px;
  border-radius: 8px;
  object-fit: contain;
}

.remove-image {
  position: absolute;
  top: -10px;
  right: -10px;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: #e74c3c;
  color: white;
  border: none;
  font-size: 20px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
  line-height: 1;
  transition: all 0.3s ease;
}

.remove-image:hover {
  background: #c0392b;
  transform: scale(1.1);
}

.upload-placeholder {
  padding: 30px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.upload-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 20px 40px;
  background: linear-gradient(135deg, #4CAF50, #66BB6A);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 16px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(76, 175, 80, 0.3);
}

.upload-btn:hover {
  background: linear-gradient(135deg, #45a049, #4CAF50);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(76, 175, 80, 0.4);
}

.upload-icon {
  font-size: 32px;
}

.uploading {
  margin-top: 10px;
  color: #4CAF50;
  font-weight: 500;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  font-weight: 500;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 8px 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  transition: all 0.3s ease;
}

.form-group input:focus,
.form-group textarea:focus {
  border-color: #3498db;
  outline: none;
  box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
  transform: translateY(-1px);
}

.modal-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}
</style>

