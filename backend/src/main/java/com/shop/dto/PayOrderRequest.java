package com.shop.dto;

/**
 * 支付订单请求 DTO
 */
public class PayOrderRequest {
    private Long orderId;

    public PayOrderRequest() {
    }

    public PayOrderRequest(Long orderId) {
        this.orderId = orderId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}
