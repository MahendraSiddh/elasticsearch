package com.example.elasticsearch.service;

import com.example.elasticsearch.dto.FacetResultDto;
import com.example.elasticsearch.model.ProductDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductSearchService {

        private final ElasticsearchOperations elasticsearchOperations;

        /**
         * 1. Simple Match Query on 'name' field
         */
        public List<ProductDocument> searchByName(String name) {
                NativeQuery query = NativeQuery.builder()
                                .withQuery(q -> q.match(m -> m
                                                .field("name")
                                                .query(name)))
                                .build();

                SearchHits<ProductDocument> searchHits = elasticsearchOperations.search(query, ProductDocument.class);
                return extractHits(searchHits);
        }

        /**
         * 2. Multi-Match Query searching across 'name' and 'description'
         * Boosts 'name' relevance by 3x (name^3)
         */
        public List<ProductDocument> searchMultiMatch(String keyword) {
                NativeQuery query = NativeQuery.builder()
                                .withQuery(q -> q.multiMatch(m -> m
                                                .fields("name^3", "description")
                                                .query(keyword)))
                                .build();

                SearchHits<ProductDocument> searchHits = elasticsearchOperations.search(query, ProductDocument.class);
                return extractHits(searchHits);
        }

        /**
         * 3. Fuzzy Search with Typo Tolerance (fuzziness = AUTO)
         * e.g., 'iphne' matches 'iPhone', 'samsng' matches 'Samsung'
         */
        public List<ProductDocument> searchFuzzy(String keyword) {
                NativeQuery query = NativeQuery.builder()
                                .withQuery(q -> q.match(m -> m
                                                .field("name")
                                                .query(keyword)
                                                .fuzziness("AUTO")))
                                .build();

                SearchHits<ProductDocument> searchHits = elasticsearchOperations.search(query, ProductDocument.class);
                return extractHits(searchHits);
        }

        /**
         * 4. Auto-Complete / Prefix Search on Name
         */
        public List<ProductDocument> searchAutoComplete(String prefix) {
                NativeQuery query = NativeQuery.builder()
                                .withQuery(q -> q.matchPhrasePrefix(m -> m
                                                .field("name")
                                                .query(prefix)))
                                .build();

                SearchHits<ProductDocument> searchHits = elasticsearchOperations.search(query, ProductDocument.class);
                return extractHits(searchHits);
        }

        /**
         * 5. Filter Query (Category exact match + Price range query)
         */
        public List<ProductDocument> searchByCategoryAndPriceRange(String category, Double minPrice, Double maxPrice) {
                NativeQuery query = NativeQuery.builder()
                                .withQuery(q -> q.bool(b -> {
                                        if (category != null && !category.isBlank()) {
                                                b.must(m -> m.term(t -> t.field("category").value(category)));
                                        }
                                        if (minPrice != null || maxPrice != null) {
                                                b.must(m -> m.range(r -> r.number(n -> {
                                                        n.field("price");
                                                        if (minPrice != null)
                                                                n.gte(minPrice);
                                                        if (maxPrice != null)
                                                                n.lte(maxPrice);
                                                        return n;
                                                })));
                                        }
                                        return b;
                                }))
                                .build();

                SearchHits<ProductDocument> searchHits = elasticsearchOperations.search(query, ProductDocument.class);
                return extractHits(searchHits);
        }

        /**
         * 6. Terms Aggregation (Faceted Search / Bucketing)
         * Counts documents per 'category' and 'brand'
         */
        public FacetResultDto getCategoryAndBrandFaceting() {
                NativeQuery query = NativeQuery.builder()
                                .withQuery(q -> q.matchAll(m -> m))
                                .withAggregation("category_facet",
                                                co.elastic.clients.elasticsearch._types.aggregations.Aggregation
                                                                .of(a -> a
                                                                                .terms(t -> t.field("category")
                                                                                                .size(20))))
                                .withAggregation("brand_facet",
                                                co.elastic.clients.elasticsearch._types.aggregations.Aggregation
                                                                .of(a -> a
                                                                                .terms(t -> t.field("brand").size(20))))
                                .build();

                SearchHits<ProductDocument> searchHits = elasticsearchOperations.search(query, ProductDocument.class);

                java.util.Map<String, Long> categoryCounts = new java.util.HashMap<>();
                java.util.Map<String, Long> brandCounts = new java.util.HashMap<>();

                if (searchHits.getAggregations() instanceof org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations aggregations) {
                        org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregation categoryAgg = aggregations
                                        .get("category_facet");
                        if (categoryAgg != null && categoryAgg.aggregation().getAggregate().isSterms()) {
                                categoryAgg.aggregation().getAggregate().sterms().buckets().array()
                                                .forEach(bucket -> categoryCounts.put(bucket.key().stringValue(),
                                                                bucket.docCount()));
                        }

                        org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregation brandAgg = aggregations
                                        .get("brand_facet");
                        if (brandAgg != null && brandAgg.aggregation().getAggregate().isSterms()) {
                                brandAgg.aggregation().getAggregate().sterms().buckets().array()
                                                .forEach(bucket -> brandCounts.put(bucket.key().stringValue(),
                                                                bucket.docCount()));
                        }
                }

                return com.example.elasticsearch.dto.FacetResultDto.builder()
                                .categoryCounts(categoryCounts)
                                .brandCounts(brandCounts)
                                .build();
        }

        private List<ProductDocument> extractHits(SearchHits<ProductDocument> searchHits) {
                return searchHits.stream()
                                .map(SearchHit::getContent)
                                .collect(Collectors.toList());
        }
}
