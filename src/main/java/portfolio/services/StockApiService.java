package portfolio.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class StockApiService {

    private static final String API_KEY = "78daefaaa97b444a8c68e6973f5c9136";
    private static final String BASE_URL = "https://api.twelvedata.com/";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 1️⃣ Convert company name → stock symbol
    public String searchSymbol(String companyName) {
        String url = BASE_URL + "symbol_search?symbol=" + companyName + "&apikey=" + API_KEY;

        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(jsonResponse);

            if (root.has("data")) {
                return root.get("data").get(0).get("symbol").asText(); // first match
            }
        } catch (Exception ignored) {}

        return null;
    }

    // 2️⃣ Get price given stock symbol
    public Double getPrice(String symbol) {
        String url = BASE_URL + "price?symbol=" + symbol + "&apikey=" + API_KEY;

        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(jsonResponse);

            if (root.has("price")) {
                return root.get("price").asDouble();
            }
        } catch (Exception ignored) {}

        return null;
    }

    // 3️⃣ Final helper → Enter company name & get stock price
    public Double getPriceByCompany(String companyName) {
        String symbol = searchSymbol(companyName);

        if (symbol == null) {
            System.out.println("Company not found: " + companyName);
            return null;
        }

        return getPrice(symbol);
    }
}
