package com.stockmaster.dto;

public class DsaBenchmarkResultDto {

    private String category; // "SEARCH" or "SORT"
    private String algorithmName;
    private int datasetSize;
    private long averageTimeNs;
    private double averageTimeMs;
    private long minTimeNs;
    private long maxTimeNs;
    private String timeComplexity;
    private String spaceComplexity;
    private int iterations;
    private String notes;

    public DsaBenchmarkResultDto() {
    }

    public DsaBenchmarkResultDto(String category, String algorithmName, int datasetSize,
                                long averageTimeNs, long minTimeNs, long maxTimeNs,
                                String timeComplexity, String spaceComplexity,
                                int iterations, String notes) {
        this.category = category;
        this.algorithmName = algorithmName;
        this.datasetSize = datasetSize;
        this.averageTimeNs = averageTimeNs;
        this.averageTimeMs = averageTimeNs / 1_000_000.0;
        this.minTimeNs = minTimeNs;
        this.maxTimeNs = maxTimeNs;
        this.timeComplexity = timeComplexity;
        this.spaceComplexity = spaceComplexity;
        this.iterations = iterations;
        this.notes = notes;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
    }

    public int getDatasetSize() {
        return datasetSize;
    }

    public void setDatasetSize(int datasetSize) {
        this.datasetSize = datasetSize;
    }

    public long getAverageTimeNs() {
        return averageTimeNs;
    }

    public void setAverageTimeNs(long averageTimeNs) {
        this.averageTimeNs = averageTimeNs;
        this.averageTimeMs = averageTimeNs / 1_000_000.0;
    }

    public double getAverageTimeMs() {
        return averageTimeMs;
    }

    public long getMinTimeNs() {
        return minTimeNs;
    }

    public void setMinTimeNs(long minTimeNs) {
        this.minTimeNs = minTimeNs;
    }

    public long getMaxTimeNs() {
        return maxTimeNs;
    }

    public void setMaxTimeNs(long maxTimeNs) {
        this.maxTimeNs = maxTimeNs;
    }

    public String getTimeComplexity() {
        return timeComplexity;
    }

    public void setTimeComplexity(String timeComplexity) {
        this.timeComplexity = timeComplexity;
    }

    public String getSpaceComplexity() {
        return spaceComplexity;
    }

    public void setSpaceComplexity(String spaceComplexity) {
        this.spaceComplexity = spaceComplexity;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
