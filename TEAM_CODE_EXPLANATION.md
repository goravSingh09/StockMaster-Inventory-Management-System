# StockMaster — Complete Team Code Explanation & Architecture Guide
### University A6 Data Structures Project (Explanation in Hinglish & English)

Welcome to the **StockMaster** source code walkthrough. This document is written in clear, simple language so every member of your team can easily understand how the code works, explain it to professors during the project viva, and answer questions with complete confidence.

---

## 🏗️ 1. Project Overview & Architecture Flow

### System Architecture Flow (Request-Response Lifecycle)

```mermaid
flowchart TD
    User["User Browser / Client"] -->|HTTP GET/POST| Auth["AuthInterceptor (Session Verification)"]
    Auth -->|Valid Session| Controller["Spring Boot Controller (MVC)"]
    Auth -->|Invalid Session| Login["Redirect to /login"]
    
    Controller -->|DTO Validation| Service["Service Layer (Business Logic)"]
    Service -->|Atomic ACID Transaction| Repo["Spring Data JPA Repository"]
    Repo -->|SQL Queries| DB[("MySQL / Persistent Database")]
    
    Service -->|Synchronize Indexes| DSA["In-Memory Custom DSA Engine"]
    DSA -->|O(1) Lookup| Hash["HashTable (Separate Chaining)"]
    DSA -->|O(log N) Sorted| AVL["AVLTree (Self-Balancing BST)"]
    DSA -->|O(K log N) Ranking| Heap["MaxHeap (Priority Queue)"]
    
    Controller -->|Model Attributes| View["Thymeleaf Engine + Bootstrap 5 + Chart.js"]
    View -->|Rendered HTML UI| User
```

### Hinglish Explanation:
1. **Frontend:** Jab user browser mein koi button click karta hai (jaise "Add Product" ya "Execute Search"), to ek HTTP Request Spring Boot backend par aati hai.
2. **Security (`AuthInterceptor.java`):** Sabse pehle interceptor check karta hai ki user logged in hai ya nahi. Agar session nahi hai, to direct `/login` par redirect kar deta hai.
3. **Controller (`controller/*`):** Request ko accept karta hai, input validate karta hai, aur task ko respective `Service` ko hand-over karta hai.
4. **Service (`service/*`):** Ye business logic ka dimaag hai. Yahan actual checks hote hain (jaise stock negative na ho, duplicate code na bane).
5. **Database (`repository/*`):** Service layer persistent changes ko database table mein save karti hai.
6. **In-Memory DSA (`DsaIndexService.java`):** Database update hone ke turant baad humare custom `HashTable`, `AVLTree`, aur `MaxHeap` ko update kiya jata hai taaki next search memory se instant $O(1)$ ya $O(\log N)$ mein ho sake.
7. **View (`templates/*`):** Thymeleaf model data ko HTML, Bootstrap cards, aur Chart.js graphs mein render karke user ko responsive UI dikhata hai.

---

## 📁 2. Package Structure & File Responsibilities

```text
com.stockmaster
├── config
│   ├── AuthInterceptor.java     # Session-based authentication guard
│   ├── DataInitializer.java    # Automatic DB seeding (35 products, sales, admin)
│   └── WebMvcConfig.java       # Interceptor & static resource route config
├── controller
│   ├── AuthController.java     # Sign in & sign out endpoints
│   ├── DashboardController.java# Analytics command center & charts
│   ├── ProductController.java  # Product CRUD, category filters, custom sorting
│   ├── SearchController.java   # 4 algorithm comparison search page
│   ├── StockController.java    # Stock In/Out movements & validations
│   ├── SaleController.java     # POS billing, invoice generation, MaxHeap rankings
│   ├── HistoryController.java  # Comprehensive audit trail & date filtering
│   └── DsaAnalysisController.java# Real-time System.nanoTime() benchmarking page
├── dsa
│   ├── HashTable.java          # Custom Hash Table with Separate Chaining & Rehashing
│   ├── AVLTree.java            # Custom Self-Balancing BST with 4 Rotations
│   ├── MaxHeap.java            # Custom Priority Queue with Floyd's BuildHeap & Top-K
│   ├── BinarySearch.java       # Custom Iterative Divide-and-Conquer Search
│   ├── LinearSearch.java       # Baseline Sequential Search for Comparison
│   ├── MergeSort.java          # Custom Divide-and-Conquer Stable Sort
│   └── QuickSort.java          # Custom In-Place Median-of-Three Partition Sort
├── dto                         # Data Transfer Objects & Form Validations
├── entity                      # JPA Database Entities (Product, Sale, StockTransaction, etc.)
├── exception                   # Custom Exceptions & Centralized ControllerAdvice
├── repository                  # Spring Data JPA Repository Interfaces
└── service                     # Business Logic & DSA Index Synchronization
```

