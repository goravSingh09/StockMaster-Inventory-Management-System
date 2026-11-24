package com.stockmaster.service;

import com.stockmaster.dto.DashboardStatsDto;
import com.stockmaster.dto.ProductSalesDto;
import com.stockmaster.entity.Product;
import com.stockmaster.entity.Sale;
import com.stockmaster.entity.StockTransaction;
import com.stockmaster.repository.ProductRepository;
import com.stockmaster.repository.SaleRepository;
import com.stockmaster.repository.StockTransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class DashboardService {

    private final ProductRepository productRepository;
    private final SaleRepository saleRepository;
    private final StockTransactionRepository transactionRepository;
    private final SaleService saleService;

    public DashboardService(ProductRepository productRepository,
                            SaleRepository saleRepository,
                            StockTransactionRepository transactionRepository,
                            SaleService saleService) {
        this.productRepository = productRepository;
        this.saleRepository = saleRepository;
        this.transactionRepository = transactionRepository;
        this.saleService = saleService;
    }

    public DashboardStatsDto getDashboardStats() {
        DashboardStatsDto stats = new DashboardStatsDto();

        // High level KPI counts
        stats.setTotalProducts(productRepository.count());
        Long totalUnits = productRepository.sumTotalStockUnits();
        stats.setTotalStockUnits(totalUnits != null ? totalUnits : 0L);

        BigDecimal inventoryValue = productRepository.sumTotalInventoryValue();
        stats.setTotalInventoryValue(inventoryValue != null ? inventoryValue : BigDecimal.ZERO);

        Long totalSales = saleRepository.countTotalSales();
        stats.setTotalSalesCount(totalSales != null ? totalSales : 0L);

        BigDecimal totalRevenue = saleRepository.sumTotalRevenue();
        stats.setTotalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO);

        // Low stock and out of stock lists
        List<Product> lowStock = productRepository.findLowStockProducts();
        stats.setLowStockProducts(lowStock);
        stats.setLowStockCount(lowStock.size());

        List<Product> outOfStock = productRepository.findOutOfStockProducts();
        stats.setOutOfStockCount(outOfStock.size());

        // Recent tables
        List<StockTransaction> recentTx = transactionRepository.findTop10ByOrderByCreatedAtDesc();
        stats.setRecentTransactions(recentTx);

        List<Sale> recentSales = saleRepository.findTop10ByOrderBySaleDateDesc();
        stats.setRecentSales(recentSales);

        // Category distribution for Chart.js Doughnut Chart
        Map<String, Long> categoryMap = new LinkedHashMap<>();
        List<Object[]> categoryData = productRepository.getCategoryStockSummary();
        for (Object[] row : categoryData) {
            String cat = (String) row[0];
            Number count = (Number) row[1];
            categoryMap.put(cat, count != null ? count.longValue() : 0L);
        }
        stats.setCategoryDistribution(categoryMap);

        // Monthly sales trend for Chart.js Bar/Line Chart
        Map<String, BigDecimal> monthlySales = new LinkedHashMap<>();
        List<Sale> allSales = saleRepository.findAll();
        DateTimeFormatter monthYearFmt = DateTimeFormatter.ofPattern("MMM yyyy");

        // Seed recent months
        for (Sale sale : allSales) {
            String key = sale.getSaleDate().format(monthYearFmt);
            monthlySales.put(key, monthlySales.getOrDefault(key, BigDecimal.ZERO).add(sale.getNetAmount()));
        }
        stats.setMonthlySalesData(monthlySales);

        // Top 5 Best-Selling Products from custom Max Heap
        List<ProductSalesDto> topSelling = saleService.getTopSellingProducts(5);
        stats.setTopSellingProducts(topSelling);

        return stats;
    }
}
