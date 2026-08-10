package com.devtrace.order.dto;

public class OrderRequest {
    private Long userId;
    private Long productId;
    private Integer quantity;
    private String paymentMethod;
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    @Override
    public String toString() {
        return "OrderRequest{userId=" + userId + ", productId=" + productId + 
               ", quantity=" + quantity + ", paymentMethod='" + paymentMethod + "'}";
    }
}
