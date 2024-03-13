import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Dictionary {
    private static final String WORDS_URL = "https://raw.githubusercontent.com/nikiiv/JavaCodingTestOne/master/scrabble-words.txt";
    private static final List<String> ONE_LETTER_WORDS = List.of("I", "A");
    private static Set<String> allWords = new HashSet<>();

    public Dictionary() {
        try {
            loadAllWords();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Stream<CompletableFuture<String>> findPuzzleNineLetterWords() {
        var nineLetterWords = allWords.stream()
                .filter(w -> w.length() == 9)
                .collect(Collectors.toSet());

        return nineLetterWords.stream().map(this::checkWord);
    }

    private CompletableFuture<String> checkWord(String word) {
        return CompletableFuture.supplyAsync(() -> {
            Set<String> shorterNewWords = findShorterNewWords(word);
            while (!shorterNewWords.isEmpty()) {
                Optional<String> firstWord = shorterNewWords.stream().findFirst();
                if (firstWord.get().length() == 1) return word;
                shorterNewWords = shorterNewWords.stream().map(Dictionary::findShorterNewWords).flatMap(Collection::stream).collect(Collectors.toSet());
            }
            return null;
        });
    }

    private static Set<String> findShorterNewWords(String word) {
        Set<String> shorterWords = new HashSet<>();
        for (int i = 0; i < word.length(); i++) {
            var sb = new StringBuilder(word);
            String shorterWord = sb.deleteCharAt(i).toString();
            if (ONE_LETTER_WORDS.contains(shorterWord)) {
                shorterWords.add(shorterWord);
                break;
            }
            if (allWords.contains(shorterWord)) shorterWords.add(shorterWord);
        }
        return shorterWords;
    }

    private static void loadAllWords() throws IOException {
        var wordsUrl = new URL(WORDS_URL);
        try (var br = new BufferedReader(
                new InputStreamReader(wordsUrl.openConnection().getInputStream()))) {
            allWords = br.lines().skip(2).collect(Collectors.toSet());
        }
    }
}
