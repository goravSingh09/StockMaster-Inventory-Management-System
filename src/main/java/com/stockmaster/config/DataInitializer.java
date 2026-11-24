package com.stockmaster.config;

import com.stockmaster.entity.*;
import com.stockmaster.repository.*;
import com.stockmaster.service.AdminAuthService;
import com.stockmaster.service.DsaIndexService;
import com.stockmaster.service.SaleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final AdminRepository adminRepository;
    private final ProductRepository productRepository;
    private final StockTransactionRepository transactionRepository;
    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final AdminAuthService authService;
    private final DsaIndexService dsaIndexService;
    private final SaleService saleService;

    public DataInitializer(AdminRepository adminRepository,
                           ProductRepository productRepository,
                           StockTransactionRepository transactionRepository,
                           SaleRepository saleRepository,
                           SaleItemRepository saleItemRepository,
                           AdminAuthService authService,
                           DsaIndexService dsaIndexService,
                           SaleService saleService) {
        this.adminRepository = adminRepository;
        this.productRepository = productRepository;
        this.transactionRepository = transactionRepository;
        this.saleRepository = saleRepository;
        this.saleItemRepository = saleItemRepository;
        this.authService = authService;
        this.dsaIndexService = dsaIndexService;
        this.saleService = saleService;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        seedAdminUser();
        seedSampleProductsAndTransactions();
        seedSampleSales();

        // Rebuild in-memory DSA structures
        List<Product> allProducts = productRepository.findAll();
        dsaIndexService.rebuildAll(allProducts, new ArrayList<>());
        saleService.refreshBestSellingHeap();

        log.info("StockMaster database & in-memory DSA indexes initialized successfully.");
    }

    private void seedAdminUser() {
        if (!adminRepository.existsByUsername("admin")) {
            Admin admin = new Admin(
                    "admin",
                    authService.hashPassword("admin123"),
                    "System Administrator",
                    "admin@stockmaster.com",
                    "ADMIN"
            );
            adminRepository.save(admin);
            log.info("Default Admin account seeded: admin / admin123");
        }
    }

    private void seedSampleProductsAndTransactions() {
        if (productRepository.count() > 0) {
            return;
        }

        List<Product> products = new ArrayList<>();

        // 1. Electronics
        products.add(new Product("ELC-1001", "MacBook Pro M3 16-inch", "Electronics", "Apple M3 Pro 18GB Unified Memory 512GB SSD", new BigDecimal("1850.00"), new BigDecimal("2199.00"), 12, 5, "Apple Inc."));
        products.add(new Product("ELC-1002", "Logitech MX Master 3S Mouse", "Electronics", "Quiet clicks 8K DPI laser sensor with MagSpeed scroll", new BigDecimal("65.00"), new BigDecimal("99.99"), 45, 10, "Logitech Global"));
        products.add(new Product("ELC-1003", "Keychron Q1 Pro Wireless Keyboard", "Electronics", "75% Custom Mechanical Keyboard RGB Hot-swappable", new BigDecimal("120.00"), new BigDecimal("199.00"), 20, 8, "Keychron Direct"));
        products.add(new Product("ELC-1004", "Dell UltraSharp 27 4K Monitor", "Electronics", "IPS Black technology 98% DCI-P3 USB-C Hub 90W PD", new BigDecimal("380.00"), new BigDecimal("549.99"), 15, 5, "Dell Technologies"));
        products.add(new Product("ELC-1005", "Anker 7-in-1 USB-C Hub", "Electronics", "4K HDMI 100W Power Delivery 3x USB 3.0 SD Card reader", new BigDecimal("22.00"), new BigDecimal("39.99"), 60, 15, "Anker Innovations"));
        products.add(new Product("ELC-1006", "Sony WH-1000XM5 Headphones", "Electronics", "Industry leading active noise cancellation 30hr battery", new BigDecimal("240.00"), new BigDecimal("349.99"), 4, 10, "Sony Electronics")); // Low stock!
        products.add(new Product("ELC-1007", "Elgato Facecam Pro 4K60", "Electronics", "True 4K60 Ultra HD studio camera with Sony STARVIS sensor", new BigDecimal("190.00"), new BigDecimal("299.99"), 0, 5, "Corsair Elgato")); // Out of stock!

        // 2. Stationery
        products.add(new Product("STA-2001", "Leuchtturm1917 A5 Hardcover Notebook", "Stationery", "Dotted paper 251 numbered pages ink-proof paper", new BigDecimal("12.50"), new BigDecimal("24.50"), 80, 20, "Leuchtturm1917 DE"));
        products.add(new Product("STA-2002", "Pilot G2 Gel Pens Black 0.7mm (Pack of 12)", "Stationery", "Smooth writing dynamic gel ink long-lasting refillable", new BigDecimal("8.00"), new BigDecimal("16.99"), 110, 30, "Pilot Corporation"));
        products.add(new Product("STA-2003", "Post-it Super Sticky Notes 3x3 Canary", "Stationery", "Strong adhesive 5 pads pack 450 total sheets", new BigDecimal("4.20"), new BigDecimal("8.49"), 95, 25, "3M Stationery"));
        products.add(new Product("STA-2004", "Executive Metal Mesh Desk Organizer", "Stationery", "6 compartments with sliding drawer for office desk", new BigDecimal("9.00"), new BigDecimal("18.99"), 35, 10, "OfficeZone Ltd"));
        products.add(new Product("STA-2005", "Kangaro Heavy Duty Metal Stapler HD-10D", "Stationery", "All metal construction capacity up to 20 sheets", new BigDecimal("3.50"), new BigDecimal("7.99"), 6, 15, "Kangaro Industries")); // Low stock!
        products.add(new Product("STA-2006", "Expanding Accordion File Document Folder", "Stationery", "13 pockets with colored tabs letter size closure buckle", new BigDecimal("5.00"), new BigDecimal("11.50"), 42, 12, "Solo Stationery"));
        products.add(new Product("STA-2007", "Rotring 600 Mechanical Pencil 0.5mm", "Stationery", "Full metal body knurled grip matte black drafting tool", new BigDecimal("19.00"), new BigDecimal("34.99"), 18, 5, "Newell Brands"));

        // 3. Grocery
        products.add(new Product("GRO-3001", "Blue Tokai Single-Origin Arabica 500g", "Grocery", "Whole bean medium roast Attikan estate coffee", new BigDecimal("7.50"), new BigDecimal("14.00"), 50, 15, "Blue Tokai Roasters"));
        products.add(new Product("GRO-3002", "Uji Ceremonial Japanese Matcha 100g", "Grocery", "Stone ground 1st harvest organic antioxidant tea", new BigDecimal("16.00"), new BigDecimal("29.99"), 25, 10, "Kyoto Tea Exports"));
        products.add(new Product("GRO-3003", "The Whole Truth Crunchy Peanut Butter 1kg", "Grocery", "100% roasted peanuts zero added sugar or oils", new BigDecimal("4.00"), new BigDecimal("8.50"), 40, 10, "Fitshit Foods"));
        products.add(new Product("GRO-3004", "Lindt Excellence Dark Chocolate 85% 100g", "Grocery", "Rich cocoa full-bodied aromatic dark chocolate bar", new BigDecimal("2.20"), new BigDecimal("4.49"), 85, 20, "Lindt & Sprungli"));
        products.add(new Product("GRO-3005", "Organic Raw Wildflower Forest Honey 500g", "Grocery", "Unpasteurized unfiltered pure mountain nectar", new BigDecimal("5.50"), new BigDecimal("11.99"), 3, 10, "Himalayan Organics")); // Low stock!
        products.add(new Product("GRO-3006", "Himalayan Pure Pink Rock Salt Grinder 380g", "Grocery", "Natural coarse mineral crystal with refillable mill", new BigDecimal("2.80"), new BigDecimal("5.99"), 70, 15, "Salt Naturals"));
        products.add(new Product("GRO-3007", "Bob's Red Mill Gluten Free Rolled Oats 907g", "Grocery", "Whole grain certified organic high fiber breakfast oats", new BigDecimal("4.80"), new BigDecimal("9.99"), 32, 10, "Bob's Red Mill"));

        // 4. Accessories
        products.add(new Product("ACC-4001", "Dual-Sided Eco PU Leather Desk Mat 90x45cm", "Accessories", "Waterproof smooth surface non-slip base midnight navy", new BigDecimal("8.50"), new BigDecimal("19.99"), 65, 15, "Nordic Desk Co"));
        products.add(new Product("ACC-4002", "Nulaxy Ergonomic Aluminum Laptop Stand", "Accessories", "Foldable height adjustable cooling stand up to 17 inch", new BigDecimal("13.00"), new BigDecimal("27.50"), 40, 10, "Nulaxy Tech"));
        products.add(new Product("ACC-4003", "Anker PowerLine+ III USB-C to USB-C 2M", "Accessories", "Heavy duty braided nylon 60W fast charging cable", new BigDecimal("6.00"), new BigDecimal("13.99"), 90, 25, "Anker Innovations"));
        products.add(new Product("ACC-4004", "Tomtoc Protective Laptop Sleeve 14-inch", "Accessories", "CornerArmor 360 military protection spill-resistant", new BigDecimal("14.00"), new BigDecimal("28.99"), 28, 8, "Tomtoc Bags"));
        products.add(new Product("ACC-4005", "Magnetic Desktop Cable Clips (Set of 6)", "Accessories", "Silicone cord organizers adhesive back multi-wire", new BigDecimal("3.00"), new BigDecimal("8.99"), 5, 12, "Baseus Official")); // Low stock!
        products.add(new Product("ACC-4006", "Spigen Rugged Armor Phone Case iPhone 15", "Accessories", "Carbon fiber air cushion technology shockproof case", new BigDecimal("7.00"), new BigDecimal("16.99"), 50, 15, "Spigen Inc"));
        products.add(new Product("ACC-4007", "Anti-Static Microfiber Lens Cleaning Cloth (5x)", "Accessories", "Ultra fine lint-free cloths for screens and optics", new BigDecimal("2.00"), new BigDecimal("6.50"), 80, 20, "CamKix Supplies"));

        // 5. Home Appliances
        products.add(new Product("HAP-5001", "Xiaomi Smart Tower Oscillating Desk Fan", "Home Appliances", "DC inverter brushless motor 100 level airflow ultra quiet", new BigDecimal("35.00"), new BigDecimal("64.99"), 22, 6, "Xiaomi Inc."));
        products.add(new Product("HAP-5002", "Dreo Space Heater Ceramic 1500W PTC", "Home Appliances", "Digital thermostat remote control 70 oscillation eco mode", new BigDecimal("28.00"), new BigDecimal("49.99"), 18, 5, "Dreo Home"));
        products.add(new Product("HAP-5003", "Levoit Core 300 True HEPA Air Purifier", "Home Appliances", "VortexAir technology cleans 99.97% allergens smoke odor", new BigDecimal("62.00"), new BigDecimal("99.99"), 14, 4, "Levoit USA"));
        products.add(new Product("HAP-5004", "Philips Electric Glass Kettle 1.7L Blue LED", "Home Appliances", "304 food-grade stainless steel base auto shut-off 2200W", new BigDecimal("18.00"), new BigDecimal("32.50"), 30, 8, "Philips Domestic"));
        products.add(new Product("HAP-5005", "BenQ WiT e-Reading LED Desk Lamp", "Home Appliances", "Curved optical lighting auto-dimming touch sensor clamp", new BigDecimal("115.00"), new BigDecimal("189.00"), 2, 5, "BenQ Corp")); // Low stock!
        products.add(new Product("HAP-5006", "Levoit Ultrasonic Cool Mist Humidifier 3L", "Home Appliances", "Top-fill 25hr runtime whisper quiet essential oil tray", new BigDecimal("21.00"), new BigDecimal("39.99"), 25, 8, "Levoit USA"));
        products.add(new Product("HAP-5007", "Etekcity High Precision Digital Kitchen Scale", "Home Appliances", "Stainless steel 5kg/11lb capacity 1g graduation tare function", new BigDecimal("6.50"), new BigDecimal("14.99"), 45, 12, "Etekcity Brands"));

        List<Product> savedProducts = productRepository.saveAll(products);

        // Record Initial Stock Transactions
        List<StockTransaction> txList = new ArrayList<>();
        for (Product p : savedProducts) {
            txList.add(new StockTransaction(
                    p,
                    TransactionType.INITIAL,
                    p.getAvailableQuantity(),
                    0,
                    p.getAvailableQuantity(),
                    "Initial warehouse batch intake",
                    "BATCH-2026-001",
                    "System"
            ));
        }
        transactionRepository.saveAll(txList);
        log.info("Seeded 35 sample products with initial stock transactions.");
    }

    private void seedSampleSales() {
        if (saleRepository.count() > 0) {
            return;
        }

        List<Product> all = productRepository.findAll();
        if (all.size() < 10) {
            return;
        }

        // Create 6 realistic historical sales with multiple items to populate Chart.js trends and MaxHeap
        createHistoricalSale("Rajesh Kumar", "+91 98765 43210", "CARD",
                List.of(new SaleItemSeed(all.get(1), 5), new SaleItemSeed(all.get(4), 8)),
                new BigDecimal("10.00"), LocalDateTime.now().minusDays(18));

        createHistoricalSale("Priya Sharma", "+91 91234 56789", "UPI",
                List.of(new SaleItemSeed(all.get(8), 12), new SaleItemSeed(all.get(7), 4)),
                new BigDecimal("5.00"), LocalDateTime.now().minusDays(14));

        createHistoricalSale("Amitabh Patel", "+91 99887 76655", "CASH",
                List.of(new SaleItemSeed(all.get(0), 1), new SaleItemSeed(all.get(1), 2), new SaleItemSeed(all.get(3), 2)),
                new BigDecimal("50.00"), LocalDateTime.now().minusDays(9));

        createHistoricalSale("Neha Verma", "+91 94567 12345", "UPI",
                List.of(new SaleItemSeed(all.get(14), 15), new SaleItemSeed(all.get(17), 6)),
                new BigDecimal("8.00"), LocalDateTime.now().minusDays(5));

        createHistoricalSale("Vikram Singh", "+91 97890 65432", "CARD",
                List.of(new SaleItemSeed(all.get(21), 10), new SaleItemSeed(all.get(23), 5)),
                new BigDecimal("15.00"), LocalDateTime.now().minusDays(2));

        createHistoricalSale("Ananya Roy", "+91 98111 22334", "CASH",
                List.of(new SaleItemSeed(all.get(1), 3), new SaleItemSeed(all.get(2), 2)),
                BigDecimal.ZERO, LocalDateTime.now().minusHours(4));

        log.info("Seeded realistic historical sales and customer receipts.");
    }

    private static class SaleItemSeed {
        final Product product;
        final int quantity;
        SaleItemSeed(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }
    }

    private void createHistoricalSale(String customerName, String phone, String method,
                                      List<SaleItemSeed> items, BigDecimal discount, LocalDateTime saleDate) {
        BigDecimal total = BigDecimal.ZERO;
        String invoiceNumber = "INV-" + saleDate.getYear() + "-" + String.format("%04d", (int) (Math.random() * 9000 + 1000));

        Sale sale = new Sale(invoiceNumber, customerName, phone, BigDecimal.ZERO, discount, BigDecimal.ZERO, method, "Walk-in purchase");
        sale.setSaleDate(saleDate);

        for (SaleItemSeed seed : items) {
            BigDecimal subtotal = seed.product.getSellingPrice().multiply(BigDecimal.valueOf(seed.quantity));
            total = total.add(subtotal);
            SaleItem item = new SaleItem(seed.product, seed.quantity, seed.product.getSellingPrice(), subtotal);
            sale.addItem(item);
        }

        sale.setTotalAmount(total);
        sale.setNetAmount(total.subtract(discount).max(BigDecimal.ZERO));
        saleRepository.save(sale);
    }
}
