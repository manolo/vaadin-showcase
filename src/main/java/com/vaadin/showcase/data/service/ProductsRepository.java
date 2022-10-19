package com.vaadin.showcase.data.service;

import com.vaadin.showcase.data.entity.Products;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductsRepository extends JpaRepository<Products, UUID> {

}