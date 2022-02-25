/**
 * class that represents the result of the Reduce operation
 */
public class ReduceTaskResult implements Comparable<ReduceTaskResult> {
    private final String fileName;
    private final float rank;
    private final int maxLength;
    private final int nrOfMaxLength;

    public ReduceTaskResult(String fileName, float rank, int maxLength, int nrOfMaxLength) {
        this.fileName = fileName;
        this.rank = rank;
        this.maxLength = maxLength;
        this.nrOfMaxLength = nrOfMaxLength;
    }

    public double getRank() {
        return rank;
    }

    @Override
    public String toString() {
        return fileName + "," +
                String.format("%.2f", rank) + "," +
                maxLength + "," +
                nrOfMaxLength;
    }

    @Override
    public int compareTo(ReduceTaskResult anotherResult) {
        int result = 0;

        if (rank > anotherResult.getRank()) {
            result = -1;
        } else if (rank <= anotherResult.getRank()) {
            result = 1;
        }

        return result;
    }
}
