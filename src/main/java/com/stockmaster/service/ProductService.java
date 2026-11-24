package com.stockmaster.service;

import com.stockmaster.dsa.BinarySearch;
import com.stockmaster.dsa.LinearSearch;
import com.stockmaster.dsa.MergeSort;
import com.stockmaster.dsa.QuickSort;
import com.stockmaster.dto.ProductDto;
import com.stockmaster.dto.SearchRequestDto;
import com.stockmaster.dto.SearchResponseDto;
import com.stockmaster.entity.Product;
import com.stockmaster.entity.StockTransaction;
import com.stockmaster.entity.TransactionType;
import com.stockmaster.exception.DuplicateResourceException;
import com.stockmaster.exception.ResourceNotFoundException;
import com.stockmaster.repository.ProductRepository;
import com.stockmaster.repository.SaleItemRepository;
import com.stockmaster.repository.StockTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final StockTransactionRepository transactionRepository;
    private final DsaIndexService dsaIndexService;
    private final SaleItemRepository saleItemRepository;

    public ProductService(ProductRepository productRepository,
                          StockTransactionRepository transactionRepository,
                          DsaIndexService dsaIndexService) {
        this(productRepository, transactionRepository, dsaIndexService, null);
    }

    @org.springframework.beans.factory.annotation.Autowired
    public ProductService(ProductRepository productRepository,
                          StockTransactionRepository transactionRepository,
                          DsaIndexService dsaIndexService,
                          @org.springframework.beans.factory.annotation.Autowired(required = false) SaleItemRepository saleItemRepository) {
        this.productRepository = productRepository;
        this.transactionRepository = transactionRepository;
        this.dsaIndexService = dsaIndexService;
        this.saleItemRepository = saleItemRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Retrieves products sorted using custom MergeSort or QuickSort implementations.
     */
    public List<Product> getProductsSorted(String sortBy, String direction, String algorithm) {
        List<Product> products = new ArrayList<>(productRepository.findAll());
        if (products.isEmpty()) {
            return products;
        }

        Comparator<Product> comparator = switch (sortBy != null ? sortBy.toLowerCase() : "code") {
            case "name" -> Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER);
            case "price" -> Comparator.comparing(Product::getSellingPrice);
            case "quantity" -> Comparator.comparing(Product::getAvailableQuantity);
            case "category" -> Comparator.comparing(Product::getCategory, String.CASE_INSENSITIVE_ORDER);
            default -> Comparator.comparing(Product::getProductCode);
        };

        if ("desc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }

        // Apply custom sorting algorithm
        if ("quick".equalsIgnoreCase(algorithm)) {
            QuickSort.sort(products, comparator);
        } else {
            MergeSort.sort(products, comparator);
        }

        return products;
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
    }

    public Product getProductByCode(String code) {
        // Fast path: try in-memory Hash Table first
        Product product = dsaIndexService.searchByHashTable(code);
        if (product != null) {
            return product;
        }
        return productRepository.findByProductCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with Code: " + code));
    }

    @Transactional
    public Product createProduct(ProductDto dto) {
        String code = dto.getProductCode().trim().toUpperCase();
        if (productRepository.existsByProductCode(code)) {
            throw new DuplicateResourceException("Product with code '" + code + "' already exists!");
        }

        Product product = new Product(
                code,
                dto.getName().trim(),
                dto.getCategory().trim(),
                dto.getDescription(),
                dto.getPurchasePrice(),
                dto.getSellingPrice(),
                dto.getAvailableQuantity(),
                dto.getMinStockLevel(),
                dto.getSupplier()
        );

        Product saved = productRepository.save(product);

        // Record initial stock setup in transaction history if quantity > 0
        if (saved.getAvailableQuantity() > 0) {
            StockTransaction tx = new StockTransaction(
                    saved,
                    TransactionType.INITIAL,
                    saved.getAvailableQuantity(),
                    0,
                    saved.getAvailableQuantity(),
                    "Initial stock creation",
                    "INIT-" + saved.getProductCode(),
                    "Admin"
            );
            transactionRepository.save(tx);
        }

        // Update in-memory DSA indexes
        dsaIndexService.indexProduct(saved);

        return saved;
    }

    @Transactional
    public Product updateProduct(Long id, ProductDto dto) {
        Product existing = getProductById(id);
        String newCode = dto.getProductCode().trim().toUpperCase();

        if (!existing.getProductCode().equalsIgnoreCase(newCode) &&
                productRepository.existsByProductCode(newCode)) {
            throw new DuplicateResourceException("Product code '" + newCode + "' is already in use by another product");
        }

        // Remove old key from DSA indexes if code changed
        if (!existing.getProductCode().equals(newCode)) {
            dsaIndexService.removeProduct(existing.getProductCode());
        }

        existing.setProductCode(newCode);
        existing.setName(dto.getName().trim());
        existing.setCategory(dto.getCategory().trim());
        existing.setDescription(dto.getDescription());
        existing.setPurchasePrice(dto.getPurchasePrice());
        existing.setSellingPrice(dto.getSellingPrice());
        existing.setMinStockLevel(dto.getMinStockLevel());
        existing.setSupplier(dto.getSupplier());

        Product updated = productRepository.save(existing);
        dsaIndexService.indexProduct(updated);

        return updated;
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        String code = product.getProductCode();

        if (saleItemRepository != null && saleItemRepository.existsByProductId(id)) {
            throw new IllegalStateException("Cannot delete product '" + product.getName() + "' because sales records reference it. To maintain financial audit history, products with sales cannot be deleted.");
        }

        // Clean up stock transactions before deleting product
        transactionRepository.deleteByProductId(id);

        productRepository.delete(product);
        dsaIndexService.removeProduct(code);
    }

    /**
     * Executes the requested search algorithm and tracks real nanosecond timings and comparisons.
     */
    public SearchResponseDto searchProducts(SearchRequestDto request) {
        String query = request.getQuery() != null ? request.getQuery().trim().toUpperCase() : "";
        SearchRequestDto.SearchAlgorithm algo = request.getAlgorithm() != null ?
                request.getAlgorithm() : SearchRequestDto.SearchAlgorithm.HASH_TABLE;

        if (query.isEmpty()) {
            return new SearchResponseDto(null, false, query, algo.getDisplayName(),
                    algo.getComplexity(), algo.getDescription(), 0, 0);
        }

        Product foundProduct = null;
        long executionTimeNs = 0;
        int comparisons = 0;

        switch (algo) {
            case HASH_TABLE -> {
                long start = System.nanoTime();
                foundProduct = dsaIndexService.searchByHashTable(query);
                executionTimeNs = System.nanoTime() - start;
                comparisons = 1; // 1 hash computation + bucket probe
            }
            case AVL_TREE -> {
                long start = System.nanoTime();
                foundProduct = dsaIndexService.searchByAVLTree(query);
                executionTimeNs = System.nanoTime() - start;
                // Height-bounded comparisons
                int treeHeight = dsaIndexService.getAVLTreeHeight();
                comparisons = Math.max(1, treeHeight);
            }
            case BINARY_SEARCH -> {
                // Get products sorted by code using custom MergeSort
                List<Product> sortedList = productRepository.findAll();
                MergeSort.sort(sortedList, Comparator.comparing(Product::getProductCode));

                BinarySearch.SearchResult<Product> result =
                        BinarySearch.search(sortedList, query, Product::getProductCode);

                foundProduct = result.getFoundItem();
                executionTimeNs = result.getExecutionTimeNs();
                comparisons = result.getComparisons();
            }
            case LINEAR_SEARCH -> {
                List<Product> list = productRepository.findAll();
                LinearSearch.SearchResult<Product> result =
                        LinearSearch.search(list, query, Product::getProductCode);

                foundProduct = result.getFoundItem();
                executionTimeNs = result.getExecutionTimeNs();
                comparisons = result.getComparisons();
            }
        }

        return new SearchResponseDto(
                foundProduct,
                foundProduct != null,
                query,
                algo.getDisplayName(),
                algo.getComplexity(),
                algo.getDescription(),
                executionTimeNs,
                comparisons
        );
    }

    public List<Product> getLowStockProducts() {
        return productRepository.findLowStockProducts();
    }

    public List<Product> getOutOfStockProducts() {
        return productRepository.findOutOfStockProducts();
    }

    public List<String> getAllCategories() {
        return productRepository.findDistinctCategories();
    }
}
