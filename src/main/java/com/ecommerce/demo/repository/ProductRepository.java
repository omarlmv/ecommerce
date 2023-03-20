package com.ecommerce.demo.repository;

import com.ecommerce.demo.dto.ProductDto;

import java.util.List;

public interface ProductRepository {
    List<ProductDto> getProductsJson() throws Exception;
}
