# StockMaster — Comprehensive University Technical Viva Guide (25 Questions & Answers)
### Academic Project: Inventory Management System Using Advanced Data Structures (University A6)

---

## 📌 Section 1: Core Data Structures & Algorithms (DSA)

### Q1. Is project mein kaun-kaun se Data Structures use kiye gaye hain aur unka inventory mein kya purpose hai?
**Answer (Hinglish):**
Is project mein humne 5 Core Custom Data Structures scratch se Java mein implement kiye hain:
1. **Custom Hash Table (Separate Chaining):** Product ID / Code (e.g. `PRD-1001`) se instant $O(1)$ average time mein product search aur lookup karne ke liye.
2. **Custom AVL Tree (Self-Balancing BST):** Products ko sorted order mein rakhne ke liye aur guaranteed $O(\log N)$ search time provide karne ke liye.
3. **Custom Max Heap (Priority Queue):** Best-Selling Products ko rank karne ke liye (Top 5 / Top 10 by Units Sold) in $O(K \log N)$ time bina pure database ko sort kiye.
4. **Custom Binary Search (Iterative):** Pre-sorted array/list of products par divide-and-conquer search karne ke liye in $O(\log N)$ time.
5. **Custom Sorting (Merge Sort & Quick Sort):** Products ko price, quantity aur name ke basis par sort karne ke liye with $O(N \log N)$ complexity.

---

### Q2. Hash Table mein Collision kya hota hai aur aapne ise kaise resolve kiya?
**Answer (Hinglish):**
- **Collision:** Jab do alag-alag keys (jaise do alag Product IDs) hash function calculate karne ke baad same array bucket index generate karte hain, to use *Collision* kehte hain (`hash(k1) % capacity == hash(k2) % capacity`).
- **Resolution (Separate Chaining):** Humne **Separate Chaining** technique use ki hai. Har array bucket ek Singly Linked List (`HashNode<K, V>`) ka head pointer hota hai. Jab collision hota hai, to naya element us bucket ki linked list ke start (head) par link ho jata hai.
- **Rehashing:** Jab load factor $\frac{\text{size}}{\text{capacity}} \ge 0.75$ ho jata hai, hum table capacity ko double ($2 \times \text{capacity}$) karte hain aur sabhi existing nodes ko rehash karte hain taaki lookup hamesha $O(1)$ rahe.

---

### Q3. AVL Tree standard Binary Search Tree (BST) se behtar kyun hai?
**Answer (Hinglish):**
- Standard BST mein agar elements sorted order mein insert kiye jayein (e.g., 1, 2, 3, 4, 5), to tree ek skewed line (linked list jaisa) ban jata hai, jisse search time $O(N)$ ho jata hai.
- **AVL Tree (Adelson-Velsky & Landis):** Ye ek *Self-Balancing BST* hai. Har node par Balance Factor calculate hota hai:
  $$\text{Balance Factor} = \text{Height(Left Subtree)} - \text{Height(Right Subtree)}$$
  Balance factor hamesha $\{-1, 0, +1\}$ ke beech hona chahiye.
- Jaise hi koi insertion ya deletion is factor ko $\pm 2$ kar deta hai, tree automatically **Rotations** perform karke tree ki height ko strictly $\le 1.44 \log_2(N)$ par balance karta hai. Is wajah se search, insert, aur delete worst-case mein bhi guaranteed $O(\log N)$ hote hain.

---

### Q4. AVL Tree mein kitne types ke Rotations hote hain?
**Answer (Hinglish):**
AVL Tree mein balance restore karne ke liye 4 standard rotations hote hain:
1. **Left-Left (LL) Heavy:** Subtree left-heavy hota hai. Fix: **Single Right Rotation**.
2. **Right-Right (RR) Heavy:** Subtree right-heavy hota hai. Fix: **Single Left Rotation**.
3. **Left-Right (LR) Heavy:** Left child ka right subtree heavy hota hai. Fix: **Double Rotation** (Left child par Left Rotation, phir parent par Right Rotation).
4. **Right-Left (RL) Heavy:** Right child ka left subtree heavy hota hai. Fix: **Double Rotation** (Right child par Right Rotation, phir parent par Left Rotation).

---

### Q5. AVL Tree ka Inorder Traversal inventory ke liye kya fayda deta hai?
**Answer (Hinglish):**
BST ya AVL tree ka **Inorder Traversal** (`Left Subtree -> Root -> Right Subtree`) hamesha keys ko strictly **Ascending Sorted Order** mein visit karta hai.
StockMaster mein jab user sorted product codes chahta hai, to hume extra sorting algorithm run karne ki zaroorat nahi padti; AVL Tree ka inorder traversal $O(N)$ linear time mein already sorted collection provide kar deta hai.

