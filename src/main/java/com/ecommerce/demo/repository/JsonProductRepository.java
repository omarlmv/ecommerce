package com.ecommerce.demo.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ecommerce.demo.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JsonProductRepository implements ProductRepository{

    private final ObjectMapper mapper;

    @Override
    public List<ProductDto> getProductsJson() throws Exception {
        return mapper.readValue(new File("src/main/resources/data/product.json"), new TypeReference<List<ProductDto>>(){});
    }
}
