package edu.neu.coe.info6205.sort.elementary;

import edu.neu.coe.info6205.sort.*;

import edu.neu.coe.info6205.sort.Helper;
import edu.neu.coe.info6205.sort.HelperFactory;
import edu.neu.coe.info6205.util.Benchmark_Timer;
import edu.neu.coe.info6205.util.Config;
import org.ini4j.Ini;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static edu.neu.coe.info6205.sort.InstrumentedHelper.HITS;

public class InsertionBenchmark{

    // region  fields
    private final Helper<Integer> helper;
    private HashMap<String, Supplier<Integer[]>> arrayMap;

    private final HashMap<String, Double> timeMap;
    private final int n;

    SortWithHelper<Integer> sorter;

    public static final String TRUE = "true";
    public static final String FALSE = "";
    public static final String INSTRUMENTING = InstrumentedHelper.INSTRUMENTING;
    public static final String INVERSIONS = InstrumentedHelper.INVERSIONS;
    public static final String SEED = "seed";
    public static final String CUTOFF = "cutoff";
    public static final String SWAPS = InstrumentedHelper.SWAPS;
    public static final String COMPARES = InstrumentedHelper.COMPARES;
    public static final String COPIES = InstrumentedHelper.COPIES;
    public static final String FIXES = InstrumentedHelper.FIXES;
    public static final String INSURANCE = "";
    public static final String NOCOPY = "";
    // endregion

    // region  config
    public static Config setupConfig(final String instrumenting, final String seed, final String inversions, String cutoff, String interimInversions) {
        final Ini ini = new Ini();
        final String sInstrumenting = INSTRUMENTING;
        ini.put(Config.HELPER, Config.INSTRUMENT, instrumenting);
        ini.put(Config.HELPER, SEED, seed);
        ini.put(Config.HELPER, CUTOFF, cutoff);
        ini.put(sInstrumenting, INVERSIONS, inversions);
        ini.put(sInstrumenting, SWAPS, instrumenting);
        ini.put(sInstrumenting, COMPARES, instrumenting);
        ini.put(sInstrumenting, COPIES, instrumenting);
        ini.put(sInstrumenting, FIXES, instrumenting);
        ini.put(sInstrumenting, HITS, instrumenting);
        ini.put("huskyhelper", "countinteriminversions", interimInversions);
        return new Config(ini);
    }
    // endregion

    public InsertionBenchmark(int n) {
        this.n = n;
        final Config config = setupConfig("true", "0", "1", "", "");
        this.helper = HelperFactory.create("InsertionSort", n, config);
        sorter = new InsertionSort<>(helper);
        helper.init(n);
        arrayMap = new HashMap<>();
        arrayMap.put("random", this::createRandomArray);
        arrayMap.put("ordered", this::createOrderedArray);
        arrayMap.put("reversed", this::createReverseOrderedArray);
        arrayMap.put("partiallyOrdered", this::createPartiallyOrderedArray);
        timeMap = new HashMap<>();
    }

    // region  methods

    /**
     * Method to create a random array of Integers.
     *
     * @return an array of Integers.
     */
    public Integer[] createRandomArray() {
        return helper.random(Integer.class, r -> r.nextInt(n * 10));
    }

    /**
     * create an array with n elements in ascending order
     * @return an array of Integers.
     */
    public Integer[] createOrderedArray() {
        Integer[] orderedArray = new Integer[n];

        for (int i = 0; i < n; i++) {
            orderedArray[i] = i * 10;
        }

        return orderedArray;
    }

    /**
     * create an array with n elements in partially ascending order
     * @return an array of Integers.
     */
    public Integer[] createPartiallyOrderedArray() {
        Integer[] partiallyOrderedArray = new Integer[n];

        for (int i = 0; i < n; i++) {
            if (i < n / 2) {
                partiallyOrderedArray[i] = i * 10;
            } else {
                partiallyOrderedArray[i] = (int) (Math.random() * n * 10);
            }
        }

        return partiallyOrderedArray;
    }

    /**
     * create an array with n elements in descending order
     * @return an array of Integers.
     */
    public Integer[] createReverseOrderedArray() {
        Integer[] reverseOrderedArray = new Integer[n];

        for (int i = 0; i < n; i++) {
            reverseOrderedArray[i] = (n - i) * 10;
        }

        return reverseOrderedArray;
    }

    public void run() {
        for (Map.Entry<String, Supplier<Integer[]>> entry : arrayMap.entrySet()) {
            Consumer<Integer[]> consumer = (x) -> sorter.sort(x, 0, x.length);
            double time = new Benchmark_Timer<>("InsertionSort " + entry.getKey(),
                    consumer).runFromSupplier(entry.getValue(), 10);
            timeMap.put(entry.getKey(), time);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(n);
        for(Map.Entry<String, Double> set : timeMap.entrySet()) {
            sb.append("\t");
            sb.append(set.getValue());
        }
        return sb.toString();
    }

    // endregion

    public static void main(String[] args) {
        InsertionBenchmark[] benchmarks = new InsertionBenchmark[8];

        for (int i = 1; i <= benchmarks.length; i ++) {
            InsertionBenchmark insertionBenchmark = new InsertionBenchmark( (int)Math.pow(2,i) * 100);
            insertionBenchmark.run();
            benchmarks[i - 1] = insertionBenchmark;
        }

        System.out.println("n \trandom \tordered \tpartiallyOrdered \treversed");

        for (InsertionBenchmark benchmark : benchmarks) {
            System.out.println(benchmark);
        }
    }
}
