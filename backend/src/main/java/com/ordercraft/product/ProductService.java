package com.ordercraft.product;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    public Product create(Product product) {
        product.setId(null);
        return productRepository.save(product);
    }

    public Product update(Long id, Product updates) {
        Product product = findById(id);
        product.setName(updates.getName());
        product.setDescription(updates.getDescription());
        product.setUnitPrice(updates.getUnitPrice());
        product.setStockQuantity(updates.getStockQuantity());
        return productRepository.save(product);
    }

    public void delete(Long id) {
        productRepository.delete(findById(id));
    }
}
