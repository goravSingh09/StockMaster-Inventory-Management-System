package com.stockmaster.service;

import com.stockmaster.dto.SaleItemRequestDto;
import com.stockmaster.dto.SaleRequestDto;
import com.stockmaster.entity.Product;
import com.stockmaster.entity.Sale;
import com.stockmaster.exception.InsufficientStockException;
import com.stockmaster.repository.ProductRepository;
import com.stockmaster.repository.SaleItemRepository;
import com.stockmaster.repository.SaleRepository;
import com.stockmaster.repository.StockTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("SaleService Checkout & Overselling Prevention Tests")
class SaleServiceTest {

    private SaleRepository saleRepository;
    private SaleItemRepository saleItemRepository;
    private ProductRepository productRepository;
    private StockTransactionRepository transactionRepository;
    private DsaIndexService dsaIndexService;
    private SaleService saleService;
    private Product laptop;

    @BeforeEach
    void setUp() {
        saleRepository = mock(SaleRepository.class);
        saleItemRepository = mock(SaleItemRepository.class);
        productRepository = mock(ProductRepository.class);
        transactionRepository = mock(StockTransactionRepository.class);
        dsaIndexService = new DsaIndexService();
        saleService = new SaleService(saleRepository, saleItemRepository, productRepository, transactionRepository, dsaIndexService);

        laptop = new Product(
                "ELC-01",
                "MacBook Pro",
                "Electronics",
                "Laptop",
                new BigDecimal("1500.00"),
                new BigDecimal("2000.00"),
                5,
                2,
                "Apple"
        );
        laptop.setId(10L);
    }

    @Test
    @DisplayName("Should successfully process sale, deduct inventory, and calculate bill")
    void testProcessSaleSuccess() {
        SaleRequestDto req = new SaleRequestDto();
        req.setCustomerName("Sunita Rao");
        req.setCustomerPhone("+91 99999 88888");
        req.setPaymentMethod("CARD");
        req.setDiscountAmount(new BigDecimal("100.00"));
        req.setItems(List.of(new SaleItemRequestDto(10L, 2)));

        when(productRepository.findById(10L)).thenReturn(Optional.of(laptop));
        when(saleRepository.save(any(Sale.class))).thenAnswer(i -> {
            Sale s = i.getArgument(0);
            s.setId(100L);
            return s;
        });

        Sale completed = saleService.processSale(req);

        assertNotNull(completed);
        assertEquals(3, laptop.getAvailableQuantity()); // 5 - 2 = 3
        assertEquals(new BigDecimal("4000.00"), completed.getTotalAmount()); // 2 * $2000
        assertEquals(new BigDecimal("100.00"), completed.getDiscountAmount());
        assertEquals(new BigDecimal("3900.00"), completed.getNetAmount());

        verify(productRepository).saveAll(any());
        verify(transactionRepository).saveAll(any());
    }

    @Test
    @DisplayName("Should prevent overselling when requested quantity exceeds available stock")
    void testOversellingPrevention() {
        SaleRequestDto req = new SaleRequestDto();
        req.setCustomerName("Test Customer");
        req.setItems(List.of(new SaleItemRequestDto(10L, 10))); // Only 5 available!

        when(productRepository.findById(10L)).thenReturn(Optional.of(laptop));

        assertThrows(InsufficientStockException.class, () -> saleService.processSale(req));

        assertEquals(5, laptop.getAvailableQuantity()); // Stock remains untouched
        verify(saleRepository, never()).save(any());
        verify(productRepository, never()).saveAll(any());
    }
}
