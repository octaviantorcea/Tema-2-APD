/**
 * class that contains constants used by the program
 */
public class Constants {
    public static String specialChars = ";:/?˜\\.,><‘[]{}()!@#$%ˆ&-_+’=*”|";

    // all Fibonacci numbers that are integers
    // should cover most of the english words
    private static final int[] fibonacciSequence = {1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144, 233,
            377, 610, 987, 1597, 2584, 4181, 6765, 10946, 17711, 28657, 46368, 75025, 121393,
            196418, 317811, 514229, 832040, 1346269, 2178309, 3524578, 5702887, 9227465, 14930352,
            24157817, 39088169, 63245986, 102334155, 165580141, 267914296, 433494437, 701408733,
            1134903170, 1836311903};

    /*
     * if for whatever reason there is a word with a length bigger than 45, use this to compute
     * the Fibonacci number...
     */
    private static long fibonacciNumber(int length) {
        if (length == 0) {
            return 0;
        }

        if (length == 1) {
            return 1;
        }

        return fibonacciNumber(length - 1) + fibonacciNumber(length - 2);
    }

    public static long getFibonacciNumber(int wordLength) {
        if (wordLength <= 45) {
            return fibonacciSequence[wordLength];
        } else {
            return fibonacciNumber(wordLength + 1);
        }
    }
}
