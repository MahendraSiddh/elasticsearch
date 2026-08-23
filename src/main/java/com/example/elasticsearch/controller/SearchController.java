package com.example.elasticsearch.controller;

import com.example.elasticsearch.dto.FacetResultDto;
import com.example.elasticsearch.model.ProductDocument;
import com.example.elasticsearch.service.ProductSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final ProductSearchService productSearchService;

    @GetMapping("/match")
    public ResponseEntity<List<ProductDocument>> searchByName(@RequestParam String name) {
        return ResponseEntity.ok(productSearchService.searchByName(name));
    }

    @GetMapping("/multi-match")
    public ResponseEntity<List<ProductDocument>> searchMultiMatch(@RequestParam String keyword) {
        return ResponseEntity.ok(productSearchService.searchMultiMatch(keyword));
    }

    @GetMapping("/fuzzy")
    public ResponseEntity<List<ProductDocument>> searchFuzzy(@RequestParam String keyword) {
        return ResponseEntity.ok(productSearchService.searchFuzzy(keyword));
    }

    @GetMapping("/autocomplete")
    public ResponseEntity<List<ProductDocument>> searchAutoComplete(@RequestParam String prefix) {
        return ResponseEntity.ok(productSearchService.searchAutoComplete(prefix));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<ProductDocument>> searchByCategoryAndPriceRange(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {
        return ResponseEntity.ok(productSearchService.searchByCategoryAndPriceRange(category, minPrice, maxPrice));
    }

    @GetMapping("/facets")
    public ResponseEntity<FacetResultDto> getCategoryAndBrandFacets() {
        return ResponseEntity.ok(productSearchService.getCategoryAndBrandFaceting());
    }
}
