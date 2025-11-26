package portfolio.services;

import portfolio.models.Stock;
import portfolio.models.Portfolio;
import portfolio.storage.JsonStorage;

import java.util.List;

public class PortfolioService {

    private Portfolio portfolio;

    public PortfolioService() {
        // load stocks from JSON
        List<Stock> savedStocks = JsonStorage.loadStocks();
        portfolio = new Portfolio();

        // put saved stocks into portfolio
        for (Stock s : savedStocks) {
            portfolio.addStock(s);
        }
    }

    public List<Stock> getAllStocks() {
        return portfolio.getStocks();
    }

    public void addStock(Stock stock) {
        portfolio.addStock(stock);
        JsonStorage.saveStocks(portfolio.getStocks());
    }

    public void removeStock(String symbol) {
        portfolio.removeStock(symbol);
        JsonStorage.saveStocks(portfolio.getStocks());
    }

    public Stock findStock(String symbol) {
        return portfolio.findStock(symbol);
    }
}
