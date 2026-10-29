package com.ordercraft.purchase;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class PurchaseOrderRequest {
    @NotNull @Positive
    private Long materialId;
    @NotNull @DecimalMin(value = "0.000", inclusive = false, message = "Quantity must be greater than zero")
    private BigDecimal quantity;
    @Size(max = 160)
    private String supplierName;

    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
}
