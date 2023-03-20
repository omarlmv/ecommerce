package com.ecommerce.demo.cache;

import com.ecommerce.demo.dto.ProductDto;
import com.ecommerce.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class ProductCache implements IProductCache, IInventoryCache {
    private final ProductRepository productRepository;
    private List<ProductDto> products = new ArrayList<>();
    private Map<Integer, Integer> stock = new HashMap<>();

    @Override
    public List<ProductDto> getProducts() {
        if (products.isEmpty()) {
            try {
                products = productRepository.getProductsJson();
                for (ProductDto product : products) {
                    stock.put(product.getIdentification(), product.getStock());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return products;
    }

    @Override
    public int getStock(int id) {
        return stock.getOrDefault(id, 0);
    }

    @Override
    public boolean reduceStock(int id, int quantity) {
        if (stock.containsKey(id)) {
            int currentStock = stock.get(id);
            if (currentStock >= quantity) {
                stock.put(id, currentStock - quantity);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean increaseStock(int id, int quantity) {
        if (stock.containsKey(id)) {
            int currentStock = stock.get(id);
            stock.put(id, currentStock + quantity);
            return true;
        }
        return false;
    }

}
