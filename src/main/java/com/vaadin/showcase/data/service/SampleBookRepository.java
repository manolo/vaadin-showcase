package com.vaadin.showcase.data.service;

import com.vaadin.showcase.data.entity.SampleBook;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SampleBookRepository extends JpaRepository<SampleBook, UUID> {

}