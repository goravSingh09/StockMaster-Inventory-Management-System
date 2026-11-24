package com.stockmaster.controller;

import com.stockmaster.entity.StockTransaction;
import com.stockmaster.entity.TransactionType;
import com.stockmaster.service.ProductService;
import com.stockmaster.service.StockService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/history")
public class HistoryController {

    private final StockService stockService;
    private final ProductService productService;

    public HistoryController(StockService stockService, ProductService productService) {
        this.stockService = stockService;
        this.productService = productService;
    }

    @GetMapping
    public String viewHistory(@RequestParam(value = "type", required = false) String typeStr,
                              @RequestParam(value = "productId", required = false) Long productId,
                              @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                              @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                              Model model) {
        TransactionType type = null;
        if (typeStr != null && !typeStr.isBlank() && !"ALL".equalsIgnoreCase(typeStr)) {
            try {
                type = TransactionType.valueOf(typeStr.toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }

        LocalDateTime startDateTime = (startDate != null) ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = (endDate != null) ? endDate.atTime(LocalTime.MAX) : null;

        List<StockTransaction> transactions = stockService.filterTransactions(type, productId, startDateTime, endDateTime);

        model.addAttribute("transactions", transactions);
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("transactionTypes", TransactionType.values());
        model.addAttribute("selectedType", typeStr);
        model.addAttribute("selectedProductId", productId);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("activePage", "history");

        return "history/index";
    }
}
