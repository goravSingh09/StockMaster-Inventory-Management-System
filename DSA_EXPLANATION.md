# StockMaster — Advanced Data Structures & Algorithms (DSA) In-Depth Academic Guide

## 1. Introduction & Academic Objective

In enterprise inventory management systems, real-time response times for searching, sorting, and ranking directly determine system scalability. Rather than relying on standard Java collection wrappers (`HashMap`, `TreeMap`, `PriorityQueue`, `Arrays.sort()`), **StockMaster implements every foundational algorithm from scratch using pure Core Java** to demonstrate algorithmic rigor for academic evaluation and technical viva examination.

The system maintains a **dual architecture**:
1. **Persistent Relational Database (MySQL / JPA)**: Serves as the durable system of record.
2. **In-Memory Custom DSA Indexes (`DsaIndexService`)**: Synchronously maintained in RAM via write-through updates to power instantaneous $O(1)$ and $O(\log N)$ operations without incurring recurring disk I/O penalties.

---

## 2. Theoretical Complexity Matrix

| Data Structure / Algorithm | Operation | Best Case | Average Case | Worst Case | Space Complexity | Real-World Application in StockMaster |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Hash Table** (Separate Chaining) | Search / Insert / Delete | $O(1)$ | $O(1)$ | $O(N)$ | $O(N + M)$ | Instantaneous Product ID lookup |
| **AVL Tree** (Self-Balancing BST) | Search / Insert / Delete | $O(1)$ | $O(\log N)$ | $O(\log N)$ | $O(N)$ | Strictly balanced range retrieval & sorted traversal |
| **Binary Search** | Search (Sorted Array) | $O(1)$ | $O(\log N)$ | $O(\log N)$ | $O(1)$ | Iterative product search across sorted arrays |
| **Linear Search** | Search (Unsorted/Any) | $O(1)$ | $O(N)$ | $O(N)$ | $O(1)$ | Baseline performance benchmark |
| **Max Heap** (Binary Heap) | Build Heap | $O(N)$ | $O(N)$ | $O(N)$ | $O(N)$ | Linear-time heap construction via Floyd's method |
| **Max Heap** | Insert / Extract-Max | $O(1)$ / $O(1)$ | $O(\log N)$ | $O(\log N)$ | $O(1)$ | Dynamic ranking of Top-K best-selling products |
| **Merge Sort** | Sort | $O(N \log N)$ | $O(N \log N)$ | $O(N \log N)$ | $O(N)$ | Stable sorting by price, quantity, and name |
| **Quick Sort** (Median-of-3) | Sort | $O(N \log N)$ | $O(N \log N)$ | $O(N^2)$ | $O(\log N)$ | High-throughput in-place catalog sorting |

---

## 3. Data Structure 1: Hash Table with Separate Chaining

### 3.1 Class: `com.stockmaster.dsa.HashTable<K, V>`

A custom Hash Table providing associative key-value mapping with explicit collision resolution via **Separate Chaining** (linked list buckets).

```
Bucket Array (Capacity M)
[ 0 ] -> [ "PRD-1001", P1 ] -> null
[ 1 ] -> null
[ 2 ] -> [ "PRD-1025", P2 ] -> [ "PRD-1049", P3 ] -> null  (Collision handled by chaining)
...
[ M-1] -> [ "PRD-1033", P4 ] -> null
```

### 3.2 Key Algorithmic Features
1. **Hash Distribution Function**:
   Uses polynomial accumulation combined with bitwise masking to avoid negative modulo values:
   $$\text{index} = (\text{key.hashCode()} \ \& \ 0x7FFFFFFF) \pmod M$$
2. **Dynamic Rehashing**:
   - Initial capacity: $M = 16$
   - Maximum load factor: $\lambda_{\max} = 0.75$, where $\lambda = \frac{N}{M}$
   - When $\lambda \ge \lambda_{\max}$, the table dynamically doubles its capacity to $2M$, and every existing node is re-hashed and re-inserted into the newly sized bucket array. This preserves $O(1)$ amortized lookup guarantees.
3. **Collision Handling**:
   Entries in the same bucket form a singly-linked list (`HashNode<K, V>`). When inserting a duplicate key, the node value is updated in-place without creating orphan records.

### 3.3 Complexity Derivation
- **Time Complexity**:
  - Average Case: Assuming uniform hashing, the average chain length is $\alpha = \frac{N}{M} \le 0.75 \implies O(1)$.
  - Worst Case: When all keys collide into the same bucket, degenerate chain length is $N \implies O(N)$.
