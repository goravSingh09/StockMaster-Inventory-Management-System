package com.stockmaster.controller;

import com.stockmaster.dto.ProductDto;
import com.stockmaster.entity.Product;
import com.stockmaster.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String listProducts(@RequestParam(value = "category", required = false) String category,
                               @RequestParam(value = "sortBy", defaultValue = "code") String sortBy,
                               @RequestParam(value = "direction", defaultValue = "asc") String direction,
                               @RequestParam(value = "algorithm", defaultValue = "merge") String algorithm,
                               Model model) {
        List<Product> products = productService.getProductsSorted(sortBy, direction, algorithm);

        if (category != null && !category.isBlank()) {
            products = products.stream()
                    .filter(p -> p.getCategory().equalsIgnoreCase(category))
                    .toList();
        }

        model.addAttribute("products", products);
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedSortBy", sortBy);
        model.addAttribute("selectedDirection", direction);
        model.addAttribute("selectedAlgorithm", algorithm);
        model.addAttribute("activePage", "products");

        return "products/list";
    }

    @GetMapping("/new")
    public String newProductForm(Model model) {
        if (!model.containsAttribute("productDto")) {
            model.addAttribute("productDto", new ProductDto());
        }
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("activePage", "products");
        return "products/form";
    }

    @PostMapping
    public String createProduct(@Valid @ModelAttribute("productDto") ProductDto dto,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", productService.getAllCategories());
            model.addAttribute("activePage", "products");
            return "products/form";
        }

        Product created = productService.createProduct(dto);
        redirectAttributes.addFlashAttribute("successMessage",
                "Product '" + created.getName() + "' (" + created.getProductCode() + ") created and indexed successfully!");
        return "redirect:/products";
    }

    @GetMapping("/{id}/edit")
    public String editProductForm(@PathVariable("id") Long id, Model model) {
        Product product = productService.getProductById(id);

        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setProductCode(product.getProductCode());
        dto.setName(product.getName());
        dto.setCategory(product.getCategory());
        dto.setDescription(product.getDescription());
        dto.setPurchasePrice(product.getPurchasePrice());
        dto.setSellingPrice(product.getSellingPrice());
        dto.setAvailableQuantity(product.getAvailableQuantity());
        dto.setMinStockLevel(product.getMinStockLevel());
        dto.setSupplier(product.getSupplier());

        model.addAttribute("productDto", dto);
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("isEdit", true);
        model.addAttribute("activePage", "products");
        return "products/form";
    }

    @PostMapping("/{id}")
    public String updateProduct(@PathVariable("id") Long id,
                                @Valid @ModelAttribute("productDto") ProductDto dto,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", productService.getAllCategories());
            model.addAttribute("isEdit", true);
            model.addAttribute("activePage", "products");
            return "products/form";
        }

        Product updated = productService.updateProduct(id, dto);
        redirectAttributes.addFlashAttribute("successMessage",
                "Product '" + updated.getName() + "' updated and re-indexed in HashTable & AVL Tree!");
        return "redirect:/products";
    }

    @PostMapping("/{id}/delete")
    public String deleteProduct(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.getProductById(id);
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Product '" + product.getName() + "' (" + product.getProductCode() + ") removed from Database, HashTable, and AVLTree.");
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Cannot delete product with existing sales or inventory transaction history. Financial audit records must be preserved.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting product: " + ex.getMessage());
        }
        return "redirect:/products";
    }

    @GetMapping("/low-stock")
    public String lowStockProducts(Model model) {
        model.addAttribute("lowStockProducts", productService.getLowStockProducts());
        model.addAttribute("outOfStockProducts", productService.getOutOfStockProducts());
        model.addAttribute("activePage", "low-stock");
        return "products/low_stock";
    }
}
