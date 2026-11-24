package com.stockmaster.service;

import com.stockmaster.dto.ProductSalesDto;
import com.stockmaster.dto.SaleItemRequestDto;
import com.stockmaster.dto.SaleRequestDto;
import com.stockmaster.entity.*;
import com.stockmaster.exception.InsufficientStockException;
import com.stockmaster.exception.ResourceNotFoundException;
import com.stockmaster.repository.ProductRepository;
import com.stockmaster.repository.SaleItemRepository;
import com.stockmaster.repository.SaleRepository;
import com.stockmaster.repository.StockTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final ProductRepository productRepository;
    private final StockTransactionRepository transactionRepository;
    private final DsaIndexService dsaIndexService;

    public SaleService(SaleRepository saleRepository,
                       SaleItemRepository saleItemRepository,
                       ProductRepository productRepository,
                       StockTransactionRepository transactionRepository,
                       DsaIndexService dsaIndexService) {
        this.saleRepository = saleRepository;
        this.saleItemRepository = saleItemRepository;
        this.productRepository = productRepository;
        this.transactionRepository = transactionRepository;
        this.dsaIndexService = dsaIndexService;
    }

    /**
     * Executes atomic sale checkout:
     * 1. Validates sufficient stock for all requested items (prevents overselling).
     * 2. Decrements available stock for each product.
     * 3. Creates Sale, SaleItem records, and audit StockTransaction records.
     * 4. Updates in-memory DSA indexes and recalculates MaxHeap bestsellers.
     */
    @Transactional
    public Sale processSale(SaleRequestDto request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cannot create an empty sale invoice");
        }

        BigDecimal calculatedTotal = BigDecimal.ZERO;
        List<SaleItem> itemsToSave = new ArrayList<>();
        List<Product> productsToUpdate = new ArrayList<>();
        List<StockTransaction> transactionsToSave = new ArrayList<>();

        // Generate unique invoice number: INV-yyyyMMdd-XXXX
        String invoiceNumber = "INV-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));

        for (SaleItemRequestDto itemDto : request.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + itemDto.getProductId()));

            int requestedQty = itemDto.getQuantity();
            int currentStock = product.getAvailableQuantity();

            if (currentStock < requestedQty) {
                throw new InsufficientStockException(
                        "Overselling prevented: Product '" + product.getName() + "' (Code: " +
                        product.getProductCode() + ") has only " + currentStock +
                        " units in stock, but " + requestedQty + " were requested."
                );
            }

            BigDecimal unitPrice = product.getSellingPrice();
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(requestedQty));
            calculatedTotal = calculatedTotal.add(subtotal);

            // Decrement inventory stock
            int newStock = currentStock - requestedQty;
            product.setAvailableQuantity(newStock);
            productsToUpdate.add(product);

            // Prepare SaleItem
            SaleItem saleItem = new SaleItem(product, requestedQty, unitPrice, subtotal);
            itemsToSave.add(saleItem);

            // Prepare Stock Transaction Audit log
            StockTransaction tx = new StockTransaction(
                    product,
                    TransactionType.SALE,
                    requestedQty,
                    currentStock,
                    newStock,
                    "Sale checkout: " + invoiceNumber,
                    invoiceNumber,
                    "Admin"
            );
            transactionsToSave.add(tx);
        }

        BigDecimal discount = request.getDiscountAmount() != null ? request.getDiscountAmount() : BigDecimal.ZERO;
        BigDecimal netAmount = calculatedTotal.subtract(discount).max(BigDecimal.ZERO);

        Sale sale = new Sale(
                invoiceNumber,
                request.getCustomerName(),
                request.getCustomerPhone(),
                calculatedTotal,
                discount,
                netAmount,
                request.getPaymentMethod(),
                request.getNotes()
        );

        for (SaleItem item : itemsToSave) {
            sale.addItem(item);
        }

        Sale savedSale = saleRepository.save(sale);
        productRepository.saveAll(productsToUpdate);
        transactionRepository.saveAll(transactionsToSave);

        // Synchronize updated product records into in-memory DSA indexes
        for (Product p : productsToUpdate) {
            dsaIndexService.indexProduct(p);
        }

        // Refresh MaxHeap best-selling products ranking
        refreshBestSellingHeap();

        return savedSale;
    }

    /**
     * Refreshes the in-memory MaxHeap ranking from aggregated sales data.
     */
    public void refreshBestSellingHeap() {
        List<Object[]> aggregates = saleItemRepository.getProductSalesAggregates();
        List<ProductSalesDto> salesDtoList = new ArrayList<>();

        for (Object[] row : aggregates) {
            Long productId = (Long) row[0];
            String code = (String) row[1];
            String name = (String) row[2];
            String category = (String) row[3];
            Number totalSold = (Number) row[4];
            BigDecimal revenue = (BigDecimal) row[5];

            salesDtoList.add(new ProductSalesDto(
                    productId,
                    code,
                    name,
                    category,
                    totalSold != null ? totalSold.intValue() : 0,
                    revenue
            ));
        }

        dsaIndexService.updateSalesHeap(salesDtoList);
    }

    /**
     * Retrieves Top-K Best-Selling products ranked by the custom Max Heap!
     */
    public List<ProductSalesDto> getTopSellingProducts(int k) {
        return dsaIndexService.getTopSellingProducts(k);
    }

    public List<Sale> getAllSales() {
        return saleRepository.findAllByOrderBySaleDateDesc();
    }

    public List<Sale> getRecentSales() {
        return saleRepository.findTop10ByOrderBySaleDateDesc();
    }

    public Sale getSaleById(Long id) {
        return saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with ID: " + id));
    }

    public Sale getSaleByInvoice(String invoiceNumber) {
        return saleRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with Invoice: " + invoiceNumber));
    }

    public List<Sale> filterSales(LocalDateTime start, LocalDateTime end) {
        return saleRepository.filterSales(start, end);
    }
}
