import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Tema2 {
    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        final int workers;
        final String inputFile;
        final String outputFile;
        final int fragmentSize;
        final int nrOfDocuments;
        final ArrayList<String> documentPath = new ArrayList<>();
        final ArrayList<String> documentNames = new ArrayList<>();
        final ArrayList<Long> documentSize = new ArrayList<>();

        // pending results from Map operation
        final List<Future<MapTaskResult>> futureMapResults = new ArrayList<>();

        // pending results from Reduce operation
        final List<Future<ReduceTaskResult>> toOutputFuture = new ArrayList<>();

        // results from Reduce operation
        final List<ReduceTaskResult> toOutput = new ArrayList<>();

        if (args.length < 3) {
            System.err.println("Usage: Tema2 <workers> <in_file> <out_file>");
        }

        // read input
        workers = Integer.parseInt(args[0]);
        inputFile = args[1];
        outputFile = args[2];

        BufferedReader inputReader = new BufferedReader(new FileReader(inputFile));

        fragmentSize = Integer.parseInt(inputReader.readLine());
        nrOfDocuments = Integer.parseInt(inputReader.readLine());

        for (int i = 0; i < nrOfDocuments; i++) {
            String file = inputReader.readLine();
            documentPath.add(file);
            documentNames.add(file.substring(file.lastIndexOf("/") + 1));
            documentSize.add(new File(file).length());
        }

        // ----- MAP ----- //
        // create the thread pool for Map tasks
        ExecutorService mapExecutor = Executors.newFixedThreadPool(workers);

        for (int i = 0; i < nrOfDocuments; i++) {
            long remainingBytes = documentSize.get(i);
            long offset = 0;

            while (remainingBytes > fragmentSize) {
                Future<MapTaskResult> result =
                        mapExecutor.submit(new MapTask(documentPath.get(i),
                                                        offset,
                                                        fragmentSize,
                                                        documentSize.get(i)));
                futureMapResults.add(result);
                offset += fragmentSize;
                remainingBytes -= fragmentSize;
            }

            // assign Map tasks
            Future<MapTaskResult> result =
                    mapExecutor.submit(new MapTask(documentPath.get(i),
                                                    offset,
                                                    (int)remainingBytes,
                                                    documentSize.get(i)));

            // the pending result is stored
            futureMapResults.add(result);
        }

        mapExecutor.shutdown();

        // ----- REDUCE ----- //
        // create the thread pool for Reduce tasks
        ExecutorService reduceExecutor = Executors.newFixedThreadPool(workers);

        int fileIndex = 0;
        int resultIndex = 0;

        // one file -> one task
        while (fileIndex < nrOfDocuments) {
            String currentFile = documentNames.get(fileIndex);
            MapTaskResult result = futureMapResults.get(resultIndex).get();
            ArrayList<MapTaskResult> fileResult = new ArrayList<>();

            while (result.getFilename().equals(currentFile)) {
                fileResult.add(result);
                resultIndex++;

                if (resultIndex >= futureMapResults.size()) {
                    break;
                }

                result = futureMapResults.get(resultIndex).get();
            }

            // assign Reduce tasks
            Future<ReduceTaskResult> output = reduceExecutor.submit(new ReduceTask(fileResult));

            // the pending result is stored
            toOutputFuture.add(output);

            fileIndex++;
        }

        reduceExecutor.shutdown();

        // we need the results from Reduce operation now for output
        for (Future<ReduceTaskResult> future : toOutputFuture) {
            toOutput.add(future.get());
        }

        // sort the documents by rank (descending)
        toOutput.sort(ReduceTaskResult::compareTo);

        // write the output
        FileWriter outFile = new FileWriter(outputFile);

        for (ReduceTaskResult result : toOutput) {
            outFile.write(result.toString() + "\n");
        }

        outFile.close();
    }
}
