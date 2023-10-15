package com.innovativeintelli.ml;

import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.regression.RandomForestRegressor;
import org.apache.spark.ml.regression.RandomForestRegressionModel;
import org.apache.spark.ml.evaluation.RegressionEvaluator;
import org.apache.spark.sql.functions;

import java.io.FileWriter;
import java.io.IOException;

public class PowerballNumberGenerationUsingML {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: PowerballNumberGenerationUsingML <number_of_predictions>");
            System.exit(1);
        }

        int numberOfPredictions = Integer.parseInt(args[0]);
        String outputFilePath = "predicted_powerball_numbers.txt";

        // Initialize SparkSession
        SparkSession sparkSession = SparkSession.builder()
                .appName("Powerball Number Generator")
                .master("local[*]")
                .getOrCreate();

        // Read the Powerball data from CSV
        String powerBallFilePath = PowerballNumberGenerationUsingML.class.getResource("/powerball.csv").getPath();
        Dataset<Row> powerballData = sparkSession.read().option("header", true)
                .csv(powerBallFilePath)
                .withColumn("month", functions.col("month").cast("int"))
                .withColumn("day_of_week", functions.col("day_of_week").cast("int"))
                .withColumn("year", functions.col("year").cast("int"))
                .withColumn("pow1", functions.col("pow1").cast("int"))
                .withColumn("pow2", functions.col("pow2").cast("int"))
                .withColumn("pow3", functions.col("pow3").cast("int"))
                .withColumn("pow4", functions.col("pow4").cast("int"))
                .withColumn("pow5", functions.col("pow5").cast("int"))
                .withColumn("powball", functions.col("powball").cast("int"));

        // Rename columns and assemble features into a vector
        String[] featureColumns = {"month", "day_of_week", "year", "pow1", "pow2", "pow3", "pow4", "pow5"};
        VectorAssembler assembler = new VectorAssembler()
                .setInputCols(featureColumns)
                .setOutputCol("features");

        Dataset<Row> assembledData = assembler.transform(powerballData)
                .withColumnRenamed("powball", "powerball_number");

        // Split data into training and testing sets
        Dataset<Row>[] splits = assembledData.randomSplit(new double[]{0.8, 0.2});
        Dataset<Row> trainingData = splits[0]; // 80% of the data for training
        Dataset<Row> testData = splits[1]; // 20% of the data for testing

        // Create and train the RandomForestRegressor model with tuned hyperparameters
        RandomForestRegressor randomForestRegressor = new RandomForestRegressor()
                .setLabelCol("powerball_number")
                .setFeaturesCol("features")
                .setMaxDepth(15) // set maximum depth of trees (experiment with different values)
                .setNumTrees(200) // increase the number of trees
                .setFeatureSubsetStrategy("auto"); // experiment with different strategies

        RandomForestRegressionModel model = randomForestRegressor.fit(trainingData);

        // Make predictions on test data
        Dataset<Row> predictions = model.transform(testData);

        // Evaluate the model using RMSE
        RegressionEvaluator evaluator = new RegressionEvaluator()
                .setLabelCol("powerball_number")
                .setPredictionCol("prediction")
                .setMetricName("rmse");

        double rmse = evaluator.evaluate(predictions);
        System.out.println("Root Mean Squared Error (RMSE) on test data = " + rmse);
        try {
            // Create a FileWriter instance
            FileWriter writer = new FileWriter(outputFilePath);
        // Generate the specified number of Powerball number sets
        for (int i = 0; i < numberOfPredictions; i++) {
            // Make prediction using the trained model
            Dataset<Row> randomTestData = testData.sample(true, 0.2); // Sample 20% of the test data randomly
            Dataset<Row> randomPredictions = model.transform(randomTestData);

            // Extract the individual predicted numbers
            int pow1 = (int) randomPredictions.first().get(randomPredictions.schema().fieldIndex("pow1"));
            int pow2 = (int) randomPredictions.first().get(randomPredictions.schema().fieldIndex("pow2"));
            int pow3 = (int) randomPredictions.first().get(randomPredictions.schema().fieldIndex("pow3"));
            int pow4 = (int) randomPredictions.first().get(randomPredictions.schema().fieldIndex("pow4"));
            int pow5 = (int) randomPredictions.first().get(randomPredictions.schema().fieldIndex("pow5"));
            int powball = (int) randomPredictions.first().get(randomPredictions.schema().fieldIndex("powerball_number"));

            // Print the predicted Powerball numbers for the current set
            System.out.println("Predicted Powerball Numbers " + (i + 1) + ": " + pow1 + "," + pow2 + "," + pow3 + "," + pow4 + "," + pow5 + "," + powball);
            writer.write(pow1 + "," + pow2 + "," + pow3 + "," + pow4 + "," + pow5 + "," + powball + "\n");
        }
            // Close the FileWriter
            writer.close();

            // Inform the user about the output file location
            System.out.println("Predicted Powerball numbers have been saved to: " + outputFilePath);

        } catch (IOException e) {
            // Handle IO exceptions
            e.printStackTrace();
        }

        // Stop the SparkSession
        sparkSession.stop();
    }
}
