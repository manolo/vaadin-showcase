package com.vaadin.showcase.data.entity;

import java.time.LocalDate;
import javax.persistence.Entity;

@Entity
public class Products extends AbstractEntity {

    private String name;
    private String ean;
    private Integer initial;
    private LocalDate received;
    private Integer current;
    private boolean inStock;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEan() {
        return ean;
    }
    public void setEan(String ean) {
        this.ean = ean;
    }
    public Integer getInitial() {
        return initial;
    }
    public void setInitial(Integer initial) {
        this.initial = initial;
    }
    public LocalDate getReceived() {
        return received;
    }
    public void setReceived(LocalDate received) {
        this.received = received;
    }
    public Integer getCurrent() {
        return current;
    }
    public void setCurrent(Integer current) {
        this.current = current;
    }
    public boolean isInStock() {
        return inStock;
    }
    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

}
