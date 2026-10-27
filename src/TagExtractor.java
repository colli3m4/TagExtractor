import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TagExtractor {
    private static final Path DEFAULT_INPUT = Paths.get("data", "pride-and-prejudice.txt");
    private static final Path DEFAULT_STOP_WORDS = Paths.get("data", "stopwords.txt");
    private static final Path DEFAULT_OUTPUT = Paths.get("output", "tags.txt");
    private static final int MAX_TAGS = 100;
    private static final Pattern WORD_PATTERN =
            Pattern.compile("\\p{L}[\\p{L}\\p{M}'’\\-]*");

    private TagExtractor() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 0 && args.length != 3) {
            System.err.println("Usage: java Main [<input.txt> <stopwords.txt> <output.txt>]");
            System.exit(2);
        }

        Path input = args.length == 0 ? DEFAULT_INPUT : Paths.get(args[0]);
        Path stopWordsFile = args.length == 0 ? DEFAULT_STOP_WORDS : Paths.get(args[1]);
        Path output = args.length == 0 ? DEFAULT_OUTPUT : Paths.get(args[2]);

        Set<String> stopWords = loadStopWords(stopWordsFile);
        String text = readLiteratureText(input);
        Map<String, Integer> frequencies = countTags(text, stopWords);
        List<Map.Entry<String, Integer>> tags = rankTags(frequencies);
        writeTags(output, tags);

        System.out.printf("Input: %s%nStop words: %s (%d words)%n", input, stopWordsFile,
                stopWords.size());
        System.out.printf("Extracted %d unique tags; saved the top %d to %s.%n",
                frequencies.size(), Math.min(tags.size(), MAX_TAGS), output);
        for (int i = 0; i < Math.min(20, tags.size()); i++) {
            Map.Entry<String, Integer> tag = tags.get(i);
            System.out.printf("%2d. %-18s %d%n", i + 1, tag.getKey(), tag.getValue());
        }
    }

    private static Set<String> loadStopWords(Path path) throws IOException {
        Set<String> stopWords = new HashSet<>();
        for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
            String word = line.trim().toLowerCase(Locale.ROOT);
            if (!word.isEmpty() && !word.startsWith("#")) {
                stopWords.add(word);
            }
        }
        return stopWords;
    }

    private static String readLiteratureText(Path path) throws IOException {
        String text = Files.readString(path, StandardCharsets.UTF_8);
        String[] lines = text.split("\\R", -1);
        StringBuilder body = new StringBuilder(text.length());
        int contentStart = 0;
        int contentEnd = lines.length;

        for (int i = 0; i < lines.length; i++) {
            if (lines[i].toUpperCase(Locale.ROOT).contains("*** START OF")) {
                contentStart = i + 1;
                break;
            }
        }
        if (contentStart > 0) {
            for (int i = contentStart; i < lines.length; i++) {
                if (lines[i].toUpperCase(Locale.ROOT).contains("*** END OF")) {
                    contentEnd = i;
                    break;
                }
            }
        }
        for (int i = contentStart; i < contentEnd; i++) {
            body.append(lines[i]).append('\n');
        }
        return body.toString();
    }

    private static Map<String, Integer> countTags(String text, Set<String> stopWords) {
        Map<String, Integer> frequencies = new HashMap<>();
        Matcher matcher = WORD_PATTERN.matcher(text);
        while (matcher.find()) {
            String word = matcher.group().toLowerCase(Locale.ROOT)
                    .replace('’', '\'')
                    .replaceAll("^[\\-'’]+|[\\-'’]+$", "");
            if (word.endsWith("'s") && word.length() > 2) {
                word = word.substring(0, word.length() - 2);
            }
            if (word.length() > 1 && !stopWords.contains(word)) {
                frequencies.merge(word, 1, Integer::sum);
            }
        }
        return frequencies;
    }

    private static List<Map.Entry<String, Integer>> rankTags(Map<String, Integer> frequencies) {
        List<Map.Entry<String, Integer>> tags = new ArrayList<>(frequencies.entrySet());
        tags.sort(Comparator.<Map.Entry<String, Integer>>comparingInt(Map.Entry::getValue)
                .reversed()
                .thenComparing(Map.Entry::getKey));
        return tags;
    }

    private static void writeTags(Path path, List<Map.Entry<String, Integer>> tags)
            throws IOException {
        Path parent = path.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        List<String> lines = new ArrayList<>();
        lines.add("tag\tcount");
        for (int i = 0; i < Math.min(tags.size(), MAX_TAGS); i++) {
            Map.Entry<String, Integer> tag = tags.get(i);
            lines.add(tag.getKey() + "\t" + tag.getValue());
        }
        Files.write(path, lines, StandardCharsets.UTF_8);
    }
}
