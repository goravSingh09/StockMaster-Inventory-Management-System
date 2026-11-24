package com.stockmaster.repository;

import com.stockmaster.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    Optional<Sale> findByInvoiceNumber(String invoiceNumber);

    List<Sale> findAllByOrderBySaleDateDesc();

    List<Sale> findTop10ByOrderBySaleDateDesc();

    @Query("SELECT COALESCE(SUM(s.netAmount), 0) FROM Sale s")
    BigDecimal sumTotalRevenue();

    @Query("SELECT COUNT(s) FROM Sale s")
    Long countTotalSales();

    @Query("SELECT s FROM Sale s WHERE " +
           "(:startDate IS NULL OR s.saleDate >= :startDate) AND " +
           "(:endDate IS NULL OR s.saleDate <= :endDate) " +
           "ORDER BY s.saleDate DESC")
    List<Sale> filterSales(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
