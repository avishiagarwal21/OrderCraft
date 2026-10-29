package com.ordercraft.order;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class CustomerOrderController {

    private final CustomerOrderService service;

    public CustomerOrderController(CustomerOrderService service) { this.service = service; }

    @GetMapping
    public List<CustomerOrder> findAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public CustomerOrder findById(@PathVariable Long id) { return service.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerOrder create(@Valid @RequestBody OrderRequest request) { return service.create(request); }

    @PostMapping("/{id}/check-materials")
    public MaterialCheckResult checkMaterials(@PathVariable Long id) { return service.checkMaterials(id); }

    @PutMapping("/{id}/status")
    public CustomerOrder updateStatus(@PathVariable Long id, @Valid @RequestBody OrderStatusRequest request) {
        return service.updateStatus(id, request.getStatus());
    }
}