- **Space Complexity**: $O(N + M)$ where $M$ is the bucket array size and $N$ is the number of active key-value entries.

---

## 4. Data Structure 2: AVL Tree (Adelson-Velsky and Landis)

### 4.1 Class: `com.stockmaster.dsa.AVLTree<K extends Comparable<K>, V>`

An AVL tree is a height-balanced Binary Search Tree (BST) where the heights of the two child subtrees of any node differ by at most one.

$$\text{BalanceFactor}(N) = \text{height}(N.\text{left}) - \text{height}(N.\text{right}) \in \{-1, 0, +1\}$$

If $|\text{BalanceFactor}(N)| > 1$, the tree is rebalanced via one of four deterministic rotations.

### 4.2 The Four Balancing Rotations

#### Case 1: Left-Left (LL) Heavy $\implies$ Single Right Rotation
Occurs when an element is inserted into the left subtree of the left child.
```
        z                                      y
       / \                                   /   \
      y   T4      Right Rotate (z)          x     z
     / \          ===============>         / \   / \
    x   T3                                T1  T2 T3 T4
   / \
  T1  T2
```

#### Case 2: Right-Right (RR) Heavy $\implies$ Single Left Rotation
Occurs when an element is inserted into the right subtree of the right child.
```
    z                                          y
   / \                                       /   \
  T1  y           Left Rotate (z)           z     x
     / \          ==============>          / \   / \
    T2  x                                 T1  T2 T3 T4
       / \
      T3  T4
```

#### Case 3: Left-Right (LR) Heavy $\implies$ Double Rotation (Left Rotate child, then Right Rotate parent)
Occurs when an element is inserted into the right subtree of the left child.
```
      z                  z
     / \                / \                  x
    y   T4   Left(y)   x   T4   Right(z)   /   \
   / \       =====>   / \       ======>   y     z
  T1  x              y   T3              / \   / \
     / \            / \                 T1 T2 T3 T4
    T2  T3         T1  T2
```

#### Case 4: Right-Left (RL) Heavy $\implies$ Double Rotation (Right Rotate child, then Left Rotate parent)
Occurs when an element is inserted into the left subtree of the right child.
```
    z                  z
   / \                / \                    x
  T1  y     Right(y) T1  x      Left(z)    /   \
     / \    ======>     / \     ======>   z     y
    x   T4             T2  y             / \   / \
   / \                    / \           T1 T2 T3 T4
  T2  T3                 T3  T4
```

### 4.3 In-Order Traversal for Sorted Catalogs
Because the AVL tree satisfies the binary search tree invariant ($\text{left} < \text{root} < \text{right}$), an **in-order depth-first traversal** visits nodes in strictly ascending sorted order:
```java
private void inorderRec(AVLNode<K, V> node, List<V> list) {
    if (node != null) {
        inorderRec(node.left, list);
        list.add(node.value);
        inorderRec(node.right, list);
    }
}
```
This enables $O(N)$ generation of sorted product inventories directly from memory.

### 4.4 Height Bound & Complexity
- The maximum height of an AVL tree with $N$ nodes is strictly bounded:
  $$h < 1.4404 \log_2(N + 2) - 0.3277$$
- Guarantees $O(\log N)$ search, insertion, and deletion even under worst-case insertion sequences (e.g., pre-sorted product IDs).

---

## 5. Data Structure 3: Max Heap & Top-K Ranking

### 5.1 Class: `com.stockmaster.dsa.MaxHeap<T>`

A Max Heap is an array-backed Complete Binary Tree satisfying the **Heap Invariant**:
$$\text{parent}(i) \ge \text{child}(2i+1) \quad \text{and} \quad \text{parent}(i) \ge \text{child}(2i+2)$$

### 5.2 Array Mapping Formulation
For node index $i$ in a zero-indexed flat array:
- Parent index: $\lfloor(i - 1) / 2\rfloor$
- Left child index: $2i + 1$
- Right child index: $2i + 2$

### 5.3 Linear-Time Heap Construction (Floyd's Algorithm)
Instead of naively inserting $N$ elements into an empty heap (which takes $O(N \log N)$), `MaxHeap` implements **Floyd's `buildHeap` algorithm**, calling `heapifyDown` in reverse order from the last non-leaf node down to the root:
```java
public void buildHeap(List<T> list) {
    this.heap = new ArrayList<>(list);
    for (int i = (heap.size() / 2) - 1; i >= 0; i--) {
        heapifyDown(i);
    }
}
```

