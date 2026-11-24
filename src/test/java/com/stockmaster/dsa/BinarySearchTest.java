package com.stockmaster.dsa;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Custom BinarySearch & LinearSearch Unit Tests")
class BinarySearchTest {

    static class ProductItem {
        String code;
        String name;

        ProductItem(String code, String name) {
            this.code = code;
            this.name = name;
        }
    }

    @Test
    @DisplayName("Should find element via iterative binary search in O(log N)")
    void testBinarySearchFound() {
        List<ProductItem> sortedProducts = Arrays.asList(
                new ProductItem("PRD-101", "USB Cable"),
                new ProductItem("PRD-102", "Wireless Mouse"),
                new ProductItem("PRD-103", "Mechanical Keyboard"),
                new ProductItem("PRD-104", "LED Monitor"),
                new ProductItem("PRD-105", "Gaming Chair"),
                new ProductItem("PRD-106", "Webcam 4K"),
                new ProductItem("PRD-107", "Desk Pad")
        );

        // Search for mid element
        BinarySearch.SearchResult<ProductItem> r1 = BinarySearch.search(
                sortedProducts, "PRD-104", p -> p.code
        );
        assertTrue(r1.isFound());
        assertEquals(3, r1.getIndex());
        assertEquals("LED Monitor", r1.getFoundItem().name);
        assertTrue(r1.getComparisons() <= 3);

        // Search for first element
        BinarySearch.SearchResult<ProductItem> r2 = BinarySearch.search(
                sortedProducts, "PRD-101", p -> p.code
        );
        assertTrue(r2.isFound());
        assertEquals(0, r2.getIndex());
        assertEquals("USB Cable", r2.getFoundItem().name);

        // Search for last element
        BinarySearch.SearchResult<ProductItem> r3 = BinarySearch.search(
                sortedProducts, "PRD-107", p -> p.code
        );
        assertTrue(r3.isFound());
        assertEquals(6, r3.getIndex());
        assertEquals("Desk Pad", r3.getFoundItem().name);
    }

    @Test
    @DisplayName("Should return not found when key does not exist")
    void testBinarySearchNotFound() {
        List<ProductItem> sortedProducts = Arrays.asList(
                new ProductItem("PRD-10", "A"),
                new ProductItem("PRD-20", "B"),
                new ProductItem("PRD-30", "C")
        );

        BinarySearch.SearchResult<ProductItem> result = BinarySearch.search(
                sortedProducts, "PRD-99", p -> p.code
        );
        assertFalse(result.isFound());
        assertEquals(-1, result.getIndex());
        assertNull(result.getFoundItem());
    }

    @Test
    @DisplayName("LinearSearch should correctly find or fail on elements")
    void testLinearSearch() {
        List<ProductItem> list = Arrays.asList(
                new ProductItem("PRD-50", "Chair"),
                new ProductItem("PRD-10", "Desk"),
                new ProductItem("PRD-30", "Lamp")
        );

        LinearSearch.SearchResult<ProductItem> r = LinearSearch.search(
                list, "PRD-10", p -> p.code
        );
        assertTrue(r.isFound());
        assertEquals(1, r.getIndex());
        assertEquals("Desk", r.getFoundItem().name);
        assertEquals(2, r.getComparisons()); // Found on second comparison
    }
}
