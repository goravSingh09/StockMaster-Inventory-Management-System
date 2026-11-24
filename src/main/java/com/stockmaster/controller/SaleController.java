package com.stockmaster.controller;

import com.stockmaster.dto.ProductSalesDto;
import com.stockmaster.dto.SaleRequestDto;
import com.stockmaster.entity.Product;
import com.stockmaster.entity.Sale;
import com.stockmaster.service.ProductService;
import com.stockmaster.service.SaleService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/sales")
public class SaleController {

    private final SaleService saleService;
    private final ProductService productService;

    public SaleController(SaleService saleService, ProductService productService) {
        this.saleService = saleService;
        this.productService = productService;
    }

    @GetMapping
    public String listSales(Model model) {
        List<Sale> sales = saleService.getAllSales();
        model.addAttribute("sales", sales);
        model.addAttribute("activePage", "sales");
        return "sales/list";
    }

    @GetMapping("/new")
    public String newSaleForm(Model model) {
        if (!model.containsAttribute("saleRequest")) {
            model.addAttribute("saleRequest", new SaleRequestDto());
        }
        // Only products with available quantity > 0 can be sold
        List<Product> availableProducts = productService.getAllProducts().stream()
                .filter(p -> p.getAvailableQuantity() > 0)
                .toList();

        model.addAttribute("availableProducts", availableProducts);
        model.addAttribute("activePage", "sales");
        return "sales/create";
    }

    @PostMapping
    public String createSale(@Valid @ModelAttribute("saleRequest") SaleRequestDto request,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            List<Product> availableProducts = productService.getAllProducts().stream()
                    .filter(p -> p.getAvailableQuantity() > 0)
                    .toList();
            model.addAttribute("availableProducts", availableProducts);
            model.addAttribute("activePage", "sales");
            return "sales/create";
        }

        Sale completedSale = saleService.processSale(request);
        redirectAttributes.addFlashAttribute("successMessage",
                "Sale completed successfully! Invoice #" + completedSale.getInvoiceNumber() +
                " generated for amount $" + completedSale.getNetAmount() +
                ". Inventory stocks updated.");

        return "redirect:/sales/" + completedSale.getId() + "/invoice";
    }

    @GetMapping("/{id}/invoice")
    public String viewInvoice(@PathVariable("id") Long id, Model model) {
        Sale sale = saleService.getSaleById(id);
        model.addAttribute("sale", sale);
        model.addAttribute("activePage", "sales");
        return "sales/invoice";
    }

    @GetMapping("/best-sellers")
    public String bestSellingProducts(@RequestParam(value = "limit", defaultValue = "10") int limit, Model model) {
        // Enforce valid university requirement (Top 5 or Top 10)
        int k = (limit == 5) ? 5 : 10;
        List<ProductSalesDto> bestSellers = saleService.getTopSellingProducts(k);

        model.addAttribute("bestSellers", bestSellers);
        model.addAttribute("limit", k);
        model.addAttribute("activePage", "best-sellers");
        return "sales/best_sellers";
    }
}
