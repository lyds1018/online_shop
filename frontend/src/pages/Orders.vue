<template>
  <div class="orders-container">
    <h2>我的订单</h2>
    <div v-if="orders.length === 0" class="no-orders">
      <p>没有订单</p>
      <router-link to="/products" class="continue-shopping">去购物</router-link>
    </div>

    <div v-else class="order-list">
      <div v-for="order in orders" :key="order.id" class="order-card">
        <div class="order-header">
          <div>订单 #{{ order.id }}</div>
          <div class="order-status" :data-status="order.status">{{ getStatusText(order.status) }}</div>
        </div>

        <div class="order-items">
          <div v-for="item in order.items" :key="item.id" class="order-item">
            <div class="item-details">
              <h4>{{ item.product.name }}</h4>
              <p>数量：{{ item.quantity }}</p>
              <p>小计：¥{{ (item.product.price * item.quantity).toFixed(2) }}</p>
            </div>
          </div>
        </div>

        <div class="order-footer">
          <div class="total-price">总计：¥{{ order.totalPrice }}</div>
          <div class="order-actions">
            <router-link :to="`/orders/${order.id}`">查看详情</router-link>
            <button
              v-if="order.status === 'PENDING'"
              @click="pay(order.id)"
              class="pay-button"
            >去支付</button>
            <button
              v-if="order.status === 'PENDING'"
              @click="cancel(order.id)"
              class="cancel-button"
            >取消订单</button>
            <button
              v-if="order.status === 'SHIPPING'"
              @click="complete(order.id)"
              class="confirm-button"
            >确认收货</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'Orders',
  data() {
    return {
      orders: []
    }
  },
  async created() {
    await this.fetchOrders()
  },
  methods: {
    async fetchOrders() {
      try {
        const token = localStorage.getItem('token')
        const response = await axios.get('/api/orders', {
          headers: { Authorization: `Bearer ${token}` }
        })
        this.orders = response.data.data
      } catch (error) {
        alert('获取订单失败：' + error.message)
      }
    },
    getStatusText(status) {
      const map = {
        PENDING: '待支付',
        PAID: '已支付',
        SHIPPING: '已发货',
        COMPLETED: '已完成',
        CANCELLED: '已取消'
      }
      return map[status] || status
    },
    async pay(orderId) {
      try {
        const token = localStorage.getItem('token')
        await axios.post('/api/orders/pay', { orderId }, {
          headers: { Authorization: `Bearer ${token}` }
        })
        await this.fetchOrders()
        alert('支付成功')
      } catch (error) {
        alert('支付失败：' + error.message)
      }
    },
    async cancel(orderId) {
      try {
        const token = localStorage.getItem('token')
        await axios.put(`/api/orders/${orderId}/cancel`, {}, {
          headers: { Authorization: `Bearer ${token}` }
        })
        await this.fetchOrders()
        alert('订单已取消')
      } catch (error) {
        alert('取消失败：' + error.message)
      }
    },
    async complete(orderId) {
      try {
        const token = localStorage.getItem('token')
        await axios.put(`/api/orders/${orderId}/complete`, {}, {
          headers: { Authorization: `Bearer ${token}` }
        })
        await this.fetchOrders()
        alert('确认收货成功')
      } catch (error) {
        alert('操作失败：' + error.message)
      }
    }
  }
}
</script>

<style scoped>
.orders-container {
  padding: 30px 20px;
  max-width: 1200px;
  margin: 0 auto;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.orders-container h2 {
  color: #2c3e50;
  margin-bottom: 30px;
  font-size: 24px;
  font-weight: 600;
  padding-bottom: 10px;
  border-bottom: 2px solid #ecf0f1;
}

.no-orders {
  text-align: center;
  padding: 60px 20px;
  background-color: #f8f9fa;
  border-radius: 12px;
  border: 1px dashed #dee2e6;
}

.no-orders p {
  color: #6c757d;
  font-size: 18px;
  margin-bottom: 20px;
}

.continue-shopping {
  display: inline-block;
  padding: 12px 30px;
  background: linear-gradient(135deg, #3498db, #64b5f6);
  color: white;
  text-decoration: none;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.3);
}

.continue-shopping:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(52, 152, 219, 0.4);
}

.order-list {
  display: grid;
  gap: 20px;
}

.order-card {
  background-color: white;
  border: 1px solid #e1e8ed;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
}

.order-card:hover {
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f1f3f5;
}

.order-header div:first-child {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
}

.order-status {
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.order-status[data-status="PENDING"] {
  background-color: #fff3cd;
  color: #856404;
}

.order-status[data-status="PAID"] {
  background-color: #d1ecf1;
  color: #0c5460;
}

.order-status[data-status="SHIPPING"] {
  background-color: #cce5ff;
  color: #004085;
}

.order-status[data-status="COMPLETED"] {
  background-color: #d4edda;
  color: #155724;
}

.order-status[data-status="CANCELLED"] {
  background-color: #f8d7da;
  color: #721c24;
}

.order-items {
  margin-bottom: 20px;
}

.order-item {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 15px 0;
  border-bottom: 1px solid #f1f3f5;
}

.order-item:last-child {
  border-bottom: none;
}

.item-details h4 {
  margin: 0 0 8px 0;
  color: #2c3e50;
  font-size: 16px;
  font-weight: 500;
}

.item-details p {
  margin: 4px 0;
  color: #6c757d;
  font-size: 14px;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 20px;
  border-top: 1px solid #f1f3f5;
}

.total-price {
  font-size: 20px;
  font-weight: 700;
  color: #e74c3c;
}

.order-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.order-actions a {
  padding: 8px 16px;
  background-color: #f8f9fa;
  color: #3498db;
  text-decoration: none;
  border-radius: 6px;
  font-weight: 500;
  transition: all 0.3s ease;
  border: 1px solid #dee2e6;
}

.order-actions a:hover {
  background-color: #3498db;
  color: white;
  border-color: #3498db;
}

button {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.pay-button {
  background: linear-gradient(135deg, #27ae60, #2ecc71);
  color: white;
  box-shadow: 0 4px 12px rgba(39, 174, 96, 0.3);
}

.pay-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(39, 174, 96, 0.4);
}

.cancel-button {
  background: linear-gradient(135deg, #e74c3c, #c0392b);
  color: white;
  box-shadow: 0 4px 12px rgba(231, 76, 60, 0.3);
}

.cancel-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(231, 76, 60, 0.4);
}

.confirm-button {
  background: linear-gradient(135deg, #3498db, #2980b9);
  color: white;
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.3);
}

.confirm-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(52, 152, 219, 0.4);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .orders-container {
    padding: 20px 15px;
  }
  
  .order-card {
    padding: 20px;
  }
  
  .order-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .order-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .order-actions {
    width: 100%;
    flex-wrap: wrap;
  }
  
  .order-actions a,
  button {
    flex: 1;
    min-width: 120px;
    text-align: center;
  }
}
</style>
