package com.shop.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shop.dto.ResponseDTO;
import com.shop.model.Order;
import com.shop.model.Product;
import com.shop.model.User;
import com.shop.repository.UserRepository;
import com.shop.service.OrderService;
import com.shop.service.ProductService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final ProductService productService;
    private final OrderService orderService;
    private final UserRepository userRepository;

    public AdminController(
            ProductService productService,
            OrderService orderService,
            UserRepository userRepository
    ) {
        this.productService = productService;
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    /**
     * 查询所有商品（管理员视角）
     *
     * @return 商品列表
     */
    @GetMapping("/products")
    public ResponseDTO<List<Product>> listProducts() {
        return ResponseDTO.ok(productService.listAll());
    }

    /**
     * 新增商品
     *
     * @param product 商品信息
     * @return 创建后的商品
     */
    @PostMapping("/products")
    public ResponseDTO<Product> createProduct(@RequestBody Product product) {
        return ResponseDTO.ok(productService.create(product));
    }

    /**
     * 更新商品
     *
     * @param id      商品 ID
     * @param product 商品信息
     * @return 更新后的商品
     */
    @PutMapping("/products/{id}")
    public ResponseDTO<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        return ResponseDTO.ok(productService.update(id, product));
    }

    /**
     * 删除商品
     *
     * @param id 商品 ID
     * @return 成功响应
     */
    @DeleteMapping("/products/{id}")
    public ResponseDTO<Void> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ResponseDTO.ok(null);
    }

    /**
     * 查询所有订单（管理员视角）
     *
     * @return 订单列表
     */
    @GetMapping("/orders")
    public ResponseDTO<List<Order>> listOrders() {
        return ResponseDTO.ok(orderService.listAllOrders());
    }

    /**
     * 管理员发货（将订单状态更新为 SHIPPING）
     *
     * @param orderId 订单 ID
     * @return 成功响应
     */
    @PostMapping("/orders/{orderId}/ship")
    public ResponseDTO<Void> shipOrder(@PathVariable Long orderId) {
        orderService.updateStatus(orderId, "SHIPPING");
        return ResponseDTO.ok(null);
    }

    /**
     * 查询所有用户
     *
     * @return 用户列表
     */
    @GetMapping("/users")
    public ResponseDTO<List<User>> listUsers() {
        return ResponseDTO.ok(userRepository.findAll());
    }

    /**
     * 删除用户
     *
     * @param userId 用户 ID
     * @return 成功响应
     */
    @DeleteMapping("/users/{userId}")
    public ResponseDTO<Void> deleteUser(@PathVariable Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("用户不存在");
        }
        userRepository.deleteById(userId);
        return ResponseDTO.ok(null);
    }
}

