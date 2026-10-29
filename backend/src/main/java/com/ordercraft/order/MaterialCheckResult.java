package com.ordercraft.order;

import java.util.List;

public class MaterialCheckResult {
    private Long orderId;
    private OrderStatus status;
    private String message;
    private List<MaterialShortage> shortages;

    public MaterialCheckResult(Long orderId, OrderStatus status, String message,
                               List<MaterialShortage> shortages) {
        this.orderId = orderId;
        this.status = status;
        this.message = message;
        this.shortages = shortages;
    }

    public Long getOrderId() { return orderId; }
    public OrderStatus getStatus() { return status; }
    public String getMessage() { return message; }
    public List<MaterialShortage> getShortages() { return shortages; }
}
