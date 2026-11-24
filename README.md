# StockMaster — Enterprise Inventory Management System Using Advanced Data Structures

[![Java](https://img.shields.io/badge/Language-Java%2017%2B%20%2F%2025-ED8B00?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Framework-Spring%20Boot%203.3.5-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Frontend](https://img.shields.io/badge/Frontend-Thymeleaf%20%7C%20Bootstrap%205%20%7C%20Chart.js-8993be?logo=bootstrap&logoColor=white)](https://getbootstrap.com/)
[![Database](https://img.shields.io/badge/Database-MySQL%20%7C%20Spring%20Data%20JPA-4479A1?logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Testing](https://img.shields.io/badge/Testing-JUnit%205%20%2830%2F30%20Passed%29-25A162?logo=junit5&logoColor=white)](https://junit.org/junit5/)
[![License](https://img.shields.io/badge/Academic-University%20A6%20Project-blue)]()

> **University A6 Data Structures Project**: A commercial-grade, full-stack inventory management web application combining relational persistence with **ground-up Core Java implementations of custom advanced data structures and algorithms** (Hash Table, AVL Tree, Max Heap, Binary Search, Merge Sort, Quick Sort).

---

## 🏛️ Executive Summary

**StockMaster** bridges theoretical computer science and commercial enterprise software engineering. While modern web frameworks routinely hide data structures behind high-level database queries or standard library collections, StockMaster features an **integrated dual-layer architecture**:
1. **Durable Persistence Layer (MySQL / Spring Data JPA)**: Ensures all catalog items, inventory transactions, and sales invoices survive server restarts with full ACID compliance.
2. **In-Memory Algorithmic Index Layer (`com.stockmaster.dsa`)**: Reconstructs and synchronizes custom data structures in memory on startup and upon every mutation. Searches, sorting, and rankings operate directly on these algorithmic structures to achieve microsecond response times.

---

## 🚀 Mandatory Custom Data Structures & Algorithms

All five core algorithms are authored **completely from scratch in pure Core Java** (`src/main/java/com/stockmaster/dsa/`) with **zero reliance on `java.util.HashMap`, `TreeMap`, `PriorityQueue`, or `Collections.sort()`**:

| Custom Data Structure | Java Implementation | Algorithmic Mechanism | Time Complexity | Business Feature in StockMaster |
| :--- | :--- | :--- | :--- | :--- |
| **Hash Table** | `HashTable<K, V>` | Separate Chaining with Singly-Linked Nodes, Polynomial Accumulation Hash Code, Dynamic Rehashing ($2\times$ expansion when $\lambda \ge 0.75$). | **Search: $O(1)$**<br>Insert: $O(1)$<br>Delete: $O(1)$ | Instantaneous **Product ID Lookup** (`PRD-XXXX`). |
| **AVL Tree** | `AVLTree<K, V>` | Height-Balanced BST, Strict Invariant ($|\text{BF}| \le 1$), 4 Rotations (LL, RR, LR, RL), In-Order Depth-First Traversal. | **Search: $O(\log N)$**<br>Insert: $O(\log N)$<br>Delete: $O(\log N)$ | **Sorted Catalog Traversal** and balanced product indexing. |
| **Max Heap** | `MaxHeap<T>` | Array-Backed Complete Binary Tree, Parent-Child Arithmetic, `heapifyUp`, `heapifyDown`, **Floyd’s $O(N)$ Linear `buildHeap`**. | **Peek: $O(1)$**<br>Extract-Max: $O(\log N)$<br>Top-K: $O(N + K \log N)$ | **Best-Selling Products Ranking** (Top 5 & Top 10 by units sold). |
| **Binary Search** | `BinarySearch` | Iterative Interval Bisection over Pre-Sorted Array, Overflow-Safe Midpoint ($low + \frac{high - low}{2}$), Step Counting. | **Search: $O(\log N)$**<br>Best: $O(1)$<br>Comparisons: $\le \lfloor\log_2 N\rfloor + 1$ | **Fast Indexed Product Search** on sorted arrays. |
| **Merge Sort** | `MergeSort` | Stable Divide-and-Conquer, Recursive Bisection, Linear $O(N)$ Merge with Auxiliary Buffer. | **Time: $O(N \log N)$**<br>Space: $O(N)$ | **Stable Catalog Sorting** by price, stock quantity, and product name. |
| **Quick Sort** | `QuickSort` | In-Place Partitioning, **Median-of-Three Pivot Selection** (guards against $O(N^2)$ degradation on sorted inputs). | **Average: $O(N \log N)$**<br>Worst: $O(N^2)$<br>Space: $O(\log N)$ | **High-Throughput In-Place Sorting** for large product catalogs. |

---

## 📦 Key Website Modules

### 1. 🔐 Admin Authentication
- Dedicated login portal with credential validation.
- Passwords secured with **BCrypt salted cryptographic hashing** ($Cost = 10$).
- Session protection via Spring Web `HandlerInterceptor` (`AuthInterceptor`).
- Pre-seeded superadmin: `username: admin`, `password: admin123`.

### 2. 📊 Executive Dashboard
- **Real-Time KPI Cards**: Total Products, Total Physical Stock Units, Total Inventory Valuation, Total Sales Volume, and Aggregate Gross Revenue.
- **Low Stock Notification Alert Banner**: Instantly flags products at or below their minimum reorder point.
- **Interactive Chart.js Visualizations**:
  - *Monthly Sales Trend*: Trailing 6-month gross revenue curve.
  - *Inventory by Category*: Doughnut distribution of stock units across categories.
- **Recent Activity Ledger**: Snapshot of the latest stock movements and POS checkouts.
- **Top 5 Best Sellers Preview**: Dynamically calculated via the custom Max Heap.

### 3. 🏷️ Product Catalog Management
- **Full CRUD Operations**: Create, Read, Update, and Delete products with strict input validation.
- **Attributes**: Unique Product ID (SKU), Name, Category, Description, Purchase Price, Selling Price, Available Quantity, Minimum Reorder Level, Supplier, and Audit Timestamps.
- **Custom Algorithmic Sorting**: Sort the catalog dynamically using your choice of custom **Merge Sort** or **Quick Sort** by Price, Quantity, or Name.
- **Low Stock Subsystem**: Dedicated filter view identifying critical items needing replenishment.

### 4. 🔍 Multi-Algorithm Advanced Search
Allows the user to search for any product and choose between **four distinct search algorithms**:
1. **Hash Table Search** ($O(1)$ instantaneous key lookup)
2. **AVL Tree Search** ($O(\log N)$ balanced BST lookup)
3. **Binary Search** ($O(\log N)$ iterative array bisection)
4. **Linear Search** ($O(N)$ sequential scan as the baseline)

*Displays the found product card, the algorithm utilized, the exact number of key comparisons executed, and the nanosecond elapsed execution time.*

### 5. 🔄 Stock Management (In / Out)
- **Stock In**: Record incoming vendor shipments and purchase orders with quantity, supplier invoice reference, and automated stock increment.
- **Stock Out**: Record outgoing shipments, warehouse transfers, or damaged inventory.
- **Integrity Enforcement**: Strict prevention of negative inventory levels (`InsufficientStockException` thrown if requested decrement exceeds available stock).
- **Audit Logging**: Every movement generates an immutable row in `stock_transactions`.

### 6. 🛒 Point of Sale (POS) & Billing
- **Dynamic Multi-Item Cart**: Add multiple distinct products, adjust item quantities, and observe live subtotal and grand total recalculations.
- **Atomic Checkout**: Uses `@Transactional` boundary—deducts inventory, saves the sale invoice, records itemized lines, and updates the in-memory Max Heap in one atomic database operation.
- **Printable Tax Invoice**: Generates a commercial-grade, printable receipt with customer details, item breakdowns, tax summaries, and date stamps.

### 7. 🏆 Best-Selling Products Ranking
- Ranks inventory by total units sold using a **custom Max Heap**.
- Choose between **Top 5** and **Top 10** views.
- Demonstrates Floyd's $O(N)$ linear build-heap algorithm and $O(K \log N)$ priority queue extraction.

### 8. 📜 Complete Inventory & Sales History
- Consolidated audit trail of all physical stock movements (`STOCK_IN` and `STOCK_OUT`).
- Filter transactions by type, product ID, or date range.

### 9. ⚡ Live DSA Performance Analysis
- **Empirical Benchmarking Suite**: Tests all 4 search algorithms and both sorting algorithms against dynamic datasets of **100, 1,000, 5,000, and 10,000 products**.
- **JIT Warm-up Engine**: Runs 500 untimed burn-in iterations prior to sampling to ensure JVM HotSpot C1/C2 native compilation, eliminating first-run timing distortions.
- **Metrics Collected**: Real execution time measured via `System.nanoTime()` averaged over 10 repeated trials, total comparison operations, and interactive Chart.js logarithmic comparison charts.

---

## 🛠️ Technology Stack

| Layer | Technologies Used |
| :--- | :--- |
| **Backend Framework** | Java 17+, Spring Boot 3.3.5, Spring MVC, Spring Data JPA |
| **Frontend & UI** | Thymeleaf, Bootstrap 5.3, Bootstrap Icons, Chart.js 4.4, Custom CSS |
| **Data Structures** | 100% Custom Core Java (`HashTable`, `AVLTree`, `MaxHeap`, `BinarySearch`, `MergeSort`, `QuickSort`) |
| **Persistence** | MySQL 8.0 & Embedded Persistent Storage (Dual Profile via `application.properties`) |
| **Security** | BCrypt Salted Password Hashing ($Cost = 10$), Session-based `AuthInterceptor` |
| **Build Tool** | Apache Maven 3.9.9 (Bundled `mvnw.cmd` wrapper) |
| **Testing** | JUnit 5, Mockito, Spring Boot Test (30/30 Passing) |

---

## ⚡ Quick Start Instructions

### Option 1: Run Pre-Built Executable Fat JAR (Recommended)
```powershell
# Run immediately on default port 8080
java -jar target/stockmaster-inventory-system-1.0.0.jar

# Or run on custom port (e.g., 8085)
java -jar target/stockmaster-inventory-system-1.0.0.jar --server.port=8085
```

### Option 2: Run via Bundled Maven Wrapper
```powershell
# Build and run directly
.\mvnw.cmd spring-boot:run

# Or run tests
.\mvnw.cmd test
```

### Accessing the Application
- **URL**: `http://localhost:8080` (or `http://localhost:8085`)
- **Default Username**: `admin`
- **Default Password**: `admin123`

---

## 📂 Repository Directory Structure

```text
webbuild/
├── mvnw.cmd                              # Maven Wrapper script
├── pom.xml                               # Project Object Model dependencies
├── stockmaster_mysql.sql                 # Complete MySQL schema DDL & sample data
├── README.md                             # Master project overview & guide
├── SETUP_GUIDE.md                        # Step-by-step setup & deployment guide
├── DATABASE_SCHEMA.md                    # Database ERD, table specs & constraints
├── DSA_EXPLANATION.md                    # Deep academic dive into custom DSA algorithms
├── PROJECT_ARCHITECTURE.md               # Clean architecture & request lifecycle flow
├── VIVA_QUESTIONS.md                     # 25 in-depth viva questions & answers (Hinglish + English)
├── TEAM_CODE_EXPLANATION.md              # Code walkthrough for student project teams
├── src/
│   ├── main/
│   │   ├── java/com/stockmaster/
│   │   │   ├── StockMasterApplication.java
│   │   │   ├── config/                  # Interceptors, WebMvc, DataInitializer
│   │   │   ├── controller/              # Auth, Dashboard, Product, Stock, Sale, Search, DSA
│   │   │   ├── dsa/                     # HashTable, AVLTree, MaxHeap, BinarySearch, MergeSort, QuickSort
│   │   │   ├── dto/                     # Transfer objects (ProductDto, SaleRequest, Search, etc.)
│   │   │   ├── entity/                  # JPA entities (Product, Admin, StockTx, Sale, SaleItem)
│   │   │   ├── exception/               # GlobalExceptionHandler, Custom exceptions
│   │   │   ├── repository/              # Spring Data JPA repositories
│   │   │   └── service/                 # ProductService, StockService, SaleService, DsaIndexService
│   │   └── resources/
│   │       ├── application.properties   # Default persistent storage profile
│   │       ├── application-mysql.properties # Production MySQL 8.0 server profile
│   │       ├── static/                  # CSS styles & client-side JavaScript
│   │       └── templates/               # Thymeleaf HTML views & fragments
│   └── test/java/com/stockmaster/       # 30 Unit Tests covering DSA, Services, & Entities
└── target/
    └── stockmaster-inventory-system-1.0.0.jar  # 52.6 MB Standalone Executable Fat JAR
```

---

## 📚 Technical Documentation Directory

For in-depth explanations, refer to the accompanying project documentation:
- 📖 [SETUP_GUIDE.md](file:///c:/Users/gorav%20singh/Documents/webbuild/SETUP_GUIDE.md) — Comprehensive installation, port customization, and troubleshooting manual.
- 📐 [PROJECT_ARCHITECTURE.md](file:///c:/Users/gorav%20singh/Documents/webbuild/PROJECT_ARCHITECTURE.md) — Architectural diagrams, layered abstractions, and request lifecycle flows.
- 🧮 [DSA_EXPLANATION.md](file:///c:/Users/gorav%20singh/Documents/webbuild/DSA_EXPLANATION.md) — Mathematical proofs, rotation diagrams, recurrence relations, and Big-O tables.
- 🗄️ [DATABASE_SCHEMA.md](file:///c:/Users/gorav%20singh/Documents/webbuild/DATABASE_SCHEMA.md) — Entity-relationship diagram, table definitions, foreign keys, and indexes.
- 🎓 [VIVA_QUESTIONS.md](file:///c:/Users/gorav%20singh/Documents/webbuild/VIVA_QUESTIONS.md) — 25 technical viva interview questions with answers in simple Hinglish and English.
- 👥 [TEAM_CODE_EXPLANATION.md](file:///c:/Users/gorav%20singh/Documents/webbuild/TEAM_CODE_EXPLANATION.md) — Line-by-line guide for team members presenting to faculty.
