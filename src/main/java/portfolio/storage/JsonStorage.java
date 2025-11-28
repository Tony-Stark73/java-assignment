package portfolio.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import portfolio.models.PortfolioData;

import java.io.File;

public class JsonStorage {

    private static final String FILE_PATH = "src/main/resources/portfolio.json";
    private static final ObjectMapper mapper = new ObjectMapper();

    public static PortfolioData loadData() {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                return new PortfolioData(); // empty structure
            }
            return mapper.readValue(file, PortfolioData.class);
        } catch (Exception e) {
            return new PortfolioData();
        }
    }

    public static void saveData(PortfolioData data) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), data);
        } catch (Exception ignored) {
        }
    }
}
