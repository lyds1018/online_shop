<template>
  <div class="order-detail-container">
    <h2>订单详情</h2>

    <div v-if="order" class="order-detail">
      <div class="order-header">
        <p>订单编号：{{ order.id }}</p>
        <p>下单时间：{{ formatDate(order.createTime) }}</p>
        <p>
          订单状态：
          <span class="order-status-tag" :data-status="order.status">{{ getStatusText(order.status) }}</span>
        </p>
      </div>

      <div class="order-items">
        <h3>商品信息</h3>
        <div v-for="item in order.items" :key="item.id" class="item">
          <div class="item-info">
            <h4>{{ item.product.name }}</h4>
            <p>价格：¥{{ item.product.price }}</p>
            <p>数量：{{ item.quantity }}</p>
            <p>小计：¥{{ (item.product.price * item.quantity).toFixed(2) }}</p>
          </div>
        </div>
      </div>

      <div class="order-summary">
        <p>订单总计：¥{{ order.totalPrice }}</p>
        <div class="actions">
          <button
            v-if="order.status === 'PENDING'"
            class="pay-button"
            @click="pay"
          >去支付</button>
          <button
            v-if="order.status === 'PENDING'"
            class="cancel-button"
            @click="cancel"
          >取消订单</button>
          <button
            v-if="order.status === 'SHIPPING'"
            class="confirm-button"
            @click="complete"
          >确认收货</button>
        </div>
      </div>
    </div>

    <div v-else class="loading">加载中...</div>
  </div>
</template>

<script>
import { orderApi } from '@/utils/api';
import { formatDate, getStatusText, showError, showSuccess } from '@/utils/helpers';

export default {
  name: 'OrderDetail',
  data() {
    return {
      order: null
    }
  },
  async created() {
    await this.fetchOrderDetail()
  },
  methods: {
    formatDate,
    getStatusText,
    
    async fetchOrderDetail() {
      try {
        const orderId = this.$route.params.id
        const response = await orderApi.getById(orderId)
        this.order = response.data.data
      } catch (error) {
        showError(error, '获取订单详情失败')
      }
    },
    
    async pay() {
      try {
        await orderApi.pay(this.order.id)
        await this.fetchOrderDetail()
        showSuccess('支付成功')
      } catch (error) {
        showError(error, '支付失败')
      }
    },
    
    async cancel() {
      try {
        await orderApi.cancel(this.order.id)
        await this.fetchOrderDetail()
        showSuccess('订单已取消')
      } catch (error) {
        showError(error, '取消失败')
      }
    },
    
    async complete() {
      try {
        await orderApi.complete(this.order.id)
        await this.fetchOrderDetail()
        showSuccess('确认收货成功')
      } catch (error) {
        showError(error, '操作失败')
      }
    }
  }
}
</script>

<style scoped>
.order-detail-container {
  padding: 30px 20px;
  max-width: 1000px;
  margin: 0 auto;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  min-height: calc(100vh - 100px);
}

.order-detail-container h2 {
  color: #2c3e50;
  margin-bottom: 30px;
  font-size: 24px;
  font-weight: 600;
  padding-bottom: 10px;
  border-bottom: 2px solid #ecf0f1;
}

.order-detail {
  background-color: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.order-header {
  background: linear-gradient(135deg, #f8f9fa, #e9ecef);
  padding: 24px;
  margin-bottom: 0;
  border-bottom: 1px solid #dee2e6;
}

.order-header p {
  margin: 8px 0;
  font-size: 16px;
  color: #495057;
}

.order-header p:first-child {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
}

.order-status-tag {
  display: inline-block;
  padding: 8px 20px;
  border-radius: 25px;
  font-size: 16px;
  font-weight: 600;
  margin-top: 5px;
}

.order-status-tag[data-status="PENDING"] {
  background-color: #fff3cd;
  color: #856404;
}

.order-status-tag[data-status="PAID"] {
  background-color: #d1ecf1;
  color: #0c5460;
}

.order-status-tag[data-status="SHIPPING"] {
  background-color: #cce5ff;
  color: #004085;
}

.order-status-tag[data-status="COMPLETED"] {
  background-color: #d4edda;
  color: #155724;
}

.order-status-tag[data-status="CANCELLED"] {
  background-color: #f8d7da;
  color: #721c24;
}

.order-items {
  padding: 24px;
  margin-bottom: 0;
  border-bottom: 1px solid #dee2e6;
}

.order-items h3 {
  color: #2c3e50;
  margin-bottom: 20px;
  font-size: 20px;
  font-weight: 600;
}

.item {
  display: flex;
  align-items: center;
  padding: 20px 0;
  border-bottom: 1px solid #f1f3f5;
  transition: background-color 0.2s ease;
}

.item:hover {
  background-color: #f8f9fa;
  border-radius: 8px;
}

.item:last-child {
  border-bottom: none;
}

.item-info h4 {
  margin: 0 0 10px 0;
  color: #2c3e50;
  font-size: 18px;
  font-weight: 500;
}

.item-info p {
  margin: 8px 0;
  color: #6c757d;
  font-size: 16px;
}

.order-summary {
  padding: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #f8f9fa;
}

.order-summary p {
  margin: 0;
  font-size: 22px;
  font-weight: 700;
  color: #e74c3c;
}

.actions {
  display: flex;
  gap: 12px;
}

.actions button {
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 500;
  transition: all 0.3s ease;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.pay-button {
  background: linear-gradient(135deg, #27ae60, #2ecc71);
  color: white;
  box-shadow: 0 4px 12px rgba(39, 174, 96, 0.3);
}

.pay-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(39, 174, 96, 0.4);
}

.cancel-button {
  background: linear-gradient(135deg, #e74c3c, #c0392b);
  color: white;
  box-shadow: 0 4px 12px rgba(231, 76, 60, 0.3);
}

.cancel-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(231, 76, 60, 0.4);
}

.confirm-button {
  background: linear-gradient(135deg, #3498db, #2980b9);
  color: white;
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.3);
}

.confirm-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(52, 152, 219, 0.4);
}

.loading {
  text-align: center;
  padding: 80px 40px;
  font-size: 18px;
  color: #6c757d;
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.04);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .order-detail-container {
    padding: 20px 15px;
  }
  
  .order-header,
  .order-items,
  .order-summary {
    padding: 20px 15px;
  }
  
  .order-summary {
    flex-direction: column;
    align-items: flex-start;
    gap: 20px;
  }
  
  .actions {
    width: 100%;
    flex-wrap: wrap;
    justify-content: flex-start;
  }
  
  .actions button {
    flex: 1;
    min-width: 140px;
    text-align: center;
  }
  
  .item {
    padding: 15px 0;
  }
  
  .item-info h4 {
    font-size: 16px;
  }
  
  .item-info p {
    font-size: 14px;
  }
}
</style>
