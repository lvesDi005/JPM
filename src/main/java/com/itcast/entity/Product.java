package com.itcast.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class Product implements Serializable {
    private Long id;
    private String name;
    private Long categoryId;
    private BigDecimal price;
    private String image;
    private String description;
    private Integer status;
}
