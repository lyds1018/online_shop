<template>
  <div class="products-container">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <input
        type="text"
        v-model="searchQuery"
        placeholder="搜索商品..."
        @input="debouncedSearch"
      />
    </div>

    <!-- 商品列表 -->
    <div class="products-grid">
      <div v-for="p in products" :key="p.id" class="product-card">
        <!-- 添加调试信息 -->
        <div class="product-image">
          <!-- 简化图片显示逻辑，直接尝试显示 -->
          <img v-if="p.imgUrl" 
               :src="getImageUrl(p.imgUrl)" 
               :alt="p.name" 
               style="width: 120px; height: 120px; object-fit: cover;">
          <div v-else class="no-image">暂无图片</div>
        </div>
        <h3>{{ p.name }}</h3>
        <p class="price">{{ p.price }}</p>
        <p class="stock">库存: {{ p.stock }}</p>

        <button @click="addToCart(p)" :disabled="p.stock <= 0">
          {{ p.stock > 0 ? '加入购物车' : '已售罄' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { cartApi, productApi } from '@/utils/api';
import { debounce, getImageUrl, showError, showSuccess } from '@/utils/helpers';

export default {
  name: 'Products',
  data() {
    return {
      products: [],
      searchQuery: ''
    }
  },
  async created() {
    await this.fetchProducts()
  },
  methods: {
    getImageUrl,
    
    async fetchProducts() {
      try {
        const res = await productApi.getAll()
        this.products = res.data.data
      } catch (err) {
        showError(err, '获取商品失败')
      }
    },

    debouncedSearch: debounce(function() {
      this.searchProducts()
    }, 400),

    async searchProducts() {
      if (!this.searchQuery) {
        this.fetchProducts()
        return
      }

      try {
        const res = await productApi.search(this.searchQuery)
        this.products = res.data.data
      } catch (err) {
        showError(err, '搜索失败')
      }
    },

    async addToCart(product) {
      try {
        await cartApi.add(product.id, 1)
        showSuccess('已加入购物车！')
      } catch (err) {
        showError(err, '加入购物车失败')
      }
    }
  }
}
</script>

<style scoped>
.products-container {
  padding: 0;
  position: relative;
}

/* 搜索栏样式优化 */
.search-bar {
  margin-bottom: 32px;
  text-align: center;
  position: relative;
}

.search-bar input {
  width: 100%;
  max-width: 600px;
  padding: 14px 20px;
  border: 2px solid #e0e0e0;
  border-radius: 50px;
  font-size: 16px;
  transition: all 0.3s ease;
  outline: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.search-bar input:focus {
  border-color: #3498db;
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.2);
  transform: translateY(-1px);
}

.search-bar input::placeholder {
  color: #95a5a6;
  font-size: 14px;
}

/* 商品网格布局 */
.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 32px;
}

/* 商品卡片样式优化 */
.product-card {
  border: none;
  border-radius: 12px;
  padding: 24px;
  background: #ffffff;
  text-align: center;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.product-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 4px;
  background: linear-gradient(90deg, #3498db, #64b5f6);
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.product-card:hover::before {
  transform: scaleX(1);
}

/* 商品标题样式 */
.product-card h3 {
  margin: 20px 0 15px;
  font-size: 1.3em;
  font-weight: 600;
  color: #2c3e50;
  line-height: 1.3;
  min-height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 商品图片容器样式 */
.product-image {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 160px;
  margin-bottom: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  overflow: hidden;
  transition: background-color 0.3s ease;
}

.product-card:hover .product-image {
  background: #f0f2f5;
}

.product-image img {
  width: 140px !important;
  height: 140px !important;
  object-fit: contain !important;
  transition: transform 0.3s ease;
}

.product-card:hover .product-image img {
  transform: scale(1.05);
}

/* 无图片占位符样式 */
.no-image {
  width: 140px;
  height: 140px;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f5f5;
  color: #95a5a6;
  font-size: 16px;
  font-weight: 500;
  border-radius: 8px;
  border: 2px dashed #e0e0e0;
}

/* 价格样式 */
.price {
  color: #e74c3c;
  font-size: 1.4em;
  font-weight: 700;
  margin: 10px 0;
  position: relative;
  display: inline-block;
}

.price::before {
  content: '¥';
  font-size: 0.9em;
  margin-right: 2px;
}

/* 库存样式 */
.stock {
  color: #7f8c8d;
  margin: 8px 0 20px;
  font-size: 0.95em;
}

/* 按钮样式优化 */
button {
  background: linear-gradient(135deg, #3498db, #64b5f6);
  color: #fff;
  border: none;
  padding: 12px 24px;
  border-radius: 50px;
  cursor: pointer;
  font-size: 15px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.3);
  width: 100%;
  position: relative;
  overflow: hidden;
}

button::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  transform: translate(-50%, -50%);
  transition: width 0.6s, height 0.6s;
}

button:hover::before {
  width: 300px;
  height: 300px;
}

button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(52, 152, 219, 0.4);
  background: linear-gradient(135deg, #2980b9, #42a5f5);
}

button:active:not(:disabled) {
  transform: translateY(0);
}

button:disabled {
  background: #95a5a6;
  cursor: not-allowed;
  box-shadow: none;
  transform: none;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
    gap: 20px;
  }
  
  .product-card {
    padding: 16px;
  }
  
  .product-card h3 {
    font-size: 1.1em;
    min-height: 40px;
  }
  
  .product-image {
    height: 140px;
  }
  
  .product-image img,
  .no-image {
    width: 120px !important;
    height: 120px !important;
  }
  
  button {
    padding: 10px 20px;
    font-size: 14px;
  }
}

@media (max-width: 480px) {
  .products-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .search-bar input {
    padding: 12px 16px;
    font-size: 14px;
  }
}
</style>