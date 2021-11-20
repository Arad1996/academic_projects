package edu.yu.introtoalgs;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class EstimateSecretAlgorithmsClient2 {
    static class Stopwatch {
        private final long start;
        /**
         * Initializes a new stopwatch.
         */
        public Stopwatch() {
            start = System.nanoTime();
        }

        /**
         * Returns the elapsed CPU time (in nanoseconds) since the stopwatch was created.
         *
         * @return elapsed CPU time (in nanoseconds) since the stopwatch was created
         */
        public double elapsedTime() {
            long now = System.nanoTime();
            return (now - start);
        }
    }

    static class DoublingRatioTester {
        private final String reportPath;
        private final BigOMeasurable testedAlgorithm;
        private final int MIN_N = 50;
        private final double MAX_N = 50 * Math.pow(2, 12);

        public DoublingRatioTester(String reportPath, BigOMeasurable testedAlgorithm) {
            this.reportPath = reportPath;
            this.testedAlgorithm = testedAlgorithm;
        }

        /**
         * Prints progress with time prefix for each line
         * Used to monitor the progress while executed
         * @param message
         */
        private void log(String message) {
            System.out.printf("%s-%s\t%s\n", java.time.LocalDate.now(), java.time.LocalTime.now(), message);
        }

        /**
         * Run the given algorithm
         * @param n input length
         * @return time taken to execute the algorithm
         */
        public double timeTrial(int n) {
            testedAlgorithm.setup(n);
            Stopwatch timer = new Stopwatch();
            testedAlgorithm.execute();
            return timer.elapsedTime();
        }

        /**
         * @param number
         * @return result of log base 2 on the given number
         */
        private double log2(double number) {
            return Math.log(number) / Math.log(2);
        }

        public void executeAndReport() {
            try {
                log(String.format("Starting execution, report will be created at %s", reportPath));

                // Create csv file
                FileWriter fw = new FileWriter(reportPath);
                BufferedWriter reportWriter = new BufferedWriter(fw);

                // Write header of csv file
                reportWriter.write("N,timeTaken,log(N),log(TimeTaken)");
                reportWriter.newLine();

                // Execute the given algorithm and write results to file
                for (int n = MIN_N; n < MAX_N; n += n) {
                    log(String.format("Started execution for n: %d", n ));
                    double time = timeTrial(n);
                    reportWriter.write(String.format("%d,%f,%f,%f", n, time, log2(n), log2(time)));
                    reportWriter.newLine();
                    log(String.format("Done execution for n: %d", n ));
                }

                // Close the file after work done
                reportWriter.close();
            } catch (IOException e) {
                System.out.printf("Got error while trying to create or write to file path '%s'\n", reportPath);
                e.printStackTrace();
                return;
            }
        }

    }

    public static void main(String[] args) {
        BigOMeasurable secretAlgorithm1 = new SecretAlgorithm1();
        new DoublingRatioTester("secretAlgorithm1.csv", secretAlgorithm1).executeAndReport();
        BigOMeasurable secretAlgorithm2 = new SecretAlgorithm2();
        new DoublingRatioTester("secretAlgorithm2.csv", secretAlgorithm2).executeAndReport();
        BigOMeasurable secretAlgorithm3 = new SecretAlgorithm3();
        new DoublingRatioTester("secretAlgorithm3.csv", secretAlgorithm3).executeAndReport();
        BigOMeasurable secretAlgorithm4 = new SecretAlgorithm4();
        new DoublingRatioTester("secretAlgorithm4.csv", secretAlgorithm4).executeAndReport();


    }
}
