package com.oleksandr.financeplatform.controller;

import com.oleksandr.financeplatform.dto.analytics.MonthlySummaryResponse;
import com.oleksandr.financeplatform.service.AnalyticsService;
import java.time.YearMonth;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/summary")
    public MonthlySummaryResponse getSummary(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth month
    ) {
        return analyticsService.getMonthlySummary(month == null ? YearMonth.now() : month);
    }
}
