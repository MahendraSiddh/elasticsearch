package com.example.elasticsearch.service;

import com.example.elasticsearch.dto.ProductRequestDto;
import com.example.elasticsearch.entity.ProductEntity;
import com.example.elasticsearch.model.ProductDocument;
import com.example.elasticsearch.repository.ProductElasticsearchRepository;
import com.example.elasticsearch.repository.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductJpaRepository productJpaRepository;
    private final ProductElasticsearchRepository productElasticsearchRepository;

    /**
     * Dual Write: Saves product into MySQL first (Primary Database),
     * then indexes the product document into Elasticsearch (Search Engine).
     */
    @Transactional
    public ProductEntity createProduct(ProductRequestDto dto) {
        // 1. Save to MySQL
        ProductEntity entity = ProductEntity.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .category(dto.getCategory())
                .brand(dto.getBrand())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .build();

        ProductEntity savedEntity = productJpaRepository.save(entity);

        // 2. Map Entity to Elasticsearch Document & Save to ES Index
        ProductDocument document = mapToDocument(savedEntity);
        productElasticsearchRepository.save(document);

        return savedEntity;
    }

    /**
     * Bulk Sync: Reads all rows from MySQL and bulk-indexes them into Elasticsearch.
     * Useful for re-indexing or initial data migration.
     */
    public long syncAllFromMysqlToElasticsearch() {
        List<ProductEntity> entities = productJpaRepository.findAll();

        List<ProductDocument> documents = entities.stream()
                .map(this::mapToDocument)
                .collect(Collectors.toList());

        productElasticsearchRepository.saveAll(documents);
        return documents.size();
    }

    public List<ProductEntity> getAllProductsFromMysql() {
        return productJpaRepository.findAll();
    }

    public List<ProductDocument> getAllProductsFromElasticsearch() {
        Iterable<ProductDocument> iterable = productElasticsearchRepository.findAll();
        return StreamSupport.stream(iterable.spliterator(), false)
                .collect(Collectors.toList());
    }

    private ProductDocument mapToDocument(ProductEntity entity) {
        return ProductDocument.builder()
                .id(entity.getId().toString())
                .name(entity.getName())
                .description(entity.getDescription())
                .category(entity.getCategory())
                .brand(entity.getBrand())
                .price(entity.getPrice())
                .stock(entity.getStock())
                .build();
    }
}
