package portfolio.models;

public class Stock {

    private String symbol;
    private int quantity;
    private double buyPrice;

    public Stock() {
    }

    ;

    public Stock(String symbol, int quantity, double buyPrice) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.buyPrice = buyPrice;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getBuyPrice() {
        return buyPrice;

    }
    public void setBuyPrice(double buyPrice) {
        this.buyPrice = buyPrice;
    }
}