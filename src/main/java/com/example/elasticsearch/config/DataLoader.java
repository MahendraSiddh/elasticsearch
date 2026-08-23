package com.example.elasticsearch.config;

import com.example.elasticsearch.dto.ProductRequestDto;
import com.example.elasticsearch.repository.ProductJpaRepository;
import com.example.elasticsearch.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final ProductService productService;
    private final ProductJpaRepository productJpaRepository;

    @Override
    public void run(String... args) {
        if (productJpaRepository.count() == 0) {
            log.info("Seeding initial product sample data into MySQL & Elasticsearch...");

            List<ProductRequestDto> sampleProducts = List.of(
                    ProductRequestDto.builder()
                            .name("Apple iPhone 15 Pro Max")
                            .description("Latest Apple flagship smartphone with Titanium body, A17 Pro chip, and 48MP camera")
                            .category("Electronics")
                            .brand("Apple")
                            .price(1199.99)
                            .stock(50)
                            .build(),
                    ProductRequestDto.builder()
                            .name("Apple MacBook Pro 16-inch")
                            .description("Powerful laptop with M3 Max chip, 36GB Unified Memory, and Liquid Retina XDR display")
                            .category("Electronics")
                            .brand("Apple")
                            .price(2499.00)
                            .stock(20)
                            .build(),
                    ProductRequestDto.builder()
                            .name("Samsung Galaxy S24 Ultra")
                            .description("Premium Android smartphone with Galaxy AI, S-Pen, and 200MP camera")
                            .category("Electronics")
                            .brand("Samsung")
                            .price(1299.99)
                            .stock(45)
                            .build(),
                    ProductRequestDto.builder()
                            .name("Sony WH-1000XM5 Wireless Headphones")
                            .description("Industry leading noise cancelling headphones with Alexa voice control")
                            .category("Audio")
                            .brand("Sony")
                            .price(398.00)
                            .stock(100)
                            .build(),
                    ProductRequestDto.builder()
                            .name("Nike Air Max 270 Sneakers")
                            .description("Comfortable lifestyle running shoes with large Air cushioning unit")
                            .category("Footwear")
                            .brand("Nike")
                            .price(150.00)
                            .stock(200)
                            .build(),
                    ProductRequestDto.builder()
                            .name("Adidas Ultraboost Light Running Shoes")
                            .description("Lightweight responsive running sneakers with Boost midsole technology")
                            .category("Footwear")
                            .brand("Adidas")
                            .price(190.00)
                            .stock(150)
                            .build()
            );

            for (ProductRequestDto dto : sampleProducts) {
                productService.createProduct(dto);
            }

            log.info("Successfully seeded sample products!");
        }
    }
}
