package com.stockmaster.repository;

import com.stockmaster.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByProductCode(String productCode);

    boolean existsByProductCode(String productCode);

    List<Product> findByCategory(String category);

    List<Product> findAllByOrderByCreatedAtDesc();

    @Query("SELECT p FROM Product p WHERE p.availableQuantity <= p.minStockLevel ORDER BY p.availableQuantity ASC")
    List<Product> findLowStockProducts();

    @Query("SELECT p FROM Product p WHERE p.availableQuantity <= 0")
    List<Product> findOutOfStockProducts();

    @Query("SELECT COALESCE(SUM(p.availableQuantity), 0) FROM Product p")
    Long sumTotalStockUnits();

    @Query("SELECT COALESCE(SUM(p.purchasePrice * p.availableQuantity), 0) FROM Product p")
    BigDecimal sumTotalInventoryValue();

    @Query("SELECT DISTINCT p.category FROM Product p ORDER BY p.category ASC")
    List<String> findDistinctCategories();

    @Query("SELECT p.category, COUNT(p), SUM(p.availableQuantity) FROM Product p GROUP BY p.category")
    List<Object[]> getCategoryStockSummary();
}
