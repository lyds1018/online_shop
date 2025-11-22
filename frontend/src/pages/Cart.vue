<template>
  <div class="cart-container">
    <h2>购物车</h2>

    <!-- 空购物车 -->
    <div v-if="items.length === 0" class="empty-cart">
      <p>购物车是空的</p>
      <router-link to="/products" class="continue-shopping">继续购物</router-link>
    </div>

    <!-- 有商品 -->
    <div v-else>
      <div class="cart-items">
        <div v-for="item in items" :key="item.id" class="cart-item">

          <div class="item-details">
            <h3>{{ item.product.name }}</h3>
            <p class="price">¥{{ item.product.price }}</p>
          </div>

          <div class="quantity-controls">
            <button
              @click="changeQty(item, item.quantity - 1)"
              :disabled="item.quantity <= 1"
            >-</button>

            <span>{{ item.quantity }}</span>

            <button
              @click="changeQty(item, item.quantity + 1)"
              :disabled="item.quantity >= item.product.stock"
            >+</button>
          </div>

          <button class="remove-button" @click="removeItem(item.id)">删除</button>
        </div>
      </div>

      <div class="cart-summary">
        <div class="total">
          <span>总计：</span>
          <span class="total-price">¥{{ total }}</span>
        </div>
        <button class="checkout-button" @click="checkout">结算</button>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'Cart',

  data() {
    return {
      items: []
    }
  },

  computed: {
    total() {
      return this.items
        .reduce((sum, i) => sum + i.product.price * i.quantity, 0)
        .toFixed(2)
    }
  },

  async created() {
    this.loadCart()
  },

  methods: {
    async loadCart() {
      try {
        const res = await axios.get('/api/cart')
        this.items = res.data.data
      } catch (err) {
        alert('获取购物车失败：' + (err.response?.data?.message || err.message))
      }
    },

    async changeQty(item, qty) {
      try {
        await axios.put(`/api/cart/items/${item.id}`, { quantity: qty })
        this.loadCart()
      } catch (err) {
        alert('更新数量失败：' + (err.response?.data?.message || err.message))
      }
    },

    async removeItem(id) {
      if (!confirm('确定删除该商品吗？')) return
      try {
        await axios.delete(`/api/cart/items/${id}`)
        this.loadCart()
      } catch (err) {
        alert('删除失败：' + (err.response?.data?.message || err.message))
      }
    },

    async checkout() {
      try {
        const res = await axios.post('/api/orders/create')
        this.$router.push(`/orders/${res.data.data.id}`)
      } catch (err) {
        alert('创建订单失败：' + (err.response?.data?.message || err.message))
      }
    }
  }
}
</script>

<style scoped>
/* 主容器样式 */
.cart-container {
  padding: 0;
  max-width: 1000px;
  margin: 0 auto;
  min-height: calc(100vh - 100px);
  display: flex;
  flex-direction: column;
}

/* 标题样式 */
.cart-container h2 {
  font-size: 2em;
  color: #2c3e50;
  margin-bottom: 40px;
  font-weight: 700;
  position: relative;
  padding-bottom: 12px;
}

.cart-container h2::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 80px;
  height: 4px;
  background: linear-gradient(90deg, #3498db, #2ecc71);
  border-radius: 2px;
}

