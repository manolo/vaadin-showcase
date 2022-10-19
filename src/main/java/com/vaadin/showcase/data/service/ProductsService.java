package com.vaadin.showcase.data.service;

import com.vaadin.showcase.data.entity.Products;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductsService {

    private final ProductsRepository repository;

    @Autowired
    public ProductsService(ProductsRepository repository) {
        this.repository = repository;
    }

    public Optional<Products> get(UUID id) {
        return repository.findById(id);
    }

    public Products update(Products entity) {
        return repository.save(entity);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public Page<Products> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
