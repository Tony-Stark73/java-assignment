package portfolio.services;

import org.springframework.stereotype.Service;
import portfolio.models.*;
import portfolio.storage.JsonStorage;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class PortfolioApiService {

    // Simple in-memory ID generators (you can replace with something else if you want)
    private final AtomicInteger userIdGenerator = new AtomicInteger(1);
    private final AtomicInteger portfolioIdGenerator = new AtomicInteger(1);
    private final AtomicInteger shareIdGenerator = new AtomicInteger(1);

    private final StockApiService stockApiService;

    public PortfolioApiService(StockApiService stockApiService) {
        this.stockApiService = stockApiService;
    }

    private PortfolioData load() {
        return JsonStorage.loadData();
    }

    private void save(PortfolioData data) {
        JsonStorage.saveData(data);
    }

    /* ========== USER METHODS ========== */

    public User createUser(String userName) {
        PortfolioData data = load();
        int id = userIdGenerator.getAndIncrement();
        User user = new User(id, userName);
        data.getUsers().add(user);
        save(data);
        return user;
    }

    public List<User> getAllUsers() {
        return load().getUsers();
    }

    public User getUserById(int userId) {
        return load().getUsers()
                .stream()
                .filter(u -> u.getUserId() == userId)
                .findFirst()
                .orElse(null);
    }

    /* ========== PORTFOLIO METHODS ========== */

    public Portfolio createPortfolio(int userId, String portfolioName) {
        PortfolioData data = load();

        // validate user exists
        boolean userExists = data.getUsers().stream()
                .anyMatch(u -> u.getUserId() == userId);
        if (!userExists) {
            return null;
        }

        int id = portfolioIdGenerator.getAndIncrement();
        Portfolio p = new Portfolio(id, portfolioName, userId);
        data.getPortfolios().add(p);
        save(data);
        return p;
    }

    public List<Portfolio> getPortfoliosForUser(int userId) {
        PortfolioData data = load();
        return data.getPortfolios()
                .stream()
                .filter(p -> p.getUserId() == userId)
                .collect(Collectors.toList());
    }

    public Portfolio getPortfolioForUser(int userId, int portfolioId) {
        PortfolioData data = load();
        return data.getPortfolios()
                .stream()
                .filter(p -> p.getUserId() == userId && p.getPortfolioId() == portfolioId)
                .findFirst()
                .orElse(null);
    }

    /* ========== SHARE / STOCK METHODS ========== */

    public Stock addTradeToPortfolio(int userId, int portfolioId, String company,
                                     int amount, String marketValue) {

        PortfolioData data = load();

        Optional<Portfolio> portfolioOpt = data.getPortfolios().stream()
                .filter(p -> p.getUserId() == userId && p.getPortfolioId() == portfolioId)
                .findFirst();

        if (portfolioOpt.isEmpty()) {
            return null;
        }

        // Validate stock symbol
        String validSymbol = stockApiService.searchSymbol(company);
        if (validSymbol == null) {
            return null; // Symbol validation failed
        }

        int id = shareIdGenerator.getAndIncrement();
        Stock stock = new Stock(id, portfolioId, company, amount, marketValue);
        data.getShares().add(stock);
        save(data);
        return stock;
    }

    public boolean deleteTrade(int userId, int portfolioId, int shareId) {
        PortfolioData data = load();

        // ensure portfolio exists for this user
        boolean portfolioExists = data.getPortfolios().stream()
                .anyMatch(p -> p.getUserId() == userId && p.getPortfolioId() == portfolioId);

        if (!portfolioExists) return false;

        boolean removed = data.getShares().removeIf(
                s -> s.getPortfolioId() == portfolioId && s.getShareId() == shareId
        );
        if (removed) {
            save(data);
        }
        return removed;
    }

    public List<Stock> getSharesForPortfolio(int userId, int portfolioId) {
        PortfolioData data = load();

        // check portfolio belongs to user
        boolean portfolioExists = data.getPortfolios().stream()
                .anyMatch(p -> p.getUserId() == userId && p.getPortfolioId() == portfolioId);
        if (!portfolioExists) {
            return List.of();
        }

        return data.getShares().stream()
                .filter(s -> s.getPortfolioId() == portfolioId)
                .collect(Collectors.toList());
    }

    /* ========== PROFITS ========== */

    public Map<String, Object> getPortfolioProfit(int userId, int portfolioId) {
        PortfolioData data = load();
        
        // Validate user exists
        User user = data.getUsers().stream()
                .filter(u -> u.getUserId() == userId)
                .findFirst()
                .orElse(null);
        if (user == null) {
            return Map.of("status", "error", "message", "User not found");
        }

        // Validate portfolio exists and belongs to user
        Portfolio portfolio = data.getPortfolios().stream()
                .filter(p -> p.getPortfolioId() == portfolioId && p.getUserId() == userId)
                .findFirst()
                .orElse(null);
        if (portfolio == null) {
            return Map.of("status", "error", "message", "Portfolio not found");
        }

        // Get all shares for this portfolio
        List<Stock> shares = data.getShares().stream()
                .filter(s -> s.getPortfolioId() == portfolioId)
                .collect(Collectors.toList());

        if (shares.isEmpty()) {
            return Map.of("status", "success", "profit", 0.0);
        }

        double totalProfit = 0.0;

        for (Stock stock : shares) {
            String company = stock.getCompany();
            double currentPrice = stockApiService.getPrice(company);

            if (currentPrice == 0) {
                continue;
            }

            String cleanedValue = stock.getMarketValue().replaceAll("[^0-9.]", "");
            double marketValue = Double.parseDouble(cleanedValue);
            double amount = stock.getAmount();
            double currentValue = currentPrice;

            double profit = (currentValue * amount) - (marketValue * amount);
            totalProfit += profit;
        }

        return Map.of("status", "success", "profit", totalProfit);
    }
}
