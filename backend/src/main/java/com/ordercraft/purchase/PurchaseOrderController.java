package com.ordercraft.purchase;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {

    private final PurchaseOrderService service;

    public PurchaseOrderController(PurchaseOrderService service) { this.service = service; }

    @GetMapping
    public List<PurchaseOrder> findAll() { return service.findAll(); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PurchaseOrder create(@Valid @RequestBody PurchaseOrderRequest request) { return service.create(request); }

    @PostMapping("/{id}/receive")
    public PurchaseOrder receive(@PathVariable Long id) { return service.receive(id); }
}
