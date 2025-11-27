package portfolio.controllers;

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
}
