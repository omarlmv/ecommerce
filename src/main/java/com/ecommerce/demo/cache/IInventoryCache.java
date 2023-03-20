package com.ecommerce.demo.cache;

public interface IInventoryCache {
    int getStock(int id);
    boolean reduceStock(int id, int quantity);
    boolean increaseStock(int id, int quantity);

}
