package com.example.elasticsearch.repository;

import com.example.elasticsearch.model.ProductDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductElasticsearchRepository extends ElasticsearchRepository<ProductDocument, String> {

    // Derived search query methods generated automatically by Spring Data Elasticsearch
    List<ProductDocument> findByNameContaining(String name);

    List<ProductDocument> findByCategory(String category);

    List<ProductDocument> findByBrand(String brand);
}
