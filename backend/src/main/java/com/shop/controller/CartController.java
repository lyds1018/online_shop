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

import com.shop.dto.AddCartItemRequest;
import com.shop.dto.ResponseDTO;
import com.shop.dto.UpdateQuantityRequest;
import com.shop.model.CartItem;
import com.shop.service.CartService;
import com.shop.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/cart")
public class CartController extends BaseController {
    private final CartService cartService;

    public CartController(CartService cartService, UserService userService) {
        super(userService);
        this.cartService = cartService;
    }

    /**
     * 查询购物车列表（当前用户的所有有效购物车项）
     *
     * @param request HTTP 请求
     * @return 购物车项列表
     */
    @GetMapping
    public ResponseDTO<List<CartItem>> list(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        return ResponseDTO.ok(cartService.getActiveCartItems(userId));
    }

    /**
     * 添加商品到购物车
     *
     * @param request HTTP 请求
     * @param req     添加请求
     * @return 购物车项
     */
    @PostMapping("/add")
    public ResponseDTO<CartItem> add(HttpServletRequest request, @RequestBody AddCartItemRequest req) {
        Long userId = getCurrentUserId(request);
        return ResponseDTO.ok(cartService.addItem(userId, req.getProductId(), req.getQuantity()));
    }

    /**
     * 更新购物车某项的数量（增减数量都通过这个接口）
     *
     * @param itemId 购物车项 ID
     * @param req    更新请求
     * @return 成功响应
     */
    @PutMapping("/items/{itemId}")
    public ResponseDTO<Void> update(@PathVariable Long itemId, @RequestBody UpdateQuantityRequest req) {
        cartService.updateItemQuantity(itemId, req.getQuantity());
        return ResponseDTO.ok(null);
    }

    /**
     * 删除购物车中的某项
     *
     * @param itemId 购物车项 ID
     * @return 成功响应
     */
    @DeleteMapping("/items/{itemId}")
    public ResponseDTO<Void> remove(@PathVariable Long itemId) {
        cartService.removeItem(itemId);
        return ResponseDTO.ok(null);
    }
}



