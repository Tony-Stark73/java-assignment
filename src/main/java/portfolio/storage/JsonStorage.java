
package portfolio.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import portfolio.models.Stock;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JsonStorage {

    private static final String FILE_PATH = "src/main/resources/portfolio.json";
    private static final ObjectMapper mapper = new ObjectMapper();

    public static List<Stock> loadStocks() {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) return new ArrayList<>();
            return mapper.readValue(file, new TypeReference<List<Stock>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static void saveStocks(List<Stock> stocks) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), stocks);
        } catch (Exception ignored) {}
    }
}
