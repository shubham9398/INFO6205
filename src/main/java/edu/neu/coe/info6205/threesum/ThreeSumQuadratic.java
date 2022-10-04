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
        List<Triple> result = new ArrayList<>();
        int length = a.length;

        // check validation
        if(a == null || length < 3)
            return result.toArray(new Triple[0]);

        // sort the array
        Arrays.sort(a);

        // first loop from start to end of the array
        for(int i = 0; i < length - 2; i++)
        {
            // science the array is sorted, there is not a chance that sum is 0, if we loop to a number that is bigger than 0
            if(a[i]>0)
                break;

            // skip if we meet duplicate number
            if(i > 0 && a[i] == a[i - 1])
                continue;

            // 2 pointers that loop from start and end to the center
            int left = i + 1;
            int right = length - 1;

            // second loop
            while(left < right)
            {
                int sum = a[i] + a[left] + a[right];

                // check the sum
                if(sum == 0)
                {
                    // if the sum is 0, which is what we want, remember it
                    result.add(new Triple(a[i], a[left], a[right]));

                    // then move 2 pointer one step the the center
                    left++;
                    right--;

                    // skip duplicate numbers
                    while(left < right && a[left] == a[left - 1])
                        left++;

                    while(left < right && a[right] == a[right + 1])
                        right--;
                }
                // if the sum is smaller than 0, we need to move left pointer, to make the sum bigger to approach 0
                else if(sum < 0)
                    left++;

                    // similarly, move the right pointer to make the sum small to approach 0
                else
                    right--;
            }
        }

        return result.stream().distinct().toArray(Triple[]::new);
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

                    while(left < right && a[left] == a[left - 1])
                        left++;

                    while(left < right && a[right] == a[right + 1])
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
