import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.concurrent.Callable;

public class MapTask implements Callable<MapTaskResult> {
    String fileName;
    long offset;
    int dimension;
    long fileSize;

    public MapTask(String fileName, long offset, int dimension, long fileSize) {
        this.fileName = fileName;
        this.offset = offset;
        this.dimension = dimension;
        this.fileSize = fileSize;
    }

    @Override
    public MapTaskResult call() throws Exception {
        byte[] input = new byte[dimension];
        RandomAccessFile raf = new RandomAccessFile(fileName, "r");

        raf.seek(offset);
        raf.readFully(input);

        // don't need to verify the start of the document
        if (offset != 0) {
            input = parseStart(raf, offset, input);
        }

        // don't need to verify the end of the document
        if (offset + dimension < fileSize) {
            input = parseEnd(raf, offset, dimension, fileSize, input);
        }

        return mapWork(new String(input), fileName);
    }

    // if the fragment start in the middle of a word, discard it
    private byte[] parseStart(RandomAccessFile raf, long offset, byte[] input) throws IOException {
        // read the byte before the start of the fragment
        raf.seek(offset - 1);
        char before = (char) raf.readByte();

        // if it's a letter (or a digit), and the first character from the fragment is a letter (or
        // a digit) then that means the fragment start in the middle of a word
        if (!isSpecial(before) && !isSpecial((char) input[0])) {
            int copyFrom = 1;

            while (copyFrom < input.length) {
                if (isSpecial((char) input[copyFrom])) {
                    copyFrom++;
                    break;
                }

                copyFrom++;
            }

            return Arrays.copyOfRange(input, copyFrom, input.length);
        } else {
            return input;
        }
    }

    // if the fragment ends in the middle of a word, take the whole word
    private byte[] parseEnd(RandomAccessFile raf, long offset, int dimension, long fileSize,
                            byte[] input) throws IOException {
        // read the byte after the end of the fragment
        raf.seek(offset + dimension);
        byte afterByte = raf.readByte();

        // if it's a letter (or a digit), and the last character from the fragment is a letter (or a
        // digit) then that means the fragment ends in the middle of a word
        if (input.length > 0 && !isSpecial((char) afterByte)
                && !isSpecial((char) input[input.length - 1])) {
            ArrayList<Byte> afterWord = new ArrayList<>();
            afterWord.add(afterByte);

            if (raf.getFilePointer() < fileSize) {
                afterByte = raf.readByte();

                while (!isSpecial((char) afterByte)) {
                    afterWord.add(afterByte);

                    if (raf.getFilePointer() < fileSize) {
                        afterByte = raf.readByte();
                    } else {
                        break;
                    }
                }
            }

            byte[] result = new byte[input.length + afterWord.size()];

            System.arraycopy(input, 0, result, 0, input.length);

            for (int i = 0; i < afterWord.size(); i++) {
                result[i + input.length] = afterWord.get(i);
            }

            return result;
        } else {
            return input;
        }
    }

    /* creates a MapTaskResult that contains:
     * - filename
     * - an array with the longest word(s) from the fragment
     * - the desired map (wordLength -> nr of words with that length)
     */
    private MapTaskResult mapWork(String inputString, String fileName) {
        ArrayList<String> longestWords = new ArrayList<>();
        int longestWordSize = 0;
        HashMap<Integer, Integer> wordMap = new HashMap<>();
        String[] words = inputString.split("\\P{LD}+");

        for (String word : words) {
            if (word.length() > longestWordSize) {
                longestWordSize = word.length();
                longestWords.clear();
                longestWords.add(word);
            } else if (word.length() == longestWordSize) {
                longestWords.add(word);
            }

            if (wordMap.containsKey(word.length())) {
                wordMap.replace(word.length(), wordMap.get(word.length()) + 1);
            } else {
                wordMap.put(word.length(), 1);
            }
        }

        String realFileName = fileName.substring(fileName.lastIndexOf("/")  + 1);

        return new MapTaskResult(realFileName, longestWords, wordMap);
    }


    // verifies if a character is a separating one or not
    private boolean isSpecial(char character) {
        return (Constants.specialChars.indexOf(character) != -1 || Character.isWhitespace(character));
    }
}
