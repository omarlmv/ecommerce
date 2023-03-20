package com.ecommerce.demo.service.impl;

import com.ecommerce.demo.cache.ProductCache;
import com.ecommerce.demo.dto.ProductDto;
import com.ecommerce.demo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductCache productCache;
    @Override
    public List<ProductDto> getAllProducts() {
        return productCache.getProducts();
    }

    @Override
    public ProductDto getProductById(Integer id) {
        return productCache.getProducts().stream().
                filter(x -> x.getIdentification().equals(id)).findFirst().orElse(null);
    }
}