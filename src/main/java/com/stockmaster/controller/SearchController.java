package com.stockmaster.controller;

import com.stockmaster.dto.SearchRequestDto;
import com.stockmaster.dto.SearchResponseDto;
import com.stockmaster.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/search")
public class SearchController {

    private final ProductService productService;

    public SearchController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String searchPage(Model model) {
        if (!model.containsAttribute("searchRequest")) {
            model.addAttribute("searchRequest", new SearchRequestDto("", SearchRequestDto.SearchAlgorithm.HASH_TABLE));
        }
        model.addAttribute("algorithms", SearchRequestDto.SearchAlgorithm.values());
        model.addAttribute("activePage", "search");
        return "search/index";
    }

    @PostMapping
    public String executeSearch(@ModelAttribute("searchRequest") SearchRequestDto request, Model model) {
        SearchResponseDto response = productService.searchProducts(request);

        model.addAttribute("searchRequest", request);
        model.addAttribute("searchResponse", response);
        model.addAttribute("algorithms", SearchRequestDto.SearchAlgorithm.values());
        model.addAttribute("activePage", "search");

        return "search/index";
    }
}
