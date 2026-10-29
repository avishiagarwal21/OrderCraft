package com.ordercraft.material;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "raw_materials")
public class RawMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Material name is required")
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String name;

    @NotBlank(message = "Unit is required")
    @Size(max = 30)
    @Column(nullable = false, length = 30)
    private String unit;

    @NotNull(message = "Stock quantity is required")
    @DecimalMin(value = "0.000", message = "Stock quantity cannot be negative")
    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal stockQuantity = BigDecimal.ZERO;

    @NotNull(message = "Reorder level is required")
    @DecimalMin(value = "0.000", message = "Reorder level cannot be negative")
    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal reorderLevel = BigDecimal.ZERO;

    public RawMaterial() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public BigDecimal getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(BigDecimal stockQuantity) { this.stockQuantity = stockQuantity; }
    public BigDecimal getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(BigDecimal reorderLevel) { this.reorderLevel = reorderLevel; }
}
