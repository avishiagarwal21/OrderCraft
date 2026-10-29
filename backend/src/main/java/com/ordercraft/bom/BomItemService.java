package com.ordercraft.bom;

import java.util.List;

import com.ordercraft.material.RawMaterial;
import com.ordercraft.material.RawMaterialService;
import com.ordercraft.product.Product;
import com.ordercraft.product.ProductService;
import org.springframework.stereotype.Service;

@Service
public class BomItemService {

    private final BomItemRepository repository;
    private final ProductService productService;
    private final RawMaterialService materialService;

    public BomItemService(BomItemRepository repository, ProductService productService,
                          RawMaterialService materialService) {
        this.repository = repository;
        this.productService = productService;
        this.materialService = materialService;
    }

    public List<BomItem> findForProduct(Long productId) {
        productService.findById(productId);
        return repository.findByProductId(productId);
    }

    public BomItem create(BomItemRequest request) {
        Product product = productService.findById(request.getProductId());
        RawMaterial material = materialService.findById(request.getMaterialId());
        BomItem item = new BomItem();
        item.setProduct(product);
        item.setMaterial(material);
        item.setQuantityPerProduct(request.getQuantityPerProduct());
        return repository.save(item);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
