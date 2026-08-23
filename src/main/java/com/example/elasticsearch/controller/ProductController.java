package com.example.elasticsearch.controller;

import com.example.elasticsearch.dto.ProductRequestDto;
import com.example.elasticsearch.entity.ProductEntity;
import com.example.elasticsearch.model.ProductDocument;
import com.example.elasticsearch.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductEntity> createProduct(@RequestBody ProductRequestDto dto) {
        ProductEntity createdProduct = productService.createProduct(dto);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @PostMapping("/sync")
    public ResponseEntity<Map<String, Object>> syncAllToElasticsearch() {
        long count = productService.syncAllFromMysqlToElasticsearch();
        return ResponseEntity.ok(Map.of(
                "message", "Successfully synced products from MySQL to Elasticsearch",
                "syncedCount", count
        ));
    }

    @GetMapping("/mysql")
    public ResponseEntity<List<ProductEntity>> getAllFromMysql() {
        return ResponseEntity.ok(productService.getAllProductsFromMysql());
    }

    @GetMapping("/es")
    public ResponseEntity<List<ProductDocument>> getAllFromElasticsearch() {
        return ResponseEntity.ok(productService.getAllProductsFromElasticsearch());
    }
}
