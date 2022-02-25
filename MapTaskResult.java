import java.util.ArrayList;
import java.util.HashMap;

/**
 * class that represents the result of the Map operation
 */
public class MapTaskResult {
    private final String filename;
    private final ArrayList<String> maxLengthWords;
    private final HashMap<Integer, Integer> wordSizeMap;

    public MapTaskResult(String filename,
                         ArrayList<String> maxLengthWords,
                         HashMap<Integer, Integer> wordSizeMap) {
        this.filename = filename;
        this.maxLengthWords = maxLengthWords;
        this.wordSizeMap = wordSizeMap;
    }

    public String getFilename() {
        return filename;
    }

    public ArrayList<String> getMaxLengthWords() {
        return maxLengthWords;
    }

    public HashMap<Integer, Integer> getWordSizeMap() {
        return wordSizeMap;
    }
}
