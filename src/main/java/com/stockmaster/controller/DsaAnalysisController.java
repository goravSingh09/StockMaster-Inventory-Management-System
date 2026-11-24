package com.stockmaster.controller;

import com.stockmaster.dto.DsaBenchmarkResultDto;
import com.stockmaster.service.DsaBenchmarkService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class DsaAnalysisController {

    private final DsaBenchmarkService benchmarkService;

    public DsaAnalysisController(DsaBenchmarkService benchmarkService) {
        this.benchmarkService = benchmarkService;
    }

    @GetMapping("/dsa-analysis")
    public String dsaAnalysisPage(@RequestParam(value = "size", defaultValue = "1000") int size, Model model) {
        // Enforce supported university project sizes
        if (size != 100 && size != 1000 && size != 5000 && size != 10000) {
            size = 1000;
        }

        List<DsaBenchmarkResultDto> searchResults = benchmarkService.runSearchBenchmarks(size);
        List<DsaBenchmarkResultDto> sortResults = benchmarkService.runSortBenchmarks(size);

        model.addAttribute("datasetSize", size);
        model.addAttribute("searchResults", searchResults);
        model.addAttribute("sortResults", sortResults);
        model.addAttribute("activePage", "dsa-analysis");

        return "dsa/analysis";
    }

    @GetMapping("/api/benchmark/search")
    @ResponseBody
    public List<DsaBenchmarkResultDto> runSearchApi(@RequestParam(value = "size", defaultValue = "1000") int size) {
        return benchmarkService.runSearchBenchmarks(size);
    }

    @GetMapping("/api/benchmark/sort")
    @ResponseBody
    public List<DsaBenchmarkResultDto> runSortApi(@RequestParam(value = "size", defaultValue = "1000") int size) {
        return benchmarkService.runSortBenchmarks(size);
    }
}
