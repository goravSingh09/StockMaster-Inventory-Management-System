package com.stockmaster.dto;

public class SearchRequestDto {

    public enum SearchAlgorithm {
        HASH_TABLE("Hash Table Search", "O(1) Average", "Instant bucket indexing via hash function"),
        AVL_TREE("AVL Tree Search", "O(log N)", "Balanced binary search tree traversal"),
        BINARY_SEARCH("Binary Search", "O(log N)", "Divide-and-conquer on sorted product array"),
        LINEAR_SEARCH("Linear Search (Baseline)", "O(N)", "Sequential comparison from index 0");

        private final String displayName;
        private final String complexity;
        private final String description;

        SearchAlgorithm(String displayName, String complexity, String description) {
            this.displayName = displayName;
            this.complexity = complexity;
            this.description = description;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getComplexity() {
            return complexity;
        }

        public String getDescription() {
            return description;
        }
    }

    private String query;
    private SearchAlgorithm algorithm = SearchAlgorithm.HASH_TABLE;

    public SearchRequestDto() {
    }

    public SearchRequestDto(String query, SearchAlgorithm algorithm) {
        this.query = query;
        this.algorithm = algorithm;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public SearchAlgorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(SearchAlgorithm algorithm) {
        this.algorithm = algorithm;
    }
}
