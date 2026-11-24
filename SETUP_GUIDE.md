# StockMaster — Complete Setup & Deployment Guide

This guide provides step-by-step instructions for running, testing, and deploying **StockMaster: Advanced Inventory Management System Using Core Java Data Structures**.

---

## 1. System Requirements & Prerequisites

| Component | Minimum Required | Recommended | Current Environment |
| :--- | :--- | :--- | :--- |
| **Operating System** | Windows 10 / 11, macOS, Linux | Any modern 64-bit OS | Windows 11 |
| **Java Development Kit (JDK)** | OpenJDK / Oracle JDK 17+ | JDK 17 or JDK 21 LTS | JDK 25.0.2 |
| **Build Tool** | Apache Maven 3.8+ | Maven 3.9+ | Bundled Maven 3.9.9 (`mvnw.cmd`) |
| **Memory (RAM)** | 2 GB free | 4 GB free | 8 GB+ |
| **Browser** | Modern web browser | Chrome, Edge, Firefox, Safari | Chrome / Edge |

Verify your Java installation:
```powershell
java -version
```
Expected output:
```text
java version "25.0.2" ... or 17+ / 21+
```

---

## 2. Quick Start: Running in 30 Seconds

### Option A: Run Pre-Compiled Executable JAR (Fastest)

A production-ready fat JAR is already compiled and packaged in the `target/` directory:

```powershell
java -jar target/stockmaster-inventory-system-1.0.0.jar
```

To run on a custom port (e.g., if 8080 is occupied):
```powershell
java -jar target/stockmaster-inventory-system-1.0.0.jar --server.port=8085
```

---

### Option B: Run via Maven Wrapper

If you want to build and run from source code:

```powershell
# Build and run directly
.\mvnw.cmd spring-boot:run

# Or compile and package clean fat jar
.\mvnw.cmd clean package -DskipTests=false
```

---

## 3. Database Configurations

StockMaster provides **dual-database flexibility**. You can run immediately with zero external dependencies, or connect to a dedicated enterprise MySQL server.

### Mode 1: Persistent File Mode (Default — Zero Setup Required)
- **Engine**: Embedded storage running in strict MySQL compatibility mode (`MODE=MySQL`).
- **Persistence Location**: `./data/stockmaster_db.mv.db`
- **Behavior**: All product additions, stock changes, sales transactions, and user logins are **permanently saved to disk**. When the application stops and restarts, every record is automatically restored and re-indexed into custom memory structures.
- **Activation**: Active by default. No flags required.

---

### Mode 2: Dedicated MySQL 8.0 Server Mode
If your professor or viva examiner requests connecting to a live MySQL Server instance:

#### Step 1: Create Database & User in MySQL CLI / Workbench
```sql
CREATE DATABASE IF NOT EXISTS stockmaster_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### Step 2: (Optional) Import Schema & Seed Data
You can import the provided DDL script:
```powershell
mysql -u root -p stockmaster_db < stockmaster_mysql.sql
```
*(Alternatively, Hibernate's `ddl-auto=update` and `DataInitializer` will automatically create and seed all tables upon launch).*

#### Step 3: Configure Credentials
Check `src/main/resources/application-mysql.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/stockmaster_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=your_mysql_password
```

#### Step 4: Launch Application with MySQL Profile
```powershell
java -jar -Dspring.profiles.active=mysql target/stockmaster-inventory-system-1.0.0.jar
```
Or via Maven:
```powershell
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=mysql
```

---

## 4. Default Administrator Credentials

When the system initializes for the first time, `DataInitializer` automatically provisions the default administrator account:

| Field | Default Value | Notes |
| :--- | :--- | :--- |
| **Login URL** | `http://localhost:8080/login` | Redirected automatically from `/` |
| **Username** | `admin` | Case-insensitive |
| **Password** | `admin123` | Encrypted in DB with BCrypt ($Cost = 10$) |
| **Role** | Super Administrator | Full access to all modules and DSA analysis |

---

## 5. Running Automated Unit Tests

StockMaster includes 30 comprehensive JUnit 5 unit tests verifying all custom DSA implementations, service layers, and transactional guarantees.

Execute the test suite:
```powershell
.\mvnw.cmd test
```