---

## 🧠 3. Step-by-Step Explanation of Custom DSA Classes

### A. Custom Hash Table (`HashTable.java`)
- **Key Methods:**
  - `put(K key, V value)`: Key ka hash code calculate karke bucket index nikalta hai (`abs(hash % capacity)`). Agar key already exist karti hai to value update karta hai. Agar collision hota hai, to linked list ke head par naya node attach karta hai.
  - `rehash()`: Jab $\frac{\text{size}}{\text{capacity}} \ge 0.75$ hota hai, table size double ho jati hai aur sabhi elements dubara re-distribute hote hain.
  - `get(K key)`: $O(1)$ average time mein bucket linked list traverse karke value return karta hai.
- **Why in StockMaster:** Product code (e.g., `ELC-1002`) enter karte hi instant $O(1)$ time mein product mil jata hai.

### B. Custom AVL Tree (`AVLTree.java`)
- **Key Methods:**
  - `insert(K key, V value)`: Standard BST insertion karta hai aur recursion unwind hote waqt node heights update karke balance factor check karta hai:
    $$\text{Balance Factor} = \text{height(left)} - \text{height(right)}$$
  - `leftRotate()` & `rightRotate()`: Pointers manipulate karke unbalanced subtree ko balance karta hai.
  - `inorderTraversal()`: Left subtree, then Root, then Right subtree traverse karke items ko alphabetically sorted order mein return karta hai.
- **Why in StockMaster:** Sorted product display aur guaranteed $O(\log N)$ search time.

### C. Custom Max Heap (`MaxHeap.java`)
- **Key Methods:**
  - `insert(T item)`: Array ke end mein element daalta hai aur `heapifyUp()` call karta hai.
  - `extractMax()`: Root element (highest units sold) remove karta hai, last element ko root par swap karta hai, aur `heapifyDown()` call karta hai.
  - `getTopK(int k)`: Original heap ko affect kiye bina ek copy banakar Top $K$ elements extract karta hai in $O(K \log N)$ time.
- **Why in StockMaster:** Best-Selling Products ranking bina pure database ko sort kiye.

### D. Custom Binary Search (`BinarySearch.java`)
- **Key Methods:**
  - `search(List<T> sortedList, K targetKey)`: Iterative pointer method (`low`, `high`, `mid = low + (high - low) / 2`).
  - Har iteration par search space half ho jata hai.
- **Why in StockMaster:** Pre-sorted collections mein $O(\log N)$ fast lookup demonstration.

### E. Custom Merge Sort & Quick Sort (`MergeSort.java` & `QuickSort.java`)
- `MergeSort`: List ko divide karke recursively sort karta hai aur auxiliary buffer array mein merge karta hai ($O(N \log N)$ stable).
- `QuickSort`: Median-of-three (first, middle, last) pivot pick karke elements ko partition karta hai ($O(N \log N)$ in-place).

---

## 💳 4. Sales & Inventory Lifecycle Example

Jab ek customer item buy karta hai:
1. User **New Sale** page par jata hai.
2. Form submit hone par `SaleService.processSale()` trigger hota hai.
3. Method `@Transactional` hai:
   - Agar product ka stock $10$ hai aur customer ne $12$ maang liye, to `InsufficientStockException` throw hoti hai aur transaction cancel ho jata hai.
   - Agar stock sufficient hai, to stock deduct hota hai ($10 - 2 = 8$).
   - `sales` table mein master bill save hota hai (`INV-2026-XXXX`).
   - `sale_items` table mein itemized lines save hoti hain.
   - `stock_transactions` table mein audit log create hota hai type `SALE` ke saath.
   - In-memory `HashTable` aur `AVLTree` mein product ka updated stock reflect ho jata hai.
   - `MaxHeap` ko refresh kiya jata hai taaki best seller ranks automatically update ho jayein!
