package phonebook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {
        SimpleSearch search = new SimpleSearch(
                "/home/denamyte/projects/study/hyperskill/java/tests_12_phone_book/directory.txt",
                "/home/denamyte/projects/study/hyperskill/java/tests_12_phone_book/find.txt");
        search.work();
    }
}

class SimpleSearch {

    static final int MILLIS_PER_SECOND = 1000;
    static final int MILLIS_PER_MINUTE = 60 * MILLIS_PER_SECOND;

    private final String bookFileName;
    private final String searchFileName;
    private Map<String, String> phoneMap;

    public SimpleSearch(String bookFileName, String searchFileName) {
        this.bookFileName = bookFileName;
        this.searchFileName = searchFileName;
    }

    public void work() throws IOException {
        System.out.println("Start searching...");
        final long start = System.currentTimeMillis();
        readBookFile();
        final int[] searchSizeAndCount = searchNames();
        final String timeString = millisToTimeString(System.currentTimeMillis() - start);
        System.out.printf("Found %d / %d entries. Time taken: %s%n",
                          searchSizeAndCount[1], searchSizeAndCount[0], timeString);
    }

    private void readBookFile() throws IOException {
        phoneMap = Files.lines(Path.of(bookFileName))
                .map(SimpleSearch::parseRecord)
                .collect(Collectors.toMap(ar -> ar[0], ar -> ar[1], (s1, s2) -> s1, TreeMap::new));
    }

    private static String[] parseRecord(String raw) {
        final int i = raw.indexOf(" ");
        return new String[] {raw.substring(i + 1), raw.substring(0, i)};
    }

    private int[] searchNames() throws IOException {
        final List<String> names = Files.lines(Path.of(searchFileName)).collect(Collectors.toList());
        final long foundCount = names.stream().map(phoneMap::get)
                .filter(Objects::nonNull)
                .count();
        return new int[]{names.size(), (int) foundCount};
    }

    private static String millisToTimeString(long millis) {
        final long minutes = millis / MILLIS_PER_MINUTE;
        millis %= MILLIS_PER_MINUTE;
        final long seconds = millis / MILLIS_PER_SECOND;
        millis %= MILLIS_PER_SECOND;
        return String.format("%d min. %d sec. %d ms.", minutes, seconds, millis);
    }
}
