package com.ordercraft.bom;

import java.math.BigDecimal;

import com.ordercraft.material.RawMaterial;
import com.ordercraft.product.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "bill_of_materials")
public class BomItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "material_id", nullable = false)
    private RawMaterial material;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal quantityPerProduct;

    public BomItem() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public RawMaterial getMaterial() { return material; }
    public void setMaterial(RawMaterial material) { this.material = material; }
    public BigDecimal getQuantityPerProduct() { return quantityPerProduct; }
    public void setQuantityPerProduct(BigDecimal quantityPerProduct) { this.quantityPerProduct = quantityPerProduct; }
}
