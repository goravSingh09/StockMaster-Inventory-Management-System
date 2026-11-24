package com.stockmaster.controller;

import com.stockmaster.dto.StockOperationDto;
import com.stockmaster.entity.Admin;
import com.stockmaster.entity.StockTransaction;
import com.stockmaster.entity.TransactionType;
import com.stockmaster.service.AdminAuthService;
import com.stockmaster.service.ProductService;
import com.stockmaster.service.StockService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/stock")
public class StockController {

    private final StockService stockService;
    private final ProductService productService;
    private final AdminAuthService authService;

    public StockController(StockService stockService,
                           ProductService productService,
                           AdminAuthService authService) {
        this.stockService = stockService;
        this.productService = productService;
        this.authService = authService;
    }

    @GetMapping
    public String stockManagementPage(Model model) {
        if (!model.containsAttribute("stockDto")) {
            StockOperationDto dto = new StockOperationDto();
            dto.setTransactionType(TransactionType.STOCK_IN);
            model.addAttribute("stockDto", dto);
        }

        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("recentTransactions", stockService.getRecentTransactions());
        model.addAttribute("activePage", "stock");
        return "stock/index";
    }

    @PostMapping
    public String handleStockOperation(@Valid @ModelAttribute("stockDto") StockOperationDto dto,
                                       BindingResult bindingResult,
                                       HttpSession session,
                                       Model model,
                                       RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("products", productService.getAllProducts());
            model.addAttribute("recentTransactions", stockService.getRecentTransactions());
            model.addAttribute("activePage", "stock");
            return "stock/index";
        }

        Admin admin = authService.getLoggedInAdmin(session);
        String username = (admin != null) ? admin.getUsername() : "Admin";

        StockTransaction tx = stockService.processStockOperation(dto, username);

        redirectAttributes.addFlashAttribute("successMessage",
                "Stock updated successfully for '" + tx.getProduct().getName() +
                "'. Operation: " + tx.getTransactionType() +
                ", Quantity: " + tx.getQuantity() +
                ", New Stock: " + tx.getNewQuantity() + " units.");

        return "redirect:/stock";
    }
}
