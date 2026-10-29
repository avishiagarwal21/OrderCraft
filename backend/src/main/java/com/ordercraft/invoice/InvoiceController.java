package com.ordercraft.invoice;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService service;

    public InvoiceController(InvoiceService service) { this.service = service; }

    @GetMapping
    public List<Invoice> findAll() { return service.findAll(); }

    @PostMapping("/order/{orderId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Invoice createForOrder(@PathVariable Long orderId) { return service.createForOrder(orderId); }

    @PostMapping("/{id}/mark-paid")
    public Invoice markPaid(@PathVariable Long id) { return service.markPaid(id); }
}
