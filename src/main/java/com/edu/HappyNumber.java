package com.edu;

import java.util.HashSet;
import java.util.Set;

public class HappyNumber {
    public static void main(String[] args) {
        System.out.println("Hello World!");
    }

    /*
    Function isHappy(n):
        Create a set to track seen numbers
        While n is not 1 and n is not in the set:
            Add n to the set
            n = total(n) (sum of squares of digits)
        Return true if n equals 1, false otherwise
     */

    public static int total(int n) {
        int sum = 0;
        while (n > 0) {
            int current = n % 10;
            sum += current * current;
            n /= 10;
        }
        return sum;
    }

    public static boolean isHappy(int n) {
        Set<Integer> seen = new HashSet<>();
        while (!seen.contains(n) && n != 1) {
            n = total(n);
            seen.add(n);
        }
        return n == 1;
    }

    public static boolean isHappy2(int n) {
        int slow = n;
        int fast = n;
        do {
            slow = total(slow); // Move a slow pointer by 1
            fast = total(total(fast)); // Move a fast pointer by 2
            if (fast == 1) return true;
        } while (fast != slow); // if they meet, we found a cycle
        return false;
    }


    /*
    Mathematical Basis:
    In happy number sequences, every non-happy number eventually falls into a cycle containing 4. This is a mathematical property that has been proven.
     */
    public static boolean isHappy3(int n) {
        while (n != 1 && n != 4) {
            n = total(n);
        }
        return n == 1;
    }
}
