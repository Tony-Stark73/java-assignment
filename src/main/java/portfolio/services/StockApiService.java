package portfolio.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class StockApiService {

    private static final Dotenv dotenv = Dotenv.load();
    private static final String API_KEY = dotenv.get("TWELVE_DATA_API_KEY");
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

    // 3️⃣ Final helper → Enter company name & get symbol + price + name as a Map
    public Map<String, Object> getStockInfoByCompany(String companyName) {
        String symbol = searchSymbol(companyName);
        if (symbol == null) {
            return null;
        }

        Double price = getPrice(symbol);
        if (price == null) {
            return null;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("symbol", symbol);
        data.put("price", price);
        data.put("companyName", companyName);
        return data;
    }

    // (Optional helper, keep if you use elsewhere)
    public Double getPriceByCompany(String companyName) {
        String symbol = searchSymbol(companyName);

        if (symbol == null) {
            System.out.println("Company not found: " + companyName);
            return null;
        }

        return getPrice(symbol);
    }

    // 4️⃣ Time series helper → get OHLC data for a symbol over a period
    // interval example: 1min, 5min, 15min, 1h, 1day, 1week, 1month
    // dates should be in format YYYY-MM-DD (as per Twelve Data docs)
    public JsonNode getTimeSeries(String symbol, String interval, String startDate, String endDate) {
        StringBuilder urlBuilder = new StringBuilder(
                BASE_URL + "time_series?symbol=" + symbol + "&interval=" + interval + "&apikey=" + API_KEY
        );

        if (startDate != null && !startDate.isBlank()) {
            urlBuilder.append("&start_date=").append(startDate);
        }
        if (endDate != null && !endDate.isBlank()) {
            urlBuilder.append("&end_date=").append(endDate);
        }

        try {
            String jsonResponse = restTemplate.getForObject(urlBuilder.toString(), String.class);
            System.out.println("Raw time_series response: " + jsonResponse);
            JsonNode root = objectMapper.readTree(jsonResponse);

            // Basic error check according to typical Twelve Data responses
            if (root.has("status") && "error".equalsIgnoreCase(root.get("status").asText())) {
                return null;
            }

            return root;
        } catch (Exception e) {
            System.out.println("Error fetching time series: " + e.getMessage());
        }

        return null;
    }
}