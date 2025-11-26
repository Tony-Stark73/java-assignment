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

    // Endpoint: /api/stocks/search?name=Tesla
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchStock(@RequestParam String name) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Get symbol from company name
            String symbol = stockApiService.searchSymbol(name);

            if (symbol == null) {
                response.put("error", "Company not found: " + name);
                return ResponseEntity.status(404).body(response);
            }

            // Get price for the symbol
            Double price = stockApiService.getPrice(symbol);

            if (price == null) {
                response.put("error", "Price not available for: " + symbol);
                return ResponseEntity.status(404).body(response);
            }

            // Return successful response
            response.put("symbol", symbol);
            response.put("price", price);
            response.put("companyName", name);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "An error occurred: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // Alternative endpoint: /api/stocks/price?symbol=TSLA
    @GetMapping("/price")
    public ResponseEntity<Map<String, Object>> getStockPrice(@RequestParam String symbol) {
        Map<String, Object> response = new HashMap<>();

        try {
            Double price = stockApiService.getPrice(symbol);

            if (price == null) {
                response.put("error", "Price not available for symbol: " + symbol);
                return ResponseEntity.status(404).body(response);
            }

            response.put("symbol", symbol);
            response.put("price", price);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "An error occurred: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}