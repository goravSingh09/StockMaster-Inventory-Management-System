package com.stockmaster;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("Spring Boot Context Load Test")
class StockMasterApplicationTests {

    @Test
    void contextLoads() {
        // Verifies complete dependency injection graph, JPA repositories, and security beans load without errors
    }
}
