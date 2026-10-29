package com.ordercraft.material;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/materials")
public class RawMaterialController {

    private final RawMaterialService service;

    public RawMaterialController(RawMaterialService service) { this.service = service; }

    @GetMapping
    public List<RawMaterial> findAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public RawMaterial findById(@PathVariable Long id) { return service.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RawMaterial create(@Valid @RequestBody RawMaterial material) { return service.create(material); }

    @PutMapping("/{id}")
    public RawMaterial update(@PathVariable Long id, @Valid @RequestBody RawMaterial material) {
        return service.update(id, material);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
}
