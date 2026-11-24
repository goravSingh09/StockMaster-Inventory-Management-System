package com.stockmaster.repository;

import com.stockmaster.entity.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {

    List<SaleItem> findBySaleId(Long saleId);

    boolean existsByProductId(Long productId);

    @Query("SELECT item.product.id, item.product.productCode, item.product.name, " +
           "item.product.category, SUM(item.quantity), SUM(item.subtotal) " +
           "FROM SaleItem item " +
           "GROUP BY item.product.id, item.product.productCode, item.product.name, item.product.category")
    List<Object[]> getProductSalesAggregates();
}
