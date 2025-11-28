package portfolio.models;

public class Stock {

    // “Share” in the SRS
    private int shareId;
    private int portfolioId;
    private String company;
    private int amount;          // number of units
    private String marketValue;  // e.g. "160$" as in SRS

    public Stock() {
    }

    public Stock(int shareId, int portfolioId, String company, int amount, String marketValue) {
        this.shareId = shareId;
        this.portfolioId = portfolioId;
        this.company = company;
        this.amount = amount;
        this.marketValue = marketValue;
    }

    public int getShareId() {
        return shareId;
    }

    public void setShareId(int shareId) {
        this.shareId = shareId;
    }

    public int getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(int portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getMarketValue() {
        return marketValue;
    }

    public void setMarketValue(String marketValue) {
        this.marketValue = marketValue;
    }
}