/* 空购物车样式 */
.empty-cart {
  text-align: center;
  padding: 80px 20px;
  background: #f8f9fa;
  border-radius: 12px;
  margin-bottom: 30px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.empty-cart p {
  font-size: 1.3em;
  color: #7f8c8d;
  margin-bottom: 30px;
}

.continue-shopping {
  display: inline-block;
  padding: 14px 32px;
  background: linear-gradient(135deg, #3498db, #2980b9);
  color: white;
  text-decoration: none;
  border-radius: 50px;
  font-size: 16px;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.3);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.continue-shopping:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(52, 152, 219, 0.4);
  background: linear-gradient(135deg, #2980b9, #3498db);
}

/* 购物车列表样式 */
.cart-items {
  margin-bottom: 30px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  background: white;
}

/* 单个购物车项目样式 */
.cart-item {
  display: grid;
  grid-template-columns: 1fr auto auto;
  gap: 30px;
  align-items: center;
  padding: 24px;
  border-bottom: 1px solid #f0f0f0;
  transition: all 0.3s ease;
  position: relative;
}

.cart-item:last-child {
  border-bottom: none;
}

.cart-item:hover {
  background-color: #fafafa;
}

.cart-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 0;
  background: linear-gradient(180deg, #3498db, #2ecc71);
  transition: height 0.3s ease;
  border-radius: 0 2px 2px 0;
}

.cart-item:hover::before {
  height: 80%;
}

/* 商品详情样式 */
.item-details h3 {
  margin: 0 0 8px 0;
  font-size: 1.3em;
  color: #2c3e50;
  font-weight: 600;
}

.price {
  color: #e74c3c;
  font-weight: bold;
  font-size: 1.2em;
  display: inline-block;
  position: relative;
}

.price::before {
  content: '¥';
  font-size: 0.9em;
  margin-right: 2px;
}

/* 数量控制样式 */
.quantity-controls {
  display: flex;
  align-items: center;
  gap: 0;
  background: #f8f9fa;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}

.quantity-controls button {
  width: 40px;
  height: 40px;
  border: none;
  background-color: #ffffff;
  cursor: pointer;
  font-size: 18px;
  font-weight: bold;
  color: #2c3e50;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.quantity-controls button:hover:not(:disabled) {
  background-color: #3498db;
  color: white;
}

.quantity-controls button:disabled {
  background-color: #f0f0f0;
  color: #bdc3c7;
  cursor: not-allowed;
}

.quantity-controls span {
  min-width: 60px;
  text-align: center;
  padding: 0 10px;
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
}

/* 删除按钮样式 */
.remove-button {
  padding: 10px 20px;
  background-color: #e74c3c;
  color: white;
  border: none;
  border-radius: 50px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 3px 10px rgba(231, 76, 60, 0.3);
}

.remove-button:hover {
  background-color: #c0392b;
  transform: translateY(-1px);
  box-shadow: 0 5px 15px rgba(231, 76, 60, 0.4);
}

.remove-button:active {
  transform: translateY(0);
}

/* 购物车总结样式 */
.cart-summary {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  margin-top: auto;
}

.total {
  font-size: 1.4em;
  color: #2c3e50;
  font-weight: 500;
}

.total-price {
  color: #e74c3c;
  font-weight: bold;
  font-size: 1.2em;
  margin-left: 10px;
}

.total-price::before {
  content: '¥';
  font-size: 0.9em;
  margin-right: 2px;
}

/* 结算按钮样式 */
.checkout-button {
  padding: 14px 40px;
  background: linear-gradient(135deg, #27ae60, #2ecc71);
  color: white;
  border: none;
  border-radius: 50px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 700;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(46, 204, 113, 0.3);
  text-transform: uppercase;
  letter-spacing: 0.5px;
  position: relative;
  overflow: hidden;
}

.checkout-button::before {
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

.checkout-button:hover::before {
  width: 300px;
  height: 300px;
}

.checkout-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(46, 204, 113, 0.4);
  background: linear-gradient(135deg, #219a52, #27ae60);
}

.checkout-button:active {
  transform: translateY(0);
}

/* 动画效果 */
.cart-item {
  animation: fadeInUp 0.5s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .cart-container h2 {
    font-size: 1.7em;
    margin-bottom: 30px;
  }
  
  .cart-item {
    grid-template-columns: 1fr;
    gap: 20px;
    text-align: center;
  }
  
  .quantity-controls {
    justify-content: center;
    margin: 0 auto;
    max-width: 180px;
  }
  
  .cart-summary {
    flex-direction: column;
    gap: 20px;
    text-align: center;
    padding: 20px;
  }
  
  .total {
    font-size: 1.3em;
  }
  
  .empty-cart {
    padding: 60px 15px;
  }
  
  .empty-cart p {
    font-size: 1.1em;
  }
  
  .continue-shopping,
  .checkout-button {
    padding: 12px 24px;
    font-size: 14px;
  }
}

@media (max-width: 480px) {
  .cart-container h2 {
    font-size: 1.5em;
  }
  
  .cart-item {
    padding: 16px;
  }
  
  .item-details h3 {
    font-size: 1.2em;
  }
  
  .quantity-controls button {
    width: 36px;
    height: 36px;
  }
  
  .quantity-controls span {
    min-width: 50px;
    font-size: 14px;
  }
}
</style>