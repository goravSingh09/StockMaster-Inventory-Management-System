package com.stockmaster.dsa;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Custom MergeSort & QuickSort Unit Tests")
class SortingTest {

    static class Product {
        String name;
        double price;
        int quantity;

        Product(String name, double price, int quantity) {
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }
    }

    @Test
    @DisplayName("MergeSort should sort correctly by price ascending")
    void testMergeSortByPrice() {
        List<Product> products = Arrays.asList(
                new Product("Monitor", 299.99, 10),
                new Product("Mouse", 25.50, 50),
                new Product("Laptop", 1299.00, 5),
                new Product("Keyboard", 75.00, 20)
        );

        MergeSort.sort(products, Comparator.comparingDouble(p -> p.price));

        assertEquals("Mouse", products.get(0).name);
        assertEquals(25.50, products.get(0).price);
        assertEquals("Keyboard", products.get(1).name);
        assertEquals(75.00, products.get(1).price);
        assertEquals("Monitor", products.get(2).name);
        assertEquals(299.99, products.get(2).price);
        assertEquals("Laptop", products.get(3).name);
        assertEquals(1299.00, products.get(3).price);
    }

    @Test
    @DisplayName("QuickSort should sort correctly by quantity descending")
    void testQuickSortByQuantity() {
        List<Product> products = Arrays.asList(
                new Product("A", 10.0, 5),
                new Product("B", 20.0, 100),
                new Product("C", 30.0, 45),
                new Product("D", 40.0, 80)
        );

        QuickSort.sort(products, (p1, p2) -> Integer.compare(p2.quantity, p1.quantity));

        assertEquals(100, products.get(0).quantity);
        assertEquals(80, products.get(1).quantity);
        assertEquals(45, products.get(2).quantity);
        assertEquals(5, products.get(3).quantity);
    }

    @Test
    @DisplayName("Both MergeSort and QuickSort should handle large random datasets identically")
    void testLargeRandomDataset() {
        Random rand = new Random(42);
        List<Integer> list1 = new ArrayList<>();
        List<Integer> list2 = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            int val = rand.nextInt(50000);
            list1.add(val);
            list2.add(val);
        }

        MergeSort.sort(list1, Integer::compareTo);
        QuickSort.sort(list2, Integer::compareTo);

        for (int i = 0; i < 1000; i++) {
            assertEquals(list1.get(i), list2.get(i));
        }

        // Verify sorted order
        for (int i = 0; i < 999; i++) {
            assertTrue(list1.get(i) <= list1.get(i + 1));
        }
    }
}
