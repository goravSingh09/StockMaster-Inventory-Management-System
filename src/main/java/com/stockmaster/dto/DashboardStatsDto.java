package com.stockmaster.dto;

import com.stockmaster.entity.Sale;
import com.stockmaster.entity.StockTransaction;
import com.stockmaster.entity.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class DashboardStatsDto {

    private long totalProducts;
    private long totalStockUnits;
    private BigDecimal totalInventoryValue;
    private long totalSalesCount;
    private BigDecimal totalRevenue;
    private long lowStockCount;
    private long outOfStockCount;

    private List<Product> lowStockProducts;
    private List<StockTransaction> recentTransactions;
    private List<Sale> recentSales;
    private List<ProductSalesDto> topSellingProducts;

    private Map<String, Long> categoryDistribution;
    private Map<String, BigDecimal> monthlySalesData;

    public DashboardStatsDto() {
    }

    public long getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(long totalProducts) {
        this.totalProducts = totalProducts;
    }

    public long getTotalStockUnits() {
        return totalStockUnits;
    }

    public void setTotalStockUnits(long totalStockUnits) {
        this.totalStockUnits = totalStockUnits;
    }

    public BigDecimal getTotalInventoryValue() {
        return totalInventoryValue;
    }

    public void setTotalInventoryValue(BigDecimal totalInventoryValue) {
        this.totalInventoryValue = totalInventoryValue;
    }

    public long getTotalSalesCount() {
        return totalSalesCount;
    }

    public void setTotalSalesCount(long totalSalesCount) {
        this.totalSalesCount = totalSalesCount;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public long getLowStockCount() {
        return lowStockCount;
    }

    public void setLowStockCount(long lowStockCount) {
        this.lowStockCount = lowStockCount;
    }

    public long getOutOfStockCount() {
        return outOfStockCount;
    }

    public void setOutOfStockCount(long outOfStockCount) {
        this.outOfStockCount = outOfStockCount;
    }

    public List<Product> getLowStockProducts() {
        return lowStockProducts;
    }

    public void setLowStockProducts(List<Product> lowStockProducts) {
        this.lowStockProducts = lowStockProducts;
    }

    public List<StockTransaction> getRecentTransactions() {
        return recentTransactions;
    }

    public void setRecentTransactions(List<StockTransaction> recentTransactions) {
        this.recentTransactions = recentTransactions;
    }

    public List<Sale> getRecentSales() {
        return recentSales;
    }

    public void setRecentSales(List<Sale> recentSales) {
        this.recentSales = recentSales;
    }

    public List<ProductSalesDto> getTopSellingProducts() {
        return topSellingProducts;
    }

    public void setTopSellingProducts(List<ProductSalesDto> topSellingProducts) {
        this.topSellingProducts = topSellingProducts;
    }

    public Map<String, Long> getCategoryDistribution() {
        return categoryDistribution;
    }

    public void setCategoryDistribution(Map<String, Long> categoryDistribution) {
        this.categoryDistribution = categoryDistribution;
    }

    public Map<String, BigDecimal> getMonthlySalesData() {
        return monthlySalesData;
    }

    public void setMonthlySalesData(Map<String, BigDecimal> monthlySalesData) {
        this.monthlySalesData = monthlySalesData;
    }
}
