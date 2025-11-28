package portfolio.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import portfolio.services.StockApiService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/stocks")
@CrossOrigin(origins = "*") // Allow requests from frontend
public class StockController {

    @Autowired
    private StockApiService stockApiService;

    // Just use a local ObjectMapper instead of autowiring a bean
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Single endpoint:
    // - /api/stocks/price?symbol=TSLA
    // - /api/stocks/price?name=Tesla
    @GetMapping("/price")
    public ResponseEntity<Map<String, Object>> getStockPrice(
            @RequestParam(required = false) String symbol,
            @RequestParam(required = false, name = "name") String companyName
    ) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Case 1: user passes symbol directly
            if (symbol != null && !symbol.isBlank()) {
                Double price = stockApiService.getPrice(symbol);

                if (price == null) {
                    response.put("error", "Price not available for symbol: " + symbol);
                    return ResponseEntity.status(404).body(response);
                }

                response.put("symbol", symbol);
                response.put("price", price);
                return ResponseEntity.ok(response);
            }

            // Case 2: user passes company name → use helper in service
            if (companyName != null && !companyName.isBlank()) {
                Map<String, Object> data = stockApiService.getStockInfoByCompany(companyName);

                if (data == null) {
                    response.put("error", "Company or price not found for: " + companyName);
                    return ResponseEntity.status(404).body(response);
                }

                return ResponseEntity.ok(data);
            }

            // Case 3: nothing passed
            response.put("error", "Either 'symbol' or 'name' query parameter is required.");
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            response.put("error", "An error occurred: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // /api/stocks/timeSeries
    // - /api/stocks/timeSeries?symbol=TSLA&interval=1h
    // - /api/stocks/timeSeries?name=Apple&interval=1day
    // Optional: &startDate=YYYY-MM-DD&endDate=YYYY-MM-DD
    @GetMapping("/timeSeries")
    public ResponseEntity<Map<String, Object>> getTimeSeries(
            @RequestParam(required = false) String symbol,
            @RequestParam(required = false, name = "name") String companyName,
            @RequestParam(defaultValue = "1h") String interval,
            @RequestParam(required = false, name = "startDate") String startDate,
            @RequestParam(required = false, name = "endDate") String endDate
    ) {
        try {
            String finalSymbol = null;
            String finalCompanyName = null;

            // Case 1: symbol directly
            if (symbol != null && !symbol.isBlank()) {
                finalSymbol = symbol;
            }
            // Case 2: lookup symbol by company name
            else if (companyName != null && !companyName.isBlank()) {
                finalCompanyName = companyName;
                finalSymbol = stockApiService.searchSymbol(companyName);

                if (finalSymbol == null) {
                    Map<String, Object> error = new HashMap<>();
                    error.put("error", "Company not found: " + companyName);
                    return ResponseEntity.status(404).body(error);
                }
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Either 'symbol' or 'name' query parameter is required.");
                return ResponseEntity.badRequest().body(error);
            }

            JsonNode timeSeries = stockApiService.getTimeSeries(finalSymbol, interval, startDate, endDate);

            if (timeSeries == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Time series data not available for symbol: " + finalSymbol);
                return ResponseEntity.status(404).body(error);
            }

            // Build final response as a Map
            Map<String, Object> result = new HashMap<>();
            result.put("symbol", finalSymbol);
            result.put("interval", interval);

            if (finalCompanyName != null) {
                result.put("companyName", finalCompanyName);
            }
            if (startDate != null && !startDate.isBlank()) {
                result.put("startDate", startDate);
            }
            if (endDate != null && !endDate.isBlank()) {
                result.put("endDate", endDate);
            }

            // Convert JsonNode to Map/Object for proper serialization
            if (timeSeries.has("values")) {
                result.put("values", objectMapper.convertValue(timeSeries.get("values"), Object.class));
            } else {
                result.put("data", objectMapper.convertValue(timeSeries, Object.class));
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "An error occurred: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
}