---

### Q6. Max Heap kya hai aur best-selling products ke liye pure sorting ke bajaye Max Heap kyun use kiya gaya?
**Answer (Hinglish):**
- **Max Heap Property:** Ek complete binary tree jismein har parent node ki value apne child nodes se greater ya equal hoti hai:
  $$A[\text{parent}(i)] \ge A[\text{child}(i)]$$
- Array implementation: Index $i$ ke liye, Left Child $= 2i+1$, Right Child $= 2i+2$, Parent $= \lfloor(i-1)/2\rfloor$.
- **Kyun Use Kiya:**
  Agar catalog mein 10,000 products hain aur hume sirf **Top 5** best sellers chahiye:
  - Full Sort (Merge/Quick sort): $O(N \log N)$ time leta hai ($10,000 \times \log_2 10,000 \approx 133,000$ operations).
  - Max Heap: Floyd's algorithm se heap $O(N)$ mein build hota hai, aur Top-$K$ extract karne mein sirf $O(K \log N)$ lagta hai ($5 \times 14 = 70$ operations). Ye massively fast aur CPU-efficient hai.

---

### Q7. Binary Search aur Linear Search ke comparison ka kya result aaya?
**Answer (Hinglish):**
- **Linear Search:** Har element ko sequentially index 0 se check karta hai. Time Complexity: Best $O(1)$, Worst $O(N)$.
- **Binary Search:** Sorted array par divide-and-conquer karta hai, har comparison par search space half ho jata hai ($N \to N/2 \to N/4 \dots$). Time Complexity: $O(\log N)$.
- **Empirical Observation:**
  10,000 products ke dataset par Linear Search ko worst-case mein 10,000 comparisons lagte hain (approx 20,000 - 50,000 ns), jabki Binary Search maximum sirf $\lceil\log_2 10,000\rceil = 14$ comparisons mein match dhoondh leta hai (approx 200 - 800 ns).

---

### Q8. Merge Sort aur Quick Sort mein se kaunsa algorithm kab behtar perform karta hai?
**Answer (Hinglish):**
- **Merge Sort:**
  - Guaranteed $O(N \log N)$ in all cases (Best, Average, Worst).
  - **Stable Sort** hai (duplicate keys ka relative order preserve rehta hai).
  - Par isme $O(N)$ auxiliary array memory allocate karni padti hai.
- **Quick Sort:**
  - In-place algorithm hai ($O(\log N)$ stack space).
  - Spatial CPU cache locality bohot high hoti hai, isliye practical benchmarks mein aksar Merge Sort se 20-30% fast nikalta hai.
  - Worst case $O(N^2)$ ho sakta hai agar pivot kharab ho; isliye humne **Median-of-Three** pivot selection implement kiya hai jo worst-case ko actively prevent karta hai.

---

### Q9. Aapne Java ke built-in `java.util.HashMap` ya `PriorityQueue` ko kyun nahi use kiya?
**Answer (Hinglish):**
University A6 Project ka primary mandatory requirement tha custom Core Java implementation demonstrate karna. Agar hum built-in library classes use karte, to evaluator humare internal DSA concepts (rotations, pointers, collision chains, heapify) ko evaluate nahi kar pate. Humne `HashTable.java`, `AVLTree.java`, `MaxHeap.java`, `BinarySearch.java`, `MergeSort.java`, aur `QuickSort.java` sab khud Core Java mein implement kiye hain.

---

## 📌 Section 2: Architecture & Database Integration

### Q10. Database aur in-memory DSA ke beech synchronization kaise ensure hoti hai?
**Answer (Hinglish):**
- Persistent storage ke liye **MySQL / Spring Data JPA** use hota hai taaki application restart hone par data delete na ho.
- Application start hone par `DataInitializer` database se sabhi products load karke in-memory `HashTable` aur `AVLTree` ko warm-up populate karta hai.
- Jab bhi koi naya product add hota hai (`createProduct`), update hota hai, ya delete hota hai, service layer pehle database update karti hai aur turant baad `DsaIndexService` ke in-memory structures ko synchronized update karti hai.
- Is architecture ko **Write-Through In-Memory Indexing** kehte hain.

---

### Q11. Stock Out ya Sales checkout ke waqt Negative Stock ko kaise prevent kiya gaya hai?
**Answer (Hinglish):**
- Humne `StockService` aur `SaleService` dono mein strict validation logic lagaya hai.
- Agar `availableQuantity < requestedQuantity` hota hai, to code execution proceed karne ke bajaye turant custom `InsufficientStockException` throw kar deta hai.
- Method par `@Transactional` annotation lagi hai, jisse database rollback ho jata hai aur stock kabhi bhi negative nahi ho sakta (Overselling Prevention).

