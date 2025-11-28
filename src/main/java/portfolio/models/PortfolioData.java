package portfolio.models;

import java.util.ArrayList;
import java.util.List;

public class PortfolioData {

    private List<User> users = new ArrayList<>();
    private List<Portfolio> portfolios = new ArrayList<>();
    private List<Stock> shares = new ArrayList<>();

    public PortfolioData() {
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Portfolio> getPortfolios() {
        return portfolios;
    }

    public void setPortfolios(List<Portfolio> portfolios) {
        this.portfolios = portfolios;
    }

    public List<Stock> getShares() {
        return shares;
    }

    public void setShares(List<Stock> shares) {
        this.shares = shares;
    }
}
