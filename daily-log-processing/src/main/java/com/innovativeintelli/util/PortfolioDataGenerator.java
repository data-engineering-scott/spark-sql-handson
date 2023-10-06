package com.innovativeintelli.util;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class PortfolioDataGenerator {
    public static void main(String[] args) {
        String fileName = "big_file.csv";
        int numRows = 10000000; // Change this to the desired number of rows
        int maxPortfolioId = 50000; // Change this to the maximum portfolioId value
        Random random = new Random();

        // Array of stock symbols
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
            Map<Integer, String> lastStockSymbol = new HashMap<>();

            for (int i = 1; i <= numRows; i++) {
                int portfolioId = random.nextInt(maxPortfolioId) + 1; // Random portfolioId from 1 to maxPortfolioId
                String stockSymbol = getRandomStockSymbol(symbols);

                // Ensure that the same stock symbol is not repeated for a given portfolio in consecutive rows
                while (stockSymbol.equals(lastStockSymbol.get(portfolioId))) {
                    stockSymbol = getRandomStockSymbol(symbols);
                }

                lastStockSymbol.put(portfolioId, stockSymbol);
                double numberOfShares = getRandomNumberOfShares();
                double cashValue = getRandomCashValue();

                String line = String.format("%d,%s,%.2f,%.2f%n", portfolioId, stockSymbol, numberOfShares, cashValue);
                writer.write(line);
            }
            System.out.println("File generation completed: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getRandomStockSymbol(String[] symbols) {
        return symbols[new Random().nextInt(symbols.length)];
    }

    private static double getRandomNumberOfShares() {
        return Math.floor(Math.random() * 1000); // Random number of shares (0 to 999)
    }

    private static double getRandomCashValue() {
        return Math.random() * 1000.0; // Random cash value (0.00 to 999.99)
    }
}
