package com.ordercraft.order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ordercraft.bom.BomItem;
import com.ordercraft.bom.BomItemRepository;
import com.ordercraft.customer.Customer;
import com.ordercraft.customer.CustomerRepository;
import com.ordercraft.material.RawMaterial;
import com.ordercraft.material.RawMaterialRepository;
import com.ordercraft.product.Product;
import com.ordercraft.product.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CustomerOrderService {

    private final CustomerOrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final BomItemRepository bomRepository;
    private final RawMaterialRepository materialRepository;

    public CustomerOrderService(CustomerOrderRepository orderRepository,
                                CustomerRepository customerRepository,
                                ProductRepository productRepository,
                                BomItemRepository bomRepository,
                                RawMaterialRepository materialRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.bomRepository = bomRepository;
        this.materialRepository = materialRepository;
    }

    public List<CustomerOrder> findAll() { return orderRepository.findAll(); }

    public CustomerOrder findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
    }

    public CustomerOrder create(OrderRequest request) {
        if (request.getQuantity() == null || request.getQuantity() < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be at least 1");
        }
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        CustomerOrder order = new CustomerOrder();
        order.setCustomer(customer);
        order.setProduct(product);
        order.setQuantity(request.getQuantity());
        order.setOrderDate(LocalDate.now());
        order.setDeliveryDate(request.getDeliveryDate());
        order.setStatus(OrderStatus.PENDING);
        return orderRepository.save(order);
    }

    @Transactional
    public MaterialCheckResult checkMaterials(Long id) {
        CustomerOrder order = findById(id);
        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.MATERIALS_REQUIRED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Materials can only be checked while the order is pending or waiting for materials");
        }

        List<BomItem> bomItems = bomRepository.findByProductId(order.getProduct().getId());
        if (bomItems.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Add bill-of-material items to this product before checking stock");
        }

        Map<Long, BigDecimal> requiredByMaterial = new HashMap<>();
        Map<Long, RawMaterial> materialById = new HashMap<>();
        for (BomItem item : bomItems) {
            Long materialId = item.getMaterial().getId();
            BigDecimal quantity = item.getQuantityPerProduct()
                    .multiply(BigDecimal.valueOf(order.getQuantity()));
            requiredByMaterial.merge(materialId, quantity, BigDecimal::add);
            materialById.put(materialId, item.getMaterial());
        }

        List<MaterialShortage> shortages = new ArrayList<>();
        for (Map.Entry<Long, BigDecimal> entry : requiredByMaterial.entrySet()) {
            RawMaterial material = materialRepository.findById(entry.getKey())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Material not found"));
            BigDecimal required = entry.getValue();
            BigDecimal available = material.getStockQuantity();
            if (available.compareTo(required) < 0) {
                shortages.add(new MaterialShortage(material.getId(), material.getName(), material.getUnit(),
                        required, available, required.subtract(available)));
            }
        }

        if (!shortages.isEmpty()) {
            order.setStatus(OrderStatus.MATERIALS_REQUIRED);
            orderRepository.save(order);
            return new MaterialCheckResult(id, order.getStatus(), "More materials are needed", shortages);
        }

        for (Map.Entry<Long, BigDecimal> entry : requiredByMaterial.entrySet()) {
            RawMaterial material = materialById.get(entry.getKey());
            material.setStockQuantity(material.getStockQuantity().subtract(entry.getValue()));
            materialRepository.save(material);
        }
        order.setStatus(OrderStatus.IN_PRODUCTION);
        orderRepository.save(order);
        return new MaterialCheckResult(id, order.getStatus(), "Materials reserved and production can start", shortages);
    }

    @Transactional
    public CustomerOrder updateStatus(Long id, OrderStatus newStatus) {
        CustomerOrder order = findById(id);
        if (newStatus == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status is required");
        }
        boolean allowed = switch (order.getStatus()) {
            case IN_PRODUCTION -> newStatus == OrderStatus.READY || newStatus == OrderStatus.CANCELLED;
            case READY -> newStatus == OrderStatus.COMPLETED;
            default -> false;
        };
        if (!allowed) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Allowed order progress is In Production to Ready to Completed");
        }
        if (newStatus == OrderStatus.CANCELLED) {
            for (BomItem item : bomRepository.findByProductId(order.getProduct().getId())) {
                RawMaterial material = item.getMaterial();
                BigDecimal reservedQuantity = item.getQuantityPerProduct()
                        .multiply(BigDecimal.valueOf(order.getQuantity()));
                material.setStockQuantity(material.getStockQuantity().add(reservedQuantity));
                materialRepository.save(material);
            }
        }
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }
}
