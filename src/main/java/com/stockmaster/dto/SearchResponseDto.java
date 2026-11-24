package com.stockmaster.dto;

import com.stockmaster.entity.Product;

public class SearchResponseDto {

    private Product product;
    private boolean found;
    private String query;
    private String algorithmUsed;
    private String timeComplexity;
    private String algorithmDescription;
    private long executionTimeNs;
    private double executionTimeMs;
    private int comparisonCount;

    public SearchResponseDto() {
    }

    public SearchResponseDto(Product product, boolean found, String query, String algorithmUsed,
                             String timeComplexity, String algorithmDescription,
                             long executionTimeNs, int comparisonCount) {
        this.product = product;
        this.found = found;
        this.query = query;
        this.algorithmUsed = algorithmUsed;
        this.timeComplexity = timeComplexity;
        this.algorithmDescription = algorithmDescription;
        this.executionTimeNs = executionTimeNs;
        this.executionTimeMs = executionTimeNs / 1_000_000.0;
        this.comparisonCount = comparisonCount;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public boolean isFound() {
        return found;
    }

    public void setFound(boolean found) {
        this.found = found;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getAlgorithmUsed() {
        return algorithmUsed;
    }

    public void setAlgorithmUsed(String algorithmUsed) {
        this.algorithmUsed = algorithmUsed;
    }

    public String getTimeComplexity() {
        return timeComplexity;
    }

    public void setTimeComplexity(String timeComplexity) {
        this.timeComplexity = timeComplexity;
    }

    public String getAlgorithmDescription() {
        return algorithmDescription;
    }

    public void setAlgorithmDescription(String algorithmDescription) {
        this.algorithmDescription = algorithmDescription;
    }

    public long getExecutionTimeNs() {
        return executionTimeNs;
    }

    public void setExecutionTimeNs(long executionTimeNs) {
        this.executionTimeNs = executionTimeNs;
        this.executionTimeMs = executionTimeNs / 1_000_000.0;
    }

    public double getExecutionTimeMs() {
        return executionTimeMs;
    }

    public int getComparisonCount() {
        return comparisonCount;
    }

    public void setComparisonCount(int comparisonCount) {
        this.comparisonCount = comparisonCount;
    }
}
