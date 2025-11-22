package com.shop.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.exception.BusinessException;
import com.shop.model.Cart;
import com.shop.model.CartItem;
import com.shop.model.Product;
import com.shop.repository.CartItemRepository;
import com.shop.repository.CartRepository;
import com.shop.repository.ProductRepository;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    /**
     * 获取或创建用户购物车
     *
     * @param userId 用户 ID
     * @return 购物车对象
     */
    @Transactional
    public Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUserId(userId);
                    return cartRepository.save(cart);
                });
    }

    /**
     * 获取用户购物车中的所有有效商品
     *
     * @param userId 用户 ID
     * @return 购物车项列表
     */
    public List<CartItem> getActiveCartItems(Long userId) {
        Cart cart = getOrCreateCart(userId);
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        
        // 加载商品信息
        for (CartItem item : items) {
            Product product = productRepository.findById(item.getProductId())
                    .orElse(null);
            item.setProduct(product);
        }
        
        return items;
    }

    /**
     * 添加商品到购物车
     *
     * @param userId    用户 ID
     * @param productId 商品 ID
     * @param quantity  数量
     * @return 购物车项
     */
    @Transactional
    public CartItem addItem(Long userId, Long productId, int quantity) {
        // 参数校验
        if (quantity <= 0) {
            throw new BusinessException("数量必须大于 0");
        }
        
        // 查找商品并检查库存
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException("商品不存在"));
        
        if (product.getStock() < quantity) {
            throw new BusinessException("库存不足");
        }
        
        // 获取或创建购物车
        Cart cart = getOrCreateCart(userId);
        
        // 创建购物车项
        CartItem item = new CartItem();
        item.setCartId(cart.getId());
        item.setProductId(productId);
        item.setQuantity(quantity);
        
        return cartItemRepository.save(item);
    }

    /**
     * 更新购物车项数量
     *
     * @param itemId   购物车项 ID
     * @param quantity 新数量
     */
    @Transactional
    public void updateItemQuantity(Long itemId, int quantity) {
        // 参数校验
        if (quantity <= 0) {
            throw new BusinessException("数量必须大于 0");
        }
        
        // 查找购物车项
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException("购物车项不存在"));
        
        // 检查库存
        Product product = productRepository.findById(item.getProductId())
                .orElseThrow(() -> new BusinessException("商品不存在"));
        
        if (product.getStock() < quantity) {
            throw new BusinessException("库存不足");
        }
        
        item.setQuantity(quantity);
        cartItemRepository.save(item);
    }

    /**
     * 移除购物车项
     *
     * @param itemId 购物车项 ID
     */
    @Transactional
    public void removeItem(Long itemId) {
        if (!cartItemRepository.existsById(itemId)) {
            throw new BusinessException("购物车项不存在");
        }
        cartItemRepository.deleteById(itemId);
    }

    /**
     * 清空用户购物车
     *
     * @param userId 用户 ID
     */
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cartItemRepository.deleteByCartId(cart.getId());
    }
}


