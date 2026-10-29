package com.ordercraft.purchase;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.ordercraft.material.RawMaterial;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "purchase_orders")
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "material_id", nullable = false)
    private RawMaterial material;

    private BigDecimal quantity;
    private String supplierName;
    private LocalDate orderDate;

    @Enumerated(EnumType.STRING)
    private PurchaseOrderStatus status;

    public PurchaseOrder() {
    }

    public Long getId() { return id; }
    public RawMaterial getMaterial() { return material; }
    public void setMaterial(RawMaterial material) { this.material = material; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }
    public PurchaseOrderStatus getStatus() { return status; }
    public void setStatus(PurchaseOrderStatus status) { this.status = status; }
}
