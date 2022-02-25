import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;

public class ReduceTask implements Callable<ReduceTaskResult> {
    ArrayList<MapTaskResult> fileResult;

    public ReduceTask(ArrayList<MapTaskResult> fileResult) {
        this.fileResult = fileResult;
    }

    @Override
    public ReduceTaskResult call() throws Exception {
        // get the fileName
        String fileName = fileResult.get(0).getFilename();

        // array with all the words from the mapping operation
        ArrayList<String> allWords = new ArrayList<>();

        // array with all the maps from the mapping operation
        ArrayList<HashMap<Integer, Integer>> allMaps = new ArrayList<>();

        // add all words and hashMaps
        for (MapTaskResult result : fileResult) {
            allMaps.add(result.getWordSizeMap());
            allWords.addAll(result.getMaxLengthWords());
        }

        // combine the hashMaps
        HashMap<Integer, Integer> finalMap = combineMaps(allMaps);

        // get the rank
        float rank = process(finalMap);

        // get the words with maximum length from the document
        ArrayList<String> maxLengthWords = getMaxLengthWords(allWords);

        return new ReduceTaskResult(fileName,
                                    rank,
                                    maxLengthWords.get(0).length(),
                                    maxLengthWords.size());
    }

    // function that combines an array of hashMaps into a single one
    // if the key is present in both maps, the new value will be the sum of the values
    private HashMap<Integer, Integer> combineMaps(ArrayList<HashMap<Integer, Integer>> hashMaps) {
        HashMap<Integer, Integer> finalMap = new HashMap<>();

        for (HashMap<Integer, Integer> hashMap : hashMaps) {
            for (Integer key : hashMap.keySet()) {
                if (key != 0) { // ignore the "words" with 0 length
                    if(!finalMap.containsKey(key)) {
                        finalMap.put(key, hashMap.get(key));
                    } else {
                        finalMap.put(key, finalMap.get(key) + hashMap.get(key));
                    }
                }
            }
        }

        return finalMap;
    }

    // function that computes the rank of a document with a formula based on Fibonacci numbers
    private float process(HashMap<Integer, Integer> hashMap) {
        int totalWords = hashMap.values().stream().reduce(0, Integer::sum);
        long sum = 0;

        for (Integer key : hashMap.keySet()) {
            sum += Constants.getFibonacciNumber(key) * hashMap.get(key);
        }

        return (float) sum / totalWords;
    }

    // function that extracts the string(s) with maximum length from an array of strings
    private ArrayList<String> getMaxLengthWords(ArrayList<String> words) {
        ArrayList<String> maxLengthWords = new ArrayList<>();

        for (String word : words) {
            if (maxLengthWords.isEmpty()) {
                maxLengthWords.add(word);
            } else if (word.length() > maxLengthWords.get(0).length()) {
                maxLengthWords.clear();
                maxLengthWords.add(word);
            } else if (word.length() == maxLengthWords.get(0).length()) {
                maxLengthWords.add(word);
            }
        }

        return maxLengthWords;
    }
}
