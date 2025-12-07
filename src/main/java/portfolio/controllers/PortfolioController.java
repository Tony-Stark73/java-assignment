package portfolio.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import portfolio.models.Portfolio;
import portfolio.models.Stock;
import portfolio.models.User;
import portfolio.services.PortfolioApiService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PortfolioController {

    private final PortfolioApiService portfolioService;

    public PortfolioController(PortfolioApiService portfolioService) {
        this.portfolioService = portfolioService;
    }

    /* ====== USER ENDPOINTS ====== */

    // POST /api/user  – Create a new user
    @PostMapping("/user")
    public ResponseEntity<User> createUser(@RequestBody Map<String, String> body) {
        String userName = body.get("userName");
        if (userName == null || userName.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        User user = portfolioService.createUser(userName);
        return ResponseEntity.ok(user);
    }

    // GET /api/users – Get info on all users
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(portfolioService.getAllUsers());
    }

    // GET /api/user/{userId} – Get info on 1 user
    @GetMapping("/user/{userId}")
    public ResponseEntity<User> getUser(@PathVariable int userId) {
        User user = portfolioService.getUserById(userId);
        if (user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(user);
    }

    /* ====== PORTFOLIO ENDPOINTS ====== */

    // POST /api/portfolio – Create a new portfolio under a user
    @PostMapping("/portfolio")
    public ResponseEntity<Portfolio> createPortfolio(@RequestBody Map<String, String> body) {
        String name = body.get("portfolioName");
        String userIdStr = body.get("userId");
        if (name == null || userIdStr == null) {
            return ResponseEntity.badRequest().build();
        }
        int userId = Integer.parseInt(userIdStr);
        Portfolio p = portfolioService.createPortfolio(userId, name);
        if (p == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(p);
    }

    // GET /api/user/{userId}/portfolios – Get all portfolios under a user
    @GetMapping("/user/{userId}/portfolios")
    public ResponseEntity<List<Portfolio>> getPortfoliosForUser(@PathVariable int userId) {
        return ResponseEntity.ok(portfolioService.getPortfoliosForUser(userId));
    }

    // GET /api/user/{userId}/portfolio/{portfolioId} – Get a single portfolio with more details
    @GetMapping("/user/{userId}/portfolio/{portfolioId}")
    public ResponseEntity<Map<String, Object>> getPortfolioForUser(
            @PathVariable int userId,
            @PathVariable int portfolioId) {

        Portfolio portfolio = portfolioService.getPortfolioForUser(userId, portfolioId);
        if (portfolio == null) return ResponseEntity.notFound().build();

        List<Stock> shares = portfolioService.getSharesForPortfolio(userId, portfolioId);

        return ResponseEntity.ok(
                Map.of(
                        "portfolio", portfolio,
                        "shares", shares
                )
        );
    }

    /* ====== TRADES / SHARES ====== */

    // POST /api/user/{userId}/portfolio/{portfolioId}/add – Add trades under user portfolio
    @PostMapping("/user/{userId}/portfolio/{portfolioId}/add")
    public ResponseEntity<?> addTrade(
            @PathVariable int userId,
            @PathVariable int portfolioId,
            @RequestBody Map<String, String> body) {

        String company = body.get("company");
        String amountStr = body.get("amount");
        String marketValue = body.get("marketValue");

        if (company == null || amountStr == null || marketValue == null) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Missing fields"));
        }

        int amount = Integer.parseInt(amountStr);

        Stock stock = portfolioService.addTradeToPortfolio(userId, portfolioId, company, amount, marketValue);
        if (stock == null) {
            // Check if portfolio exists to determine error type
            if (portfolioService.getPortfolioForUser(userId, portfolioId) == null) {
                return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "User/Portfolio not found"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("status", "failed", "message", "provide correct symbol"));
            }
        }

        String msg = amount + " units of share bought at " + company +
                " for portfolio - " + portfolioId;

        return ResponseEntity.ok(Map.of("status", "success", "message", msg, "share", stock));
    }

    // DELETE /api/user/{userId}/portfolio/{portfolioId}/delete?shareId=XYZ
    @DeleteMapping("/user/{userId}/portfolio/{portfolioId}/delete")
    public ResponseEntity<?> deleteTrade(
            @PathVariable int userId,
            @PathVariable int portfolioId,
            @RequestParam int shareId) {

        boolean removed = portfolioService.deleteTrade(userId, portfolioId, shareId);
        if (!removed) {
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", "Trade / Portfolio not found"));
        }
        return ResponseEntity.ok(Map.of("status", "success", "message", "Trade deleted"));
    }

    // GET /api/user/{userId}/portfolio/{portfolioId}/profits
    @GetMapping("user/{userId}/portfolio/{portfolioId}/profits")
    public Map<String, Object> getProfit(@PathVariable int userId, @PathVariable int portfolioId) {
        return portfolioService.getPortfolioProfit(userId, portfolioId);
    }
}