---

### Q12. Point of Sale (POS) aur Billing module kaise kaam karta hai?
**Answer (Hinglish):**
1. Admin POS page (`/sales/new`) par customer details bharta hai aur product dropdown se items select karta hai.
2. JavaScript dynamically available stock check karta hai aur quantity enter hone par unit price $\times$ quantity calculate karke gross total aur discount deduct karke net bill show karta hai.
3. Form submit hone par backend atomic transaction mein:
   - Check karta hai ki koi item oversell na ho.
   - Har product ka available stock deduct karta hai.
   - Unique invoice number generate karta hai (`INV-yyyyMMdd-XXXX`).
   - `sales`, `sale_items` aur `stock_transactions` table mein audit record save karta hai.
   - MaxHeap sales priority queue ko refresh karta hai.

---

### Q13. System mein low-stock alerts kaise detect hote hain?
**Answer (Hinglish):**
Har product entity mein ek field hai: `minStockLevel`.
Jab `availableQuantity <= minStockLevel` hota hai:
- Product `lowStock` flag `true` ho jata hai.
- Dashboard par prominent warning alert banner trigger hota hai.
- Dedicated `/products/low-stock` page par item highlight ho jata hai jahan se admin directly one-click "Stock In" kar sakta hai.

---

### Q14. Benchmark page par System.nanoTime() kyun use kiya gaya aur JIT warm-up kyun zaroori tha?
**Answer (Hinglish):**
- `System.currentTimeMillis()` sirf millisecond precision deta hai jo modern CPU par fast algorithms ($O(1)$ lookup) ke liye 0 ms dikha deta. `System.nanoTime()` nanosecond ($10^{-9}$ sec) precision deta hai.
- **JIT Warm-up:** Java Virtual Machine initial runs mein byte-code interpret karta hai. Thodi der baad C1/C2 JIT compiler code ko native machine code mein compile karta hai. Agar hum pehli run ko measure karein, to compilation time include ho jayega. Isliye hum pehle 15 warm-up iterations chalate hain aur phir 40 trials ka average calculate karte hain.

---

## 📌 Section 3: Technical Viva Rapid-Fire (Q15 – Q25)

| # | Question | Short Concept Answer |
|---|---|---|
| **15** | Separate Chaining mein load factor kya hota hai? | $\alpha = \frac{N}{M}$ (Items / Buckets). Humara threshold 0.75 hai, jiske baad capacity double ho jati hai. |
| **16** | AVL Tree ka balance factor formula kya hai? | $\text{BF}(node) = \text{height}(node.left) - \text{height}(node.right)$, range $\in \{-1, 0, 1\}$. |
| **17** | Heapify operation ka time complexity kya hota hai? | Single element bubble-up ya sink-down: $O(\log N)$. Pure array se heap build karna (Floyd's algo): $O(N)$. |
| **18** | Quick Sort mein worst-case kab hota hai aur kaise bache? | Jab already sorted array par first/last element pivot le lein ($O(N^2)$). Humne *Median-of-Three* pivot use karke ise fix kiya. |
| **19** | Merge Sort stable hai ya unstable? | **Stable**. Agar do items ka price same hai, to unka original order preserve rehta hai (`<=` in merge). |
| **20** | BCrypt password hashing kaise work karta hai? | Salted hashing algorithm hai with configurable work factor (default 10 rounds), jo rainbow table attacks prevent karta hai. |
| **21** | Spring Boot mein `@Transactional` ka kya role hai? | ACID properties maintain karta hai. Agar multi-item sale ke beech mein koi failure aaye, to pura transaction rollback ho jata hai. |
| **22** | Database tables ke beech foreign key relationships kya hain? | `products` $\to$ `stock_transactions`, `sales` $\to$ `sale_items`, `products` $\to$ `sale_items`. |
| **23** | Binary Search array size $N=10,000$ mein kitne maximum comparisons lega? | $\lceil\log_2 10,000\rceil = 14$ comparisons maximum. |
| **24** | Session Management kaise implement kiya gaya hai? | Custom `AuthInterceptor` protected routes (`/dashboard`, `/products`, etc.) par `session.getAttribute("LOGGED_IN_ADMIN")` verify karta hai. |
| **25** | Restart ke baad data delete kyun nahi hota? | Database files persistent storage (`./data/stockmaster_db` ya MySQL server) mein persist hoti hain aur JPA Hibernate DDL update mode mein re-attach hota hai. |
