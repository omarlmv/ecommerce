package com.ecommerce.demo.service;

import com.ecommerce.demo.dto.ProductDto;

import java.util.List;

public interface ProductService {

  List<ProductDto> getAllProducts();

  ProductDto getProductById(Integer id);
}
