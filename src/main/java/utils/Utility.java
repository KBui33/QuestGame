package utils;

public abstract class Utility {
    public static int[] shiftLeft(int[] arr, int steps) {
        int n = arr.length;
        while(steps > 0) {
            steps--;
            int first = arr[0];
            for (int i = 1; i < n; i++) {
                arr[i - 1] = arr[i];
            }
            arr[n - 1] = first;
        }

        return arr;
    }
}
