package com.shop.dto;

/**
 * 添加购物车请求 DTO
 */
public class AddCartItemRequest {
    private Long productId;
    private Integer quantity;

    public AddCartItemRequest() {
    }

    public AddCartItemRequest(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
