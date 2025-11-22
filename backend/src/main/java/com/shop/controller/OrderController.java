package com.shop.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shop.dto.PayOrderRequest;
import com.shop.dto.ResponseDTO;
import com.shop.model.Order;
import com.shop.service.OrderService;
import com.shop.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/orders")
public class OrderController extends BaseController {
    private final OrderService orderService;

    public OrderController(OrderService orderService, UserService userService) {
        super(userService);
        this.orderService = orderService;
    }

    /**
     * 创建一个待支付订单（通常用于从购物车生成订单）
     *
     * @param request HTTP 请求
     * @return 创建的订单
     */
    @PostMapping("/create")
    public ResponseDTO<Order> create(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        return ResponseDTO.ok(orderService.createPendingOrder(userId));
    }

    /**
     * 支付订单
     *
     * @param request HTTP 请求
     * @param req     支付请求
     * @return 成功响应
     */
    @PostMapping("/pay")
    public ResponseDTO<Void> pay(HttpServletRequest request, @RequestBody PayOrderRequest req) {
        Long userId = getCurrentUserId(request);
        orderService.markPaidAndFulfillCart(req.getOrderId(), userId);
        return ResponseDTO.ok(null);
    }

    /**
     * 查询当前用户所有订单
     *
     * @param request HTTP 请求
     * @return 订单列表
     */
    @GetMapping
    public ResponseDTO<List<Order>> my(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        return ResponseDTO.ok(orderService.listMyOrders(userId));
    }

    /**
     * 查询当前用户指定订单详情
     *
     * @param id      订单 ID
     * @param request HTTP 请求
     * @return 订单详情
     */
    @GetMapping("/{id}")
    public ResponseDTO<Order> get(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        return ResponseDTO.ok(orderService.getOrderById(id, userId));
    }

    /**
     * 用户将订单标记为已完成
     *
     * @param orderId 订单 ID
     * @return 更新后的订单
     */
    @PutMapping("/{orderId}/complete")
    public ResponseDTO<Order> completeOrder(@PathVariable Long orderId) {
        Order updatedOrder = orderService.updateStatus(orderId, "COMPLETED");
        return ResponseDTO.ok(updatedOrder);
    }
    
    /**
     * 用户将订单标记为已取消
     *
     * @param orderId 订单 ID
     * @return 更新后的订单
     */
    @PutMapping("/{orderId}/cancel")
    public ResponseDTO<Order> cancelOrder(@PathVariable Long orderId) {
        Order updatedOrder = orderService.updateStatus(orderId, "CANCELLED");
        return ResponseDTO.ok(updatedOrder);
    }
}



