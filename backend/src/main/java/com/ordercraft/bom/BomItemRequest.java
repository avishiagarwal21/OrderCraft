package com.ordercraft.bom;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class BomItemRequest {
    @NotNull @Positive
    private Long productId;
    @NotNull @Positive
    private Long materialId;
    @NotNull @DecimalMin(value = "0.000", inclusive = false, message = "Quantity per product must be greater than zero")
    private BigDecimal quantityPerProduct;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }
    public BigDecimal getQuantityPerProduct() { return quantityPerProduct; }
    public void setQuantityPerProduct(BigDecimal quantityPerProduct) { this.quantityPerProduct = quantityPerProduct; }
}
