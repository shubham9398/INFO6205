package edu.neu.coe.info6205.threesum;

import edu.neu.coe.info6205.util.Benchmark_Timer;
import edu.neu.coe.info6205.util.TimeLogger;
import edu.neu.coe.info6205.util.Utilities;

import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class ThreeSumBenchmark {
    public ThreeSumBenchmark(int runs, int n, int m) {
        this.runs = runs;
        this.supplier = new Source(n, m).intsSupplier(10);
        this.n = n;
    }

    public void runBenchmarks() {
        System.out.println("ThreeSumBenchmark: N=" + n);
        benchmarkThreeSum("ThreeSumQuadratic", (xs) -> new ThreeSumQuadratic(xs).getTriples(), n, timeLoggersQuadratic);
        benchmarkThreeSum("ThreeSumQuadrithmic", (xs) -> new ThreeSumQuadrithmic(xs).getTriples(), n, timeLoggersQuadrithmic);
        benchmarkThreeSum("ThreeSumCubic", (xs) -> new ThreeSumCubic(xs).getTriples(), n, timeLoggersCubic);
    }

    public static void main(String[] args) {
//        new ThreeSumBenchmark(100, 250, 250).runBenchmarks();
//        new ThreeSumBenchmark(50, 500, 500).runBenchmarks();
//        new ThreeSumBenchmark(20, 1000, 1000).runBenchmarks();
//        new ThreeSumBenchmark(10, 2000, 2000).runBenchmarks();
//        new ThreeSumBenchmark(5, 4000, 4000).runBenchmarks();
//        new ThreeSumBenchmark(3, 8000, 8000).runBenchmarks();
//        new ThreeSumBenchmark(2, 16000, 16000).runBenchmarks();


        int[] runs = {100, 50, 20, 10, 5};
        int[] n = {250, 500, 1000, 2000, 4000};
        int[] m = {250, 500, 1000, 2000, 4000};
        String[] descriptions = {"ThreeSumQuadratic", "ThreeSumQuadrithmic", "ThreeSumCubic"};

        ThreeSumBenchmark[] benchmarks = new ThreeSumBenchmark[runs.length];

        for (int i = 0; i < runs.length; i++) {
            benchmarks[i] = new ThreeSumBenchmark(runs[i], n[i], m[i]);
            benchmarks[i].runBenchmarks();
        }

        System.out.println("N\t ThreeSumQuadratic\t ThreeSumQuadrithmic\t ThreeSumCubic");

        for (int i = 0; i < runs.length; i++) {
            String line = n[i] + "\t";
            for(String description: descriptions) {
                line += TimeLogger.formatTime(benchmarks[i].timeMap.get(description)) + "\t";
            }
            System.out.println(line);
        }
    }

    private void benchmarkThreeSum(final String description, final Consumer<int[]> function, int n, final TimeLogger[] timeLoggers) {

        int[] xs = supplier.get();

        if(xs.length < 3) return;

        double time = new Benchmark_Timer<>(description, function).runFromSupplier(supplier, runs);

        timeLoggers[0].log(time, n);
        timeLoggers[1].log(time, n);

        this.timeMap.put(description, time);
    }

    private final static TimeLogger[] timeLoggersCubic = {
            new TimeLogger("Raw time per run (mSec): ", (time, n) -> time),
            new TimeLogger("Normalized time per run (n^3): ", (time, n) -> time / n / n / n * 1e6)
    };
    private final static TimeLogger[] timeLoggersQuadrithmic = {
            new TimeLogger("Raw time per run (mSec): ", (time, n) -> time),
            new TimeLogger("Normalized time per run (n^2 log n): ", (time, n) -> time / n / n / Utilities.lg(n) * 1e6)
    };
    private final static TimeLogger[] timeLoggersQuadratic = {
            new TimeLogger("Raw time per run (mSec): ", (time, n) -> time),
            new TimeLogger("Normalized time per run (n^2): ", (time, n) -> time / n / n * 1e6)
    };

    private final int runs;
    private final Supplier<int[]> supplier;
    private final int n;
    private HashMap<String, Double> timeMap = new HashMap<>();

}
