package portfolio.models;

public class Portfolio {

    private int portfolioId;
    private String portfolioName;
    private int userId;   // owner

    public Portfolio() {
    }

    public Portfolio(int portfolioId, String portfolioName, int userId) {
        this.portfolioId = portfolioId;
        this.portfolioName = portfolioName;
        this.userId = userId;
    }

    public int getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(int portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
