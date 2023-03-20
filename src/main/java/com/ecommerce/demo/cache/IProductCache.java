package com.ecommerce.demo.cache;

import com.ecommerce.demo.dto.ProductDto;

import java.util.List;

public interface IProductCache {
    List<ProductDto> getProducts();
}
