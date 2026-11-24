package com.stockmaster.dsa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Custom MaxHeap Unit Tests")
class MaxHeapTest {

    static class ProductSale {
        String code;
        int unitsSold;

        ProductSale(String code, int unitsSold) {
            this.code = code;
            this.unitsSold = unitsSold;
        }
    }

    private MaxHeap<ProductSale> maxHeap;

    @BeforeEach
    void setUp() {
        // Compare by unitsSold descending (larger unitsSold has higher priority)
        maxHeap = new MaxHeap<>(Comparator.comparingInt(p -> p.unitsSold));
    }

    @Test
    @DisplayName("Should insert items and extract maximum in correct descending order")
    void testInsertAndExtractMax() {
        maxHeap.insert(new ProductSale("PRD-10", 25));
        maxHeap.insert(new ProductSale("PRD-20", 150));
        maxHeap.insert(new ProductSale("PRD-30", 80));
        maxHeap.insert(new ProductSale("PRD-40", 300));
        maxHeap.insert(new ProductSale("PRD-50", 10));

        assertEquals(5, maxHeap.size());
        assertEquals("PRD-40", maxHeap.peekMax().code);
        assertEquals(300, maxHeap.peekMax().unitsSold);

        ProductSale first = maxHeap.extractMax();
        assertEquals("PRD-40", first.code);
        assertEquals(300, first.unitsSold);

        ProductSale second = maxHeap.extractMax();
        assertEquals("PRD-20", second.code);
        assertEquals(150, second.unitsSold);

        ProductSale third = maxHeap.extractMax();
        assertEquals("PRD-30", third.code);
        assertEquals(80, third.unitsSold);

        assertEquals(2, maxHeap.size());
    }

    @Test
    @DisplayName("Should build heap in linear O(N) time and extract correctly")
    void testBuildHeap() {
        List<ProductSale> items = Arrays.asList(
                new ProductSale("A", 40),
                new ProductSale("B", 10),
                new ProductSale("C", 90),
                new ProductSale("D", 70),
                new ProductSale("E", 100)
        );

        maxHeap.buildHeap(items);
        assertEquals(5, maxHeap.size());
        assertEquals("E", maxHeap.peekMax().code);
        assertEquals(100, maxHeap.peekMax().unitsSold);
    }

    @Test
    @DisplayName("Should retrieve Top-K elements non-destructively")
    void testGetTopK() {
        maxHeap.insert(new ProductSale("P1", 10));
        maxHeap.insert(new ProductSale("P2", 50));
        maxHeap.insert(new ProductSale("P3", 30));
        maxHeap.insert(new ProductSale("P4", 100));
        maxHeap.insert(new ProductSale("P5", 70));

        List<ProductSale> top3 = maxHeap.getTopK(3);
        assertEquals(3, top3.size());
        assertEquals("P4", top3.get(0).code); // 100
        assertEquals("P5", top3.get(1).code); // 70
        assertEquals("P2", top3.get(2).code); // 50

        // Verify original heap was not drained
        assertEquals(5, maxHeap.size());
    }
}
