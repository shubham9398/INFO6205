package edu.neu.coe.info6205.sort.par;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

/**
 * This code has been fleshed out by Ziyao Qiao. Thanks very much.
 * TODO tidy it up a bit.
 */
public class Main {


    /*-this one records sorting data-*/
    public static class TimeRecord
    {
        public int CutOff;
        public int ThreadNum;
        public int ArraySize;
        public long Time;

        @Override
        public String toString() {
            return ThreadNum + "\t" + CutOff + "\t" + Time;
        }
    }

    /*-parameters-*/
    static Random random = new Random();
    static int[] arraySize = {1000000};
    static int[] threadNums = {1,2,4,8,16,32}; // 1,2,4,8,16,32,64,128,256,512,1024
    static int cutOffStartPercentage = 1;
    static int cutOfEndPercentage = 90;
    static double cutOfStepPercentage = 0.5;
    static int repeat = 30;

    /*-for exporting csv-*/
    static String filePrefix = "./src/ParSortResult";
    static String fileExtension = ".csv";
    static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("_yyyy_MM_dd_HH_mm_ss");

    static ArrayList<TimeRecord> timeRecords = new ArrayList<TimeRecord>();

    public static void main(String[] args) {
        processArgs(args);

        LoopArraySize();

        WriteToCsv();
    }

    public static void WriteToCsv()
    {
        if(timeRecords.size() == 0)
        {
            return;
        }

        System.out.println("Start export to csv ..");

        try
        {
            LocalDateTime now = LocalDateTime.now();
            String currentDateTime = dtf.format(now);
            FileOutputStream fis = new FileOutputStream(filePrefix + currentDateTime + fileExtension);
            OutputStreamWriter isr = new OutputStreamWriter(fis);
            BufferedWriter bw = new BufferedWriter(isr);

            for(int size : arraySize)
            {
                // headers
                bw.write("Array Size: " + size + ",\n");

                ArrayList<TimeRecord> filtered = timeRecords
                        .stream()
                        .filter(x -> x.ArraySize == size)
                        .collect(Collectors.toCollection(ArrayList::new));

                bw.write("Cutoff,Cutoff/arraySize,");

                for(int threadCount : threadNums)
                {
                    bw.write(threadCount + " Thread(s),");
                }

                bw.write("\n");

                // content
                for(double i = cutOffStartPercentage; i < cutOfEndPercentage; i += cutOfStepPercentage)
                {
                    int cutoff = (int)(size * i / 100);
                    bw.write(cutoff + ","  + cutoff * 1.0 / size + ",");

                    ArrayList<TimeRecord> filtered2 = filtered
                            .stream()
                            .filter(x -> x.CutOff == cutoff)
                            .collect(Collectors.toCollection(ArrayList::new));

                    for (int threadCount : threadNums)
                    {
                        ArrayList<TimeRecord> filtered3 = filtered2
                                .stream()
                                .filter(x -> x.ThreadNum == threadCount)
                                .collect(Collectors.toCollection(ArrayList::new));

                        if(filtered3.size() == 0)
                        {
                            bw.write("0,");
                        }
                        else
                        {
                            long time = filtered3.get(0).Time;
                            bw.write(time + ",");
                        }
                    }

                    bw.write("\n");
                }
                bw.write("\n");
            }
            bw.close();

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        System.out.println("Export "+timeRecords.size()+" records to csv done");

    }

    private static void LoopArraySize()
    {
        for(int i = 0; i<arraySize.length; i++)
        {
            int size = arraySize[i];
            System.out.println("Array Size: " + size);

            LoopThreadNum(size);
        }
    }

    private static void LoopThreadNum(int arraySize) {
        for (int i = 0; i < threadNums.length; i++) {

            int threadNum = threadNums[i];
            ParSort.setThreadNum(threadNum);
            System.out.println("\tThread Num: " + threadNum);

            LoopCutOffs(arraySize, threadNum);
        }
    }
    private static void LoopCutOffs(int arraySize, int threadNum)
    {
        // content
        for(double i = cutOfEndPercentage; i > cutOffStartPercentage; i -= cutOfStepPercentage)
        {
            int cutOff = (int)(arraySize * i / 100);
            ParSort.setCutoff(cutOff);
            System.out.print("\t\tCutoff: " + cutOff);

            timeRecords.add(GetParSortRecord(arraySize, threadNum, cutOff));
        }
    }

    private static TimeRecord GetParSortRecord(int arraySize, int threadNum, int cutoff)
    {
        TimeRecord record = new TimeRecord();
        record.ArraySize = arraySize;
        record.ThreadNum = threadNum;
        record.CutOff = cutoff;

        int[] array = new int[arraySize];

        for (int i = 0; i < array.length; i++) array[i] = random.nextInt();

        long time = GetParSortTime(array);
        record.Time = time;

//        System.out.println("Degree of parallelism: " + ForkJoinPool.getCommonPoolParallelism());

        System.out.println("\t" + repeat + " times, time usage: " + time + " using " + ParSort.getThreadNum() + " threads");

        return record;
    }

    private static long GetParSortTime(int[] array)
    {
        long time = 0;

        if(repeat < 1)
        {
            return time;
        }

        long start = System.currentTimeMillis();

        for (int k = 0; k < repeat; k++)
        {
            ParSort.sort(array, 0, array.length);
        }

        long end = System.currentTimeMillis();

        return end - start;
    }

    private static void processArgs(String[] args) {
        String[] xs = args;
        while (xs.length > 0)
            if (xs[0].startsWith("-")) xs = processArg(xs);
    }

    private static String[] processArg(String[] xs) {
        String[] result = new String[0];
        System.arraycopy(xs, 2, result, 0, xs.length - 2);
        processCommand(xs[0], xs[1]);
        return result;
    }

    private static void processCommand(String x, String y) {
        if (x.equalsIgnoreCase("N")) setConfig(x, Integer.parseInt(y));
        else
            // TODO sort this out
            if (x.equalsIgnoreCase("P")) //noinspection ResultOfMethodCallIgnored
                ForkJoinPool.getCommonPoolParallelism();
    }

    private static void setConfig(String x, int i) {
        configuration.put(x, i);
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static final Map<String, Integer> configuration = new HashMap<>();


}
