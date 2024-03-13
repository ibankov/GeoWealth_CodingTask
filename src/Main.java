import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        Dictionary dictionary = new Dictionary();
        var start = LocalTime.now();
        List<String> validWords = Collections.synchronizedList(new ArrayList<>());
        var futures = dictionary.findPuzzleNineLetterWords()
                .map(future -> future.thenAccept(s -> addToListIfNotNull(s, validWords)))
                .toList();

        CompletableFuture
                .allOf(futures.toArray(new CompletableFuture[0]))
                .thenRun(() -> {
                    System.out.printf("Valid words %s - %s\n", validWords.size(), validWords);
                    var end = LocalTime.now();
                    var duration = Duration.between(start, end);
                    System.out.println("Executed in " + duration.toMillis() + " msec.");
                });

        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(2));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void addToListIfNotNull(String s, List<String> l) {
        if (s != null) l.add(s);
    }

}