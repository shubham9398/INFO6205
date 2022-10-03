package edu.neu.coe.info6205.threesum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of ThreeSum which follows the approach of dividing the solution-space into
 * N sub-spaces where each sub-space corresponds to a fixed value for the middle index of the three values.
 * Each sub-space is then solved by expanding the scope of the other two indices outwards from the starting point.
 * Since each sub-space can be solved in O(N) time, the overall complexity is O(N^2).
 * <p>
 * NOTE: The array provided in the constructor MUST be ordered.
 */
public class ThreeSumQuadratic implements ThreeSum {
    /**
     * Construct a ThreeSumQuadratic on a.
     * @param a a sorted array.
     */
    public ThreeSumQuadratic(int[] a) {
        this.a = a;
        length = a.length;
    }

    public Triple[] getTriples() {
        List<Triple> triples = new ArrayList<>();
        for (int i = 0; i < length; i++) triples.addAll(getTriples(i));
        Collections.sort(triples);
        return triples.stream().distinct().toArray(Triple[]::new);
    }

    /**
     * Get a list of Triples such that the middle index is the given value j.
     *
     * @param j the index of the middle value.
     * @return a Triple such that
     */
    public List<Triple> getTriples(int j) {
        List<Triple> triples = new ArrayList<>();

        // check the array validation
        if(a == null || a.length < 3 || a.length < j + 1) return triples;

        // mid value
        int mid = a[j];

        Arrays.sort(a);

        int len = a.length;

        for (int i = 0; i < len - 2; i++)
        {
            // return is the value is bigger than 0 and middle value
            // since the array is sorted, the value can only be bigger than 0 and middle value
            if(a[i] > 0 || a[i] > mid) break;

            // skip the duplicate value
            if(i > 0 && a[i] == a[i - 1]) continue;

            // create left and right pointer
            int left = i + 1;
            int right = len - 1;

            // move the pointer toward the middle and see if the sum of a[i], a[left], a[right] is 0
            while (left < right)
            {
                int sum = a[i] + a[left] + a[right];
                if (sum == 0)
                {
                    // if the left one is equal the middle value, add the triple to the list
                    if (a[left] == mid){
                        triples.add(new Triple(a[i], a[left], a[right]));
                    }
                    left++;
                    right--;
                }
                else if (sum < 0)
                {
                    left++;
                }
                else
                {
                    right--;
                }
            }
        }

        return triples;
    }

    private final int[] a;
    private final int length;
}
