package com.ordercraft.material;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RawMaterialService {

    private final RawMaterialRepository repository;

    public RawMaterialService(RawMaterialRepository repository) {
        this.repository = repository;
    }

    public List<RawMaterial> findAll() { return repository.findAll(); }

    public RawMaterial findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Material not found"));
    }

    public RawMaterial create(RawMaterial material) {
        material.setId(null);
        return repository.save(material);
    }

    public RawMaterial update(Long id, RawMaterial updates) {
        RawMaterial material = findById(id);
        material.setName(updates.getName());
        material.setUnit(updates.getUnit());
        material.setStockQuantity(updates.getStockQuantity());
        material.setReorderLevel(updates.getReorderLevel());
        return repository.save(material);
    }

    public void delete(Long id) { repository.delete(findById(id)); }
}