### Verified Test Classes:
1. `HashTableTest`: Verifies insert, get, collision handling, dynamic resizing ($2\times$ capacity expansion at load factor 0.75), update, delete, and duplicate key integrity.
2. `AVLTreeTest`: Verifies height calculation, balance factors, all 4 rotation variants (LL, RR, LR, RL), deletion rebalancing, and sorted in-order traversal.
3. `MaxHeapTest`: Verifies heap invariant, parent-child indexing, `insert`, `extractMax`, `peekMax`, Floyd's $O(N)$ linear `buildHeap`, and Top-K extraction.
4. `BinarySearchTest`: Verifies exact key matching, step counting, bounds checking, missing element handling, and edge cases (first/last index).
5. `SortingTest`: Verifies `MergeSort` and `QuickSort` (with median-of-three pivot selection) across random, ascending, descending, and duplicate datasets.
6. `ProductServiceTest`: Verifies Product CRUD, validation, and multi-algorithm searching.
7. `StockServiceTest`: Verifies atomic Stock In, Stock Out, negative stock prevention, and transaction log creation.
8. `SaleServiceTest`: Verifies atomic POS sales checkout, line item calculations, inventory deduction, and Top-K heap ranking refresh.

---

## 6. Application URL Directory & Feature Verification

Once the application is running, access the following routes in your web browser:

| Feature / Module | URL Route | Description |
| :--- | :--- | :--- |
| **Authentication** | `http://localhost:8080/login` | Secure administrator login form with error feedback |
| **Dashboard** | `http://localhost:8080/dashboard` | KPI summary cards, low-stock warnings, Chart.js trends, recent movements |
| **Product Catalog** | `http://localhost:8080/products` | View, search, filter by category, sort via custom Merge/Quick Sort |
| **Add Product** | `http://localhost:8080/products/add` | Add product with validation, SKU checks, and real-time category assignment |
| **Low Stock Alerts** | `http://localhost:8080/products/low-stock` | Filtered view of items at or below reorder threshold |
| **Advanced Search** | `http://localhost:8080/search` | Compare Hash Table, AVL Tree, Binary Search & Linear Search live |
| **Stock Operations** | `http://localhost:8080/stock` | Perform Stock In (procurement) and Stock Out (dispatch/damage) with strict validations |
| **Point of Sale (POS)** | `http://localhost:8080/sales/create` | Dynamic multi-item checkout, instant line calculations, atomic stock update |
| **Sales Records** | `http://localhost:8080/sales` | Complete ledger of sales invoices and customer details |
| **Printable Invoice** | `http://localhost:8080/sales/invoice/{id}` | Official commercial tax invoice receipt ready for printing / PDF export |
| **Best Sellers** | `http://localhost:8080/sales/best-sellers` | Max Heap ranking of Top 5 and Top 10 best-selling inventory items |
| **Audit History** | `http://localhost:8080/history` | Filterable chronological ledger of all physical stock transactions |
| **DSA Performance** | `http://localhost:8080/dsa/analysis` | Live empirical benchmark across 100 to 10,000 items with interactive Chart.js graphs |

---

## 7. Troubleshooting & FAQ

### Q1: Port 8080 is already in use (`Address already in use: bind`)
**Solution**: Run the application on an alternative port by passing `--server.port`:
```powershell
java -jar target/stockmaster-inventory-system-1.0.0.jar --server.port=8085
```
Then open `http://localhost:8085/`.

---

### Q2: How do I reset the sample data back to original state?
**Solution**: Stop the server, delete the `./data/` directory, and start the application again:
```powershell
Remove-Item -Recurse -Force ./data
java -jar target/stockmaster-inventory-system-1.0.0.jar
```
`DataInitializer` will automatically recreate the database file and seed 35 realistic products, initial stock records, and sample sales.

---

### Q3: How do I prove in the viva that custom DSA is used instead of Java Collections?
**Solution**:
1. Open the source code directory: `src/main/java/com/stockmaster/dsa/`.
2. Show the viva examiner `HashTable.java`, `AVLTree.java`, `MaxHeap.java`, `BinarySearch.java`, `MergeSort.java`, and `QuickSort.java`.
3. Open `DsaIndexService.java` to demonstrate how `HashTable` and `AVLTree` are maintained directly in memory and queried during live searches.
4. Navigate to the **DSA Performance Analysis** tab (`/dsa/analysis`) on the website to demonstrate the real-time nanosecond benchmarks.
