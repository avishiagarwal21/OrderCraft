package com.ordercraft.order;

import java.math.BigDecimal;

public class MaterialShortage {
    private Long materialId;
    private String materialName;
    private String unit;
    private BigDecimal requiredQuantity;
    private BigDecimal availableQuantity;
    private BigDecimal shortageQuantity;

    public MaterialShortage(Long materialId, String materialName, String unit,
                            BigDecimal requiredQuantity, BigDecimal availableQuantity,
                            BigDecimal shortageQuantity) {
        this.materialId = materialId;
        this.materialName = materialName;
        this.unit = unit;
        this.requiredQuantity = requiredQuantity;
        this.availableQuantity = availableQuantity;
        this.shortageQuantity = shortageQuantity;
    }

    public Long getMaterialId() { return materialId; }
    public String getMaterialName() { return materialName; }
    public String getUnit() { return unit; }
    public BigDecimal getRequiredQuantity() { return requiredQuantity; }
    public BigDecimal getAvailableQuantity() { return availableQuantity; }
    public BigDecimal getShortageQuantity() { return shortageQuantity; }
}