#### Mathematical Proof of $O(N)$ Time Complexity
Nodes at height $h$ can sink down at most $h$ levels. In a binary tree of height $H$:
$$\text{Total Work} = \sum_{h=0}^{\lfloor\log N\rfloor} \frac{N}{2^{h+1}} \cdot O(h) = \frac{N}{2} \sum_{h=0}^{\infty} \frac{h}{2^h}$$
Since the infinite arithmetic-geometric series $\sum_{h=0}^{\infty} \frac{h}{2^h} = 2$, the total operations equal:
$$\text{Work} = \frac{N}{2} \times 2 = O(N)$$

### 5.4 Top-K Best-Selling Product Extraction
To rank the Top $K$ best sellers (e.g., Top 5 or Top 10) without destroying the active heap structure, a temporary cloned heap extracts the maximum element $K$ times:
$$\text{Time Complexity} = O(N) + O(K \log N) = O(N + K \log N)$$
Since $K \ll N$ (typically $K = 5$ or $10$), this runs in virtually linear time, vastly outperforming a full $O(N \log N)$ catalog sort.

---

## 6. Search Algorithms Comparison

### 6.1 Implementations
1. **Linear Search (`LinearSearch.java`)**:
   Iterates element-by-element across the catalog until a match is found or end-of-list is reached.
   $$\text{Comparisons: } 1 \le C \le N, \quad \text{Average: } \frac{N+1}{2}$$
2. **Binary Search (`BinarySearch.java`)**:
   Operates over a pre-sorted collection by recursively halving the search interval:
   $$mid = low + \left\lfloor\frac{high - low}{2}\right\rfloor$$
   *(Using this formulation prevents integer arithmetic overflow that can occur with `(low + high) / 2`)*.
   $$\text{Comparisons: } \lfloor\log_2 N\rfloor + 1$$
3. **AVL Search**:
   Direct comparison through balanced tree height branches:
   $$\text{Comparisons: } \le 1.44 \log_2 N$$
4. **Hash Table Search**:
   Calculates hash bucket index and traverses the bucket chain:
   $$\text{Comparisons: } \approx 1 + \frac{\lambda}{2} \approx 1.375$$

---

## 7. Sorting Algorithms

### 7.1 Merge Sort (`MergeSort.java`)
A stable, divide-and-conquer sorting algorithm.
1. **Divide**: Split the list at midpoint into two halves: $T(N/2)$.
2. **Conquer**: Recursively sort each sublist.
3. **Combine**: Merge the two sorted lists using an auxiliary buffer in $O(N)$ linear time.

#### Recurrence Relation & Master Theorem
$$T(N) = 2T(N/2) + O(N)$$
Using the Master Theorem ($a = 2, b = 2, f(N) = N \implies N^{\log_2 2} = N^1$):
$$T(N) = \Theta(N \log N) \quad \text{across Best, Average, and Worst cases}$$

### 7.2 Quick Sort (`QuickSort.java`)
An in-place divide-and-conquer algorithm optimized with **Median-of-Three pivot selection**:
1. Sample elements at `low`, `mid`, and `high`.
2. Sort the three elements and place the median value as the partitioning pivot.
3. Partition the array such that all elements $\le \text{pivot}$ are on the left and elements $\ge \text{pivot}$ are on the right.
4. Recursively sort the partitions.

#### Why Median-of-Three Pivot Selection Matters
Naive Quick Sort (choosing the first or last element as pivot) degrades to worst-case $O(N^2)$ when inputs are already sorted or reverse-sorted. Median-of-three guarantees near-balanced partitions, preserving average-case $O(N \log N)$ execution even on pre-ordered inventory lists.

---

## 8. Empirical Performance Benchmarking (`DsaBenchmarkService`)

StockMaster includes an integrated empirical benchmarking engine running live experiments via `System.nanoTime()` across 4 standard data sizes:

$$\mathcal{D} \in \{100, \quad 1{,}000, \quad 5{,}000, \quad 10{,}000\}$$

### Benchmark Methodology:
1. **Synthetic Data Synthesis**: Generates unique, non-trivial `Product` models with randomized pricing, categories, and IDs.
2. **JVM Warm-up Iterations**: Executes 500 untimed burn-in cycles before recording metrics. This forces the HotSpot JVM to compile bytecodes to native machine instructions (JIT C1/C2 compilation) and populates processor branch predictors, eliminating JVM initialization noise.
3. **Multi-Trial Averaging**: Averages elapsed nanoseconds over 10 repeated trials.
4. **Metrics Reported**:
   - Time in nanoseconds ($\text{ns}$) and microseconds ($\mu\text{s}$).
   - Total element comparisons executed.
   - Algorithmic Big-O notation.
