# Elasticsearch Search Engine 🚀

A Spring Boot learning project demonstrating how to build a full-featured search engine using **Elasticsearch** as the secondary search index and **MySQL** as the primary source of truth.

---

## 💡 Key Features
- **Dual-Write Architecture**: Automatically saves transactional data to MySQL and syncs search documents into Elasticsearch.
- **Full-Text & Multi-Field Search**: Searches across titles and descriptions with **3x relevance boosting** on titles.
- **Fuzzy Search (Typo Tolerance)**: Corrects user typos on the fly (e.g., searching `"iphne"` finds `"iPhone"`).
- **Auto-Complete**: Instant prefix matching for search suggestions as you type.
- **Faceted Aggregations**: Real-time product counts grouped by category and brand.

---

## 🛠️ Prerequisites
- Java 21
- Docker & Docker Compose

---

## 🏃 How to Run

### 1. Start Docker Containers (Elasticsearch & MySQL)
```bash
docker compose up -d
```

### 2. Run the Spring Boot Server
```bash
./mvnw spring-boot:run
```
> **Note:** On first startup, the app automatically seeds sample products into MySQL and Elasticsearch!

---

## 🧪 Quick Test Endpoints (`curl`)

### 1. Typo-Tolerant Search (Fuzzy)
```bash
curl "http://localhost:8080/api/search/fuzzy?keyword=iphne"
```

### 2. Auto-Complete Suggestions
```bash
curl "http://localhost:8080/api/search/autocomplete?prefix=app"
```

### 3. Multi-Field Search (with Title Boosting)
```bash
curl "http://localhost:8080/api/search/multi-match?keyword=apple"
```

### 4. Category & Price Range Filter
```bash
curl "http://localhost:8080/api/search/filter?category=Electronics&minPrice=500&maxPrice=2000"
```

### 5. Faceted Navigation (Category & Brand Counts)
```bash
curl "http://localhost:8080/api/search/facets"
```

### 6. Create a New Product (Dual-Write)
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Sony PlayStation 5",
    "description": "Next-gen gaming console with ultra-high speed SSD",
    "category": "Gaming",
    "brand": "Sony",
    "price": 499.99,
    "stock": 30
  }'
```

### 7. Trigger Manual Bulk Sync (MySQL ➔ Elasticsearch)
```bash
curl -X POST http://localhost:8080/api/products/sync
```

---

## 📁 Project Structure
- `com.example.elasticsearch.entity.ProductEntity`: MySQL JPA entity.
- `com.example.elasticsearch.model.ProductDocument`: Elasticsearch document with `Text` & `Keyword` field mappings.
- `com.example.elasticsearch.service.ProductSearchService`: Native Elasticsearch query builders.
- `com.example.elasticsearch.config.DataLoader`: Sample dataset initializer.
