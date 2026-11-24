package com.stockmaster.repository;

import com.stockmaster.entity.StockTransaction;
import com.stockmaster.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockTransactionRepository extends JpaRepository<StockTransaction, Long> {

    List<StockTransaction> findAllByOrderByCreatedAtDesc();

    List<StockTransaction> findTop10ByOrderByCreatedAtDesc();

    List<StockTransaction> findByProductIdOrderByCreatedAtDesc(Long productId);

    @org.springframework.data.jpa.repository.Modifying
    @Query("DELETE FROM StockTransaction t WHERE t.product.id = :productId")
    void deleteByProductId(@Param("productId") Long productId);

    List<StockTransaction> findByTransactionTypeOrderByCreatedAtDesc(TransactionType transactionType);

    @Query("SELECT t FROM StockTransaction t WHERE " +
           "(:type IS NULL OR t.transactionType = :type) AND " +
           "(:productId IS NULL OR t.product.id = :productId) AND " +
           "(:startDate IS NULL OR t.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR t.createdAt <= :endDate) " +
           "ORDER BY t.createdAt DESC")
    List<StockTransaction> filterTransactions(
            @Param("type") TransactionType type,
            @Param("productId") Long productId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
