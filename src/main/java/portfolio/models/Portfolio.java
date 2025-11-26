package portfolio.models;
import java.util.*;
public class Portfolio {

    private List<Stock> stocks;

public Portfolio(){
    this.stocks=new ArrayList<>();
}
public List<Stock> getStocks() {
    return stocks;
}
public void addStock(Stock stock){
    stocks.add(stock);
}
public void removeStock(String symbol){
    for(Stock stock:stocks){
        if(stock.getSymbol().equalsIgnoreCase(symbol)){
            stocks.remove(stock);
            break;
        }
    }
}
public Stock findStock(String symbol){
    for(Stock stock:stocks){
        if(stock.getSymbol().equalsIgnoreCase(symbol)){
            return stock;
        }
    }return null;
}
}




