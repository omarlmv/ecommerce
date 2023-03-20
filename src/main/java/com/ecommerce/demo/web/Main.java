package com.ecommerce.demo.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ecommerce.demo.dto.ProductDto;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<ProductDto> productos = mapper.readValue(new File("src/main/resources/data/product.json"), new TypeReference<>() {
            });
            for (ProductDto producto : productos) {
                System.out.println("ID: " + producto.getIdentification());
                System.out.println("Descripción: " + producto.getDescription());
                System.out.println("Importe: " + producto.getImporte());
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
