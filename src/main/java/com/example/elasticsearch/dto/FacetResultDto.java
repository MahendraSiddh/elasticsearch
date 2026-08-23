package com.example.elasticsearch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacetResultDto {
    private Map<String, Long> categoryCounts;
    private Map<String, Long> brandCounts;
}
