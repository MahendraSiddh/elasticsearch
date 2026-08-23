package com.example.elasticsearch.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "products")
public class ProductDocument {

    @Id
    private String id;

    // Analyzed for full-text search (lowercased & tokenized)
    @Field(type = FieldType.Text, analyzer = "standard")
    private String name;

    // Analyzed for full-text search
    @Field(type = FieldType.Text, analyzer = "standard")
    private String description;

    // Not analyzed (saved as exact string for filtering & aggregations)
    @Field(type = FieldType.Keyword)
    private String category;

    // Not analyzed (exact match for brand filter)
    @Field(type = FieldType.Keyword)
    private String brand;

    // Numeric field for sorting and range queries (e.g. price between 100 and 500)
    @Field(type = FieldType.Double)
    private Double price;

    // Numeric field
    @Field(type = FieldType.Integer)
    private Integer stock;
}
