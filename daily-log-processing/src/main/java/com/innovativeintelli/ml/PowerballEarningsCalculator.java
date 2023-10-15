package com.innovativeintelli.ml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PowerballEarningsCalculator {
    public static void main(String[] args) {
        List<String[]> winningNumbers = readCSV("winning_numbers.csv");
        List<String[]> predictions = readCSV("predictions.csv");

        Map<String, Integer> earningsMap = new HashMap<>();
        initializeEarningsMap(earningsMap);

        int totalEarnings = 0;

        for (String[] prediction : predictions) {
            String predictedNumbers = prediction[0].split(": ")[1];
            prediction[0] = predictedNumbers;
            String predictedPowerball = prediction[5].trim();
            for (String[] winningNumber : winningNumbers) {
                if (matchesNumbers(prediction, winningNumber) && predictedPowerball.equals(winningNumber[9].trim())) {
                    String key = "5+PB";
                    totalEarnings += earningsMap.get(key);
                    break;
                } else if (matchesNumbers(prediction, winningNumber)) {
                    String key = "5";
                    totalEarnings += earningsMap.get(key);
                    break;
                }
            }
        }

        System.out.println("Total earnings: $" + totalEarnings);
    }

    private static void initializeEarningsMap(Map<String, Integer> earningsMap) {
        earningsMap.put("5+PB", 100000000);
        earningsMap.put("5", 1000000);
        earningsMap.put("4+PB", 50000);
        earningsMap.put("4", 100);
        earningsMap.put("3+PB", 100);
    }

    private static boolean matchesNumbers(String[] predictedNumbers, String[] winningNumber) {
        for (int i = 0; i < 5; i++) {
            if (!predictedNumbers[i].trim().equals(winningNumber[i + 4].trim())) {
                return false;
            }
        }
        return true;
    }

    private static List<String[]> readCSV(String filePath) {
        List<String[]> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                PowerballEarningsCalculator.class.getResourceAsStream("/" + filePath)))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                data.add(values);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
