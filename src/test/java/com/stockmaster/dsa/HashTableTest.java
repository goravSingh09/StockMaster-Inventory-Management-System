package com.stockmaster.dsa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Custom HashTable Unit Tests")
class HashTableTest {

    private HashTable<String, Integer> hashTable;

    @BeforeEach
    void setUp() {
        hashTable = new HashTable<>(4); // Small initial capacity to force rehashing early
    }

    @Test
    @DisplayName("Should insert and retrieve key-value pairs successfully")
    void testPutAndGet() {
        hashTable.put("PRD-101", 50);
        hashTable.put("PRD-102", 120);
        hashTable.put("PRD-103", 15);

        assertEquals(3, hashTable.size());
        assertEquals(50, hashTable.get("PRD-101"));
        assertEquals(120, hashTable.get("PRD-102"));
        assertEquals(15, hashTable.get("PRD-103"));
        assertNull(hashTable.get("NON_EXISTENT"));
    }

    @Test
    @DisplayName("Should update existing value when duplicate key is inserted")
    void testUpdateDuplicateKey() {
        hashTable.put("PRD-101", 50);
        Integer oldValue = hashTable.put("PRD-101", 99);

        assertEquals(50, oldValue);
        assertEquals(99, hashTable.get("PRD-101"));
        assertEquals(1, hashTable.size()); // Size should remain 1
    }

    @Test
    @DisplayName("Should remove key successfully and reduce size")
    void testRemoveKey() {
        hashTable.put("PRD-101", 50);
        hashTable.put("PRD-102", 120);

        Integer removed = hashTable.remove("PRD-101");
        assertEquals(50, removed);
        assertNull(hashTable.get("PRD-101"));
        assertFalse(hashTable.containsKey("PRD-101"));
        assertEquals(1, hashTable.size());

        assertNull(hashTable.remove("NOT_FOUND"));
    }

    @Test
    @DisplayName("Should automatically rehash when load factor threshold is crossed")
    void testRehashing() {
        int initialCap = hashTable.getCapacity();

        // Insert enough items to trigger rehashing (capacity 4, threshold 0.75 => triggers at 3 items)
        for (int i = 1; i <= 20; i++) {
            hashTable.put("KEY-" + i, i * 10);
        }

        assertEquals(20, hashTable.size());
        assertTrue(hashTable.getCapacity() > initialCap);

        // Verify all 20 elements remain retrievable after rehash
        for (int i = 1; i <= 20; i++) {
            assertEquals(i * 10, hashTable.get("KEY-" + i));
        }
    }

    @Test
    @DisplayName("Should retrieve all keys and values accurately")
    void testKeysAndValues() {
        hashTable.put("A", 1);
        hashTable.put("B", 2);
        hashTable.put("C", 3);

        List<String> keys = hashTable.keys();
        List<Integer> values = hashTable.values();

        assertEquals(3, keys.size());
        assertEquals(3, values.size());
        assertTrue(keys.contains("A") && keys.contains("B") && keys.contains("C"));
        assertTrue(values.contains(1) && values.contains(2) && values.contains(3));
    }
}
