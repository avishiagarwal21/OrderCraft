package com.ordercraft.bom;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bom")
public class BomItemController {

    private final BomItemService service;

    public BomItemController(BomItemService service) { this.service = service; }

    @GetMapping("/product/{productId}")
    public List<BomItem> findForProduct(@PathVariable Long productId) {
        return service.findForProduct(productId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BomItem create(@Valid @RequestBody BomItemRequest request) { return service.create(request); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
}
