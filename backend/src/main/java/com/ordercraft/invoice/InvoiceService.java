package com.ordercraft.invoice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.ordercraft.order.CustomerOrder;
import com.ordercraft.order.CustomerOrderRepository;
import com.ordercraft.order.OrderStatus;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final CustomerOrderRepository orderRepository;

    public InvoiceService(InvoiceRepository invoiceRepository, CustomerOrderRepository orderRepository) {
        this.invoiceRepository = invoiceRepository;
        this.orderRepository = orderRepository;
    }

    public List<Invoice> findAll() { return invoiceRepository.findAll(); }

    public Invoice createForOrder(Long orderId) {
        CustomerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An invoice can only be created for a completed order");
        }
        if (invoiceRepository.findByOrderId(orderId).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An invoice already exists for this order");
        }
        Invoice invoice = new Invoice();
        invoice.setOrder(order);
        invoice.setTotalAmount(order.getProduct().getUnitPrice().multiply(BigDecimal.valueOf(order.getQuantity())));
        invoice.setInvoiceDate(LocalDate.now());
        invoice.setStatus(InvoiceStatus.PENDING);
        return invoiceRepository.save(invoice);
    }

    public Invoice markPaid(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found"));
        invoice.setStatus(InvoiceStatus.PAID);
        return invoiceRepository.save(invoice);
    }
}
