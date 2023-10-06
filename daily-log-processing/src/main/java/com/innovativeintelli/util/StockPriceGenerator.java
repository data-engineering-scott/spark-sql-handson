import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class StockPriceGenerator {
    public static void main(String[] args) {
        String fileName = "stock_prices.csv";
        String[] symbols = {
                "AAPL", "GOOGL", "AMZN", "TSLA", "MSFT",
                "FB", "NFLX", "GOOG", "IBM", "INTC",
                "NVDA", "ADBE", "CRM", "PYPL", "AMAT",
                "ATVI", "CTSH", "EA", "HPQ", "ORCL",
                "QCOM", "TSM", "TXN", "MU", "WDC",
                "ACN", "AVGO", "CSCO", "DELL", "HPE",
                "JNPR", "LRCX", "NTAP", "SNPS", "STX",
                "TDC", "VRSN", "XRX", "ZBRA", "ZM"
        };

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("symbol,price\n");

            Random random = new Random();

            for (String symbol : symbols) {
                double price = 50 + (random.nextDouble() * 450); // Generate a random price between 50 and 500
                String line = String.format("%s,%.2f\n", symbol, price);
                writer.write(line);
            }

            System.out.println("CSV file generation completed: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
