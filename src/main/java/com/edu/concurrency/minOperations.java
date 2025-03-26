package com.edu.concurrency;

import java.util.Arrays;

public class minOperations {
    public static void main(String[] args) {
        // Example 1: Possible solution
        int[][] grid1 = {{2, 4}, {6, 8}};
        int x1 = 2;
        System.out.println("Example 1: " + minOperations(grid1, x1));  // Should output 4

        // Example 2: Another possible solution
        int[][] grid2 = {{1, 5}, {9, 13}};
        int x2 = 4;
        System.out.println("Example 2: " + minOperations(grid2, x2));  // Should output 4

        // Example 3: Impossible solution
        int[][] grid3 = {{1, 2}, {3, 4}};
        int x3 = 3;
        System.out.println("Example 3: " + minOperations(grid3, x3));  // Should output -1

        // Example 4: Detailed example to demonstrate the math check
        int[][] grid4 = {{3, 6}, {9, 12}};
        int x4 = 3;
        System.out.println("Example 4: " + minOperations(grid4, x4));  // Should output 4
    }

    /*
    You are given a 2D integer grid of size m x n and an integer x. In one operation,
    you can add x to or subtract x from any element in the grid. A uni-value grid is a grid where all the elements of it are equal.
    Return the minimum number of operations to make the grid uni-value. If it is not possible, return -1.
     */

    // Input : grid = [[2,4], [6,8]], x = 2
    // Output: 4
    // Explanation: We can make every element equal to 4 by doing the following:
    // - Add x to 2 once.
    // - Subtract x from 6 once.
    // - Subtract x from 8 twice.
    // A total of 4 operations were used.
    public static int minOperations(int[][] grid, int x) {
        int m = grid.length;
        int n = grid[0].length;
        int[] arr = new int[m * n];
        int index = 0;

        // Flatten the grid into a 1D array
        for (int[] row : grid) {
            for (int num : row) {
                arr[index++] = num;
            }
        }

        // check if all elements can be made equal
        // This equation is checking if it's mathematically possible to make all grid elements equal using only +x or -x operations
        // Mathematical Principle
        // for any two numbers to be made equal using only additions or subtractions of x:

        for (int num : arr) {
            if ((num - arr[0]) % x != 0) {
                return -1;
            }
        }

        // Sort the array to find the median
        Arrays.sort(arr);
        int median = arr[arr.length / 2];
        int operations = 0;

        // calculation the total operations needed
        for (int num : arr) {
            operations += Math.abs(num - median) / x;
        }
        return operations;
    }
}
