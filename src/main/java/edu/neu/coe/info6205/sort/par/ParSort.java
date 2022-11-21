package edu.neu.coe.info6205.sort.par;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

/**
 * This code has been fleshed out by Ziyao Qiao. Thanks very much.
 * TODO tidy it up a bit.
 */
class ParSort {

    public static int cutoff = 1000;
    public static ForkJoinPool pool = new ForkJoinPool(16);

    public static void sort(int[] array, int from, int to) {
        if (to - from < cutoff) Arrays.sort(array, from, to);
        else {
            int midPoint = (from + to) / 2;
            // FIXME next few lines should be removed from public repo.
            CompletableFuture<int[]> parSort1 = parSort(array, from, midPoint); // TO IMPLEMENT
            CompletableFuture<int[]> parSort2 = parSort(array, midPoint, to); // TO IMPLEMENT
            CompletableFuture<int[]> parSort = parSort1.thenCombine(parSort2, (xs1, xs2) -> {
                int[] result = new int[xs1.length + xs2.length];
                int i = 0;
                int j = 0;
                for (int k = 0; k < result.length; k++) {
                    if (i >= xs1.length) {
                        result[k] = xs2[j++];
                    } else if (j >= xs2.length) {
                        result[k] = xs1[i++];
                    } else if (xs2[j] < xs1[i]) {
                        result[k] = xs2[j++];
                    } else {
                        result[k] = xs1[i++];
                    }
                }
                return result;
            });

            parSort.whenComplete((result, throwable) -> System.arraycopy(result, 0, array, from, result.length));
            parSort.join();
        }
    }

    private static CompletableFuture<int[]> parSort(int[] array, int from, int to) {
        return CompletableFuture.supplyAsync(
                () -> {
                    int[] result = new int[to - from];
                    System.arraycopy(array, from, result, 0, result.length);
                    sort(result, 0, to - from);
                    return result;
                },pool
        );
    }
}