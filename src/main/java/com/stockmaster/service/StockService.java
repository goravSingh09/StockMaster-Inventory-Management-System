package com.stockmaster.service;

import com.stockmaster.dto.StockOperationDto;
import com.stockmaster.entity.Product;
import com.stockmaster.entity.StockTransaction;
import com.stockmaster.entity.TransactionType;
import com.stockmaster.exception.InsufficientStockException;
import com.stockmaster.exception.ResourceNotFoundException;
import com.stockmaster.repository.ProductRepository;
import com.stockmaster.repository.StockTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StockService {

    private final ProductRepository productRepository;
    private final StockTransactionRepository transactionRepository;
    private final DsaIndexService dsaIndexService;

    public StockService(ProductRepository productRepository,
                        StockTransactionRepository transactionRepository,
                        DsaIndexService dsaIndexService) {
        this.productRepository = productRepository;
        this.transactionRepository = transactionRepository;
        this.dsaIndexService = dsaIndexService;
    }

    /**
     * Executes atomic Stock In or Stock Out operation with strict negative stock prevention.
     */
    @Transactional
    public StockTransaction processStockOperation(StockOperationDto dto, String username) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + dto.getProductId()));

        int oldQuantity = product.getAvailableQuantity();
        int changeQty = dto.getQuantity();
        int newQuantity;

        TransactionType type = dto.getTransactionType();
        if (type == TransactionType.STOCK_IN) {
            newQuantity = oldQuantity + changeQty;
        } else if (type == TransactionType.STOCK_OUT) {
            if (oldQuantity < changeQty) {
                throw new InsufficientStockException(
                        "Cannot remove " + changeQty + " units. Current stock for '" +
                        product.getName() + "' is only " + oldQuantity + " units."
                );
            }
            newQuantity = oldQuantity - changeQty;
        } else {
            throw new IllegalArgumentException("Unsupported stock transaction type: " + type);
        }

        // Apply updated quantity to product
        product.setAvailableQuantity(newQuantity);
        Product savedProduct = productRepository.save(product);

        // Record stock transaction in database
        String ref = dto.getReferenceNumber() != null && !dto.getReferenceNumber().isBlank() ?
                dto.getReferenceNumber() : (type == TransactionType.STOCK_IN ? "IN-" : "OUT-") + System.currentTimeMillis();

        String reason = dto.getReason() != null && !dto.getReason().isBlank() ?
                dto.getReason() : (type == TransactionType.STOCK_IN ? "Regular stock replenishment" : "Manual inventory adjustment");

        StockTransaction tx = new StockTransaction(
                savedProduct,
                type,
                changeQty,
                oldQuantity,
                newQuantity,
                reason,
                ref,
                username != null ? username : "Admin"
        );
        StockTransaction savedTx = transactionRepository.save(tx);

        // Synchronize in-memory DSA indexes
        dsaIndexService.indexProduct(savedProduct);

        return savedTx;
    }

    public List<StockTransaction> getAllTransactions() {
        return transactionRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<StockTransaction> getRecentTransactions() {
        return transactionRepository.findTop10ByOrderByCreatedAtDesc();
    }

    public List<StockTransaction> filterTransactions(TransactionType type, Long productId,
                                                    LocalDateTime start, LocalDateTime end) {
        return transactionRepository.filterTransactions(type, productId, start, end);
    }
}
