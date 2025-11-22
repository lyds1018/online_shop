// 订单状态映射
export const ORDER_STATUS_MAP = {
  PENDING: '待支付',
  PAID: '已支付',
  SHIPPING: '已发货',
  COMPLETED: '已完成',
  CANCELLED: '已取消'
}

// 订单状态颜色映射（用于样式）
export const ORDER_STATUS_COLORS = {
  PENDING: { bg: '#fff3cd', color: '#856404' },
  PAID: { bg: '#d1ecf1', color: '#0c5460' },
  SHIPPING: { bg: '#cce5ff', color: '#004085' },
  COMPLETED: { bg: '#d4edda', color: '#155724' },
  CANCELLED: { bg: '#f8d7da', color: '#721c24' }
}

// 用户角色
export const USER_ROLES = {
  USER: 'USER',
  ADMIN: 'ADMIN'
}

// 本地存储键名
export const STORAGE_KEYS = {
  TOKEN: 'token',
  USER_ROLE: 'userRole',
  USERNAME: 'username'
}

// 可用的商品图片列表
export const AVAILABLE_IMAGES = [
  '00a5036a-07d3-4131-b677-956312bbbc2c.jpg',
  '0a91f05d-948b-48d4-aac5-5cbd2e85238f.jpg',
  '0b02244f-6908-4ccb-a9d2-ccb5a462e30e.jpg',
  '0b1e57bf-b4fd-40df-9832-4749d7d69db9.jpg',
  '0bc4f5ac-d601-421d-8131-81958a195705.jpg',
  '0dc503b2-90a2-4971-9723-c085a1844b76.jpg',
  '0ec8c4a7-aedc-464d-9e23-d3e4acafdc73.jpg',
  '0f701215-b782-40c7-8bbd-97b51be56461.jpg',
  '0f724c0f-8888-4b75-8fe1-dc7dd8f2b7bd.jpg',
  '1aea34fa-f45e-4c3c-b73c-da1f92492c95.jpg',
  '1c70ddcb-ca69-40ed-a263-30880b2e2cac.jpg',
  '1ca16211-2b80-4006-ab60-e1a3cab4218c.jpg',
  '1eefadae-5f62-4abd-b283-077e7b6d9193.jpg',
  '2d827a7e-fb30-493d-840a-cb21766814fd.jpg',
  '3b40971a-3f32-45cf-a99a-aada90ee8e33.jpg'
]
