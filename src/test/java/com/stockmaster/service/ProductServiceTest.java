package com.stockmaster.service;

import com.stockmaster.dto.ProductDto;
import com.stockmaster.dto.SearchRequestDto;
import com.stockmaster.dto.SearchResponseDto;
import com.stockmaster.entity.Product;
import com.stockmaster.exception.DuplicateResourceException;
import com.stockmaster.exception.ResourceNotFoundException;
import com.stockmaster.repository.ProductRepository;
import com.stockmaster.repository.StockTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("ProductService Business Logic & DSA Integration Tests")
class ProductServiceTest {

    private ProductRepository productRepository;
    private StockTransactionRepository transactionRepository;
    private DsaIndexService dsaIndexService;
    private ProductService productService;
    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        transactionRepository = mock(StockTransactionRepository.class);
        dsaIndexService = new DsaIndexService();
        productService = new ProductService(productRepository, transactionRepository, dsaIndexService);

        sampleProduct = new Product(
                "TEST-101",
                "Ergonomic Keyboard",
                "Electronics",
                "Mechanical keys",
                new BigDecimal("50.00"),
                new BigDecimal("80.00"),
                20,
                5,
                "KeyTech"
        );
        sampleProduct.setId(1L);
    }

    @Test
    @DisplayName("Should successfully create a product and index it in DSA")
    void testCreateProductSuccess() {
        ProductDto dto = new ProductDto();
        dto.setProductCode("TEST-101");
        dto.setName("Ergonomic Keyboard");
        dto.setCategory("Electronics");
        dto.setDescription("Mechanical keys");
        dto.setPurchasePrice(new BigDecimal("50.00"));
        dto.setSellingPrice(new BigDecimal("80.00"));
        dto.setAvailableQuantity(20);
        dto.setMinStockLevel(5);
        dto.setSupplier("KeyTech");

        when(productRepository.existsByProductCode("TEST-101")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);

        Product created = productService.createProduct(dto);

        assertNotNull(created);
        assertEquals("TEST-101", created.getProductCode());
        verify(productRepository).save(any(Product.class));
        verify(transactionRepository).save(any());

        // Verify in-memory DSA index received product
        assertEquals(sampleProduct, dsaIndexService.searchByHashTable("TEST-101"));
        assertEquals(sampleProduct, dsaIndexService.searchByAVLTree("TEST-101"));
    }

    @Test
    @DisplayName("Should reject product creation if product code already exists")
    void testCreateDuplicateProductCode() {
        ProductDto dto = new ProductDto();
        dto.setProductCode("TEST-101");

        when(productRepository.existsByProductCode("TEST-101")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> productService.createProduct(dto));
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should find product by ID or throw exception")
    void testGetProductById() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        Product found = productService.getProductById(1L);
        assertNotNull(found);
        assertEquals("TEST-101", found.getProductCode());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(99L));
    }

    @Test
    @DisplayName("Should execute HashTable search in O(1) time")
    void testSearchHashTable() {
        dsaIndexService.indexProduct(sampleProduct);

        SearchRequestDto req = new SearchRequestDto("TEST-101", SearchRequestDto.SearchAlgorithm.HASH_TABLE);
        SearchResponseDto resp = productService.searchProducts(req);

        assertTrue(resp.isFound());
        assertEquals("TEST-101", resp.getProduct().getProductCode());
        assertEquals("Hash Table Search", resp.getAlgorithmUsed());
        assertEquals(1, resp.getComparisonCount());
    }

    @Test
    @DisplayName("Should execute AVL Tree search in O(log N) time")
    void testSearchAVLTree() {
        dsaIndexService.indexProduct(sampleProduct);

        SearchRequestDto req = new SearchRequestDto("TEST-101", SearchRequestDto.SearchAlgorithm.AVL_TREE);
        SearchResponseDto resp = productService.searchProducts(req);

        assertTrue(resp.isFound());
        assertEquals("TEST-101", resp.getProduct().getProductCode());
        assertEquals("AVL Tree Search", resp.getAlgorithmUsed());
    }

    @Test
    @DisplayName("Should execute Binary Search on sorted products")
    void testSearchBinarySearch() {
        Product p2 = new Product("TEST-102", "Mouse", "Electronics", "Wireless",
                new BigDecimal("20.00"), new BigDecimal("35.00"), 10, 2, "Logi");

        when(productRepository.findAll()).thenReturn(Arrays.asList(p2, sampleProduct));

        SearchRequestDto req = new SearchRequestDto("TEST-101", SearchRequestDto.SearchAlgorithm.BINARY_SEARCH);
        SearchResponseDto resp = productService.searchProducts(req);

        assertTrue(resp.isFound());
        assertEquals("TEST-101", resp.getProduct().getProductCode());
    }
}
