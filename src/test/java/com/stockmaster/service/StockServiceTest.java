package com.stockmaster.service;

import com.stockmaster.dto.StockOperationDto;
import com.stockmaster.entity.Product;
import com.stockmaster.entity.StockTransaction;
import com.stockmaster.entity.TransactionType;
import com.stockmaster.exception.InsufficientStockException;
import com.stockmaster.repository.ProductRepository;
import com.stockmaster.repository.StockTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("StockService Inventory Validation Tests")
class StockServiceTest {

    private ProductRepository productRepository;
    private StockTransactionRepository transactionRepository;
    private DsaIndexService dsaIndexService;
    private StockService stockService;
    private Product product;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        transactionRepository = mock(StockTransactionRepository.class);
        dsaIndexService = new DsaIndexService();
        stockService = new StockService(productRepository, transactionRepository, dsaIndexService);

        product = new Product(
                "PRD-500",
                "Desk Fan",
                "Home Appliances",
                "Quiet fan",
                new BigDecimal("25.00"),
                new BigDecimal("45.00"),
                10,
                3,
                "CoolAir"
        );
        product.setId(5L);
    }

    @Test
    @DisplayName("Stock In should increase available stock and persist transaction")
    void testStockIn() {
        StockOperationDto dto = new StockOperationDto();
        dto.setProductId(5L);
        dto.setTransactionType(TransactionType.STOCK_IN);
        dto.setQuantity(15);
        dto.setReason("New batch shipment");
        dto.setReferenceNumber("PO-8821");

        when(productRepository.findById(5L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(transactionRepository.save(any(StockTransaction.class))).thenAnswer(i -> i.getArgument(0));

        StockTransaction tx = stockService.processStockOperation(dto, "Admin");

        assertNotNull(tx);
        assertEquals(25, product.getAvailableQuantity());
        assertEquals(TransactionType.STOCK_IN, tx.getTransactionType());
        assertEquals(15, tx.getQuantity());
        assertEquals(10, tx.getPreviousQuantity());
        assertEquals(25, tx.getNewQuantity());

        verify(productRepository).save(product);
        verify(transactionRepository).save(any(StockTransaction.class));
    }

    @Test
    @DisplayName("Stock Out should decrease stock when valid quantity is provided")
    void testStockOutSuccess() {
        StockOperationDto dto = new StockOperationDto();
        dto.setProductId(5L);
        dto.setTransactionType(TransactionType.STOCK_OUT);
        dto.setQuantity(4);

        when(productRepository.findById(5L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(transactionRepository.save(any(StockTransaction.class))).thenAnswer(i -> i.getArgument(0));

        StockTransaction tx = stockService.processStockOperation(dto, "Admin");

        assertNotNull(tx);
        assertEquals(6, product.getAvailableQuantity());
        assertEquals(10, tx.getPreviousQuantity());
        assertEquals(6, tx.getNewQuantity());
    }

    @Test
    @DisplayName("Stock Out should throw InsufficientStockException and block negative stock")
    void testStockOutInsufficientStock() {
        StockOperationDto dto = new StockOperationDto();
        dto.setProductId(5L);
        dto.setTransactionType(TransactionType.STOCK_OUT);
        dto.setQuantity(50); // Current stock is only 10

        when(productRepository.findById(5L)).thenReturn(Optional.of(product));

        assertThrows(InsufficientStockException.class, () -> stockService.processStockOperation(dto, "Admin"));
        assertEquals(10, product.getAvailableQuantity()); // Quantity must remain unchanged
        verify(productRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }
}
