package com.example.finmonitor.api;

import com.example.finmonitor.application.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/transactions/count")
    public List<Map<String, Object>> countByPeriod(@RequestParam String period) {
        return dashboardService.countByPeriod(period);
    }

    @GetMapping("/transactions/by-type")
    public Map<String, List<Map<String, Object>>> byType(@RequestParam String period) {
        return dashboardService.byType(period);
    }

    @GetMapping("/transactions/sums")
    public Map<String, Object> sumComparison(@RequestParam String period) {
        return dashboardService.sumComparison(period);
    }

    @GetMapping("/transactions/status-count")
    public Map<String, Long> statusCount(@RequestParam String period) {
        return dashboardService.statusCount(period);
    }

    @GetMapping("/transactions/by-bank")
    public List<Map<String, Object>> byBank(
            @RequestParam String role,
            @RequestParam String period
    ) {
        return dashboardService.byBank(role, period);
    }

    @GetMapping("/transactions/by-category")
    public List<Map<String, Object>> byCategory(
            @RequestParam String type,
            @RequestParam String period
    ) {
        return dashboardService.byCategory(type, period);
    }
}