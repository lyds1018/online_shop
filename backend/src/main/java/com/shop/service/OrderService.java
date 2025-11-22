package com.shop.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.exception.BusinessException;
import com.shop.model.CartItem;
import com.shop.model.Order;
import com.shop.model.OrderItem;
import com.shop.model.Product;
import com.shop.model.User;
import com.shop.repository.OrderItemRepository;
import com.shop.repository.OrderRepository;
import com.shop.repository.ProductRepository;
import com.shop.repository.UserRepository;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartService cartService;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            CartService cartService,
            ProductRepository productRepository,
            UserRepository userRepository
    ) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartService = cartService;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    /**
     * 创建待支付订单（根据用户的购物车）
     *
     * @param userId 用户 ID
     * @return 创建的订单
     */
    @Transactional
    public Order createPendingOrder(Long userId) {
        // 查询购物车所有未结算商品
        List<CartItem> items = cartService.getActiveCartItems(userId);
        if (items.isEmpty()) {
            throw new BusinessException("购物车为空");
        }

        // 计算总价格，同时验证库存
        double total = 0.0;
        for (CartItem item : items) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new BusinessException("商品不存在"));
            
            if (product.getStock() < item.getQuantity()) {
                throw new BusinessException(product.getName() + " 库存不足");
            }
            
            total += product.getPrice() * item.getQuantity();
        }

        // 创建订单
        Order order = new Order();
        order.setUserId(userId);
        order.setStatus("PENDING");
        order.setTotalPrice(total);
        order = orderRepository.save(order);

        // 创建订单项（从购物车复制过来）
        for (CartItem item : items) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new BusinessException("商品不存在"));
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(product.getId());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItemRepository.save(orderItem);
        }

        return order;
    }

    /**
     * 支付订单并扣减库存，完成购物车结算
     *
     * @param orderId 订单 ID
     * @param userId  用户 ID
     */
    @Transactional
    public void markPaidAndFulfillCart(Long orderId, Long userId) {
        // 检查订单存在性和归属
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("订单不存在"));

        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作该订单");
        }

        // 必须是待支付状态才能支付
        if (!"PENDING".equals(order.getStatus())) {
            throw new BusinessException("订单非待支付状态");
        }

        // 查询订单项
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);

        // 检查库存是否足够
        for (OrderItem item : orderItems) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new BusinessException("商品不存在"));
            
            if (product.getStock() < item.getQuantity()) {
                throw new BusinessException(product.getName() + " 库存不足");
            }
        }

        // 扣减库存
        for (OrderItem item : orderItems) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new BusinessException("商品不存在"));
            
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
        }

        // 更新订单状态为已支付
        order.setStatus("PAID");
        orderRepository.save(order);

        // 清空购物车（支付后清除）
        cartService.clearCart(userId);
    }

    /**
     * 查询当前用户的全部订单
     *
     * @param userId 用户 ID
     * @return 订单列表
     */
    public List<Order> listMyOrders(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    /**
     * 查询单个订单详情（包含订单项和商品信息）
     *
     * @param id     订单 ID
     * @param userId 用户 ID
     * @return 订单详情
     */
    public Order getOrderById(Long id, Long userId) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("订单不存在"));

        // 权限校验：用户只能查看自己的订单
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权访问此订单");
        }

        // 查询订单项并附带商品信息
        List<OrderItem> items = orderItemRepository.findByOrderId(id);
        for (OrderItem item : items) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new BusinessException("商品不存在"));
            item.setProduct(product);
        }
        order.setItems(items);

        return order;
    }

    /**
     * 更新订单状态（管理员和用户共用）
     *
     * @param orderId 订单 ID
     * @param status  新状态
     * @return 更新后的订单
     */
    @Transactional
    public Order updateStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("订单不存在"));

        order.setStatus(status);
        return orderRepository.save(order);
    }

    /**
     * 后台管理员：列出全部订单（订单信息 + 用户信息）
     *
     * @return 所有订单列表
     */
    public List<Order> listAllOrders() {
        List<Order> orders = orderRepository.findAll();

        for (Order order : orders) {
            // 加载用户信息
            User user = userRepository.findById(order.getUserId()).orElse(null);
            order.setUser(user);

            // 不加载订单项（保持为空或保持默认）
            order.setItems(null);
        }

        return orders;
    }
}
