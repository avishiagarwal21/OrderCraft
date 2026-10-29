package com.ordercraft.purchase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.ordercraft.material.RawMaterial;
import com.ordercraft.material.RawMaterialRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final RawMaterialRepository materialRepository;

    public PurchaseOrderService(PurchaseOrderRepository purchaseOrderRepository,
                                RawMaterialRepository materialRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.materialRepository = materialRepository;
    }

    public List<PurchaseOrder> findAll() { return purchaseOrderRepository.findAll(); }

    public PurchaseOrder create(PurchaseOrderRequest request) {
        if (request.getQuantity() == null || request.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Purchase quantity must be greater than zero");
        }
        RawMaterial material = materialRepository.findById(request.getMaterialId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Material not found"));
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setMaterial(material);
        purchaseOrder.setQuantity(request.getQuantity());
        purchaseOrder.setSupplierName(request.getSupplierName());
        purchaseOrder.setOrderDate(LocalDate.now());
        purchaseOrder.setStatus(PurchaseOrderStatus.OPEN);
        return purchaseOrderRepository.save(purchaseOrder);
    }

    @Transactional
    public PurchaseOrder receive(Long id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchase order not found"));
        if (purchaseOrder.getStatus() != PurchaseOrderStatus.OPEN) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This purchase order was already received");
        }
        RawMaterial material = purchaseOrder.getMaterial();
        material.setStockQuantity(material.getStockQuantity().add(purchaseOrder.getQuantity()));
        materialRepository.save(material);
        purchaseOrder.setStatus(PurchaseOrderStatus.RECEIVED);
        return purchaseOrderRepository.save(purchaseOrder);
    }
}
