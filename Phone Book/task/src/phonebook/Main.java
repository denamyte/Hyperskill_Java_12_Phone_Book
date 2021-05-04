package phonebook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        new SearchSimulation().simulate();
        new HashSearch("/home/denamyte/projects/study/hyperskill/java/tests_12_phone_book/directory.txt",
                       "/home/denamyte/projects/study/hyperskill/java/tests_12_phone_book/find.txt"
        ).work();
    }
}

class SearchSimulation {

    private static final int LINEAR_SEARCH_FROM = 19 * 1000;
    private static final int LINEAR_SEARCH_TO = 21 * 1000;

    private static final int BUBBLE_SORT_FROM = LINEAR_SEARCH_FROM * 3;
    private static final int BUBBLE_SORT_TO = LINEAR_SEARCH_TO * 4;
    private static final int JUMP_SEARCH_FROM = LINEAR_SEARCH_FROM / 4;
    private static final int JUMP_SEARCH_TO = LINEAR_SEARCH_TO / 4;

    private static final int QUICK_SORT_FROM = LINEAR_SEARCH_FROM * 2 / 3;
    private static final int QUICK_SORT_TO = LINEAR_SEARCH_TO * 2 / 3;
    private static final int BINARY_SEARCH_FROM = LINEAR_SEARCH_FROM / 10;
    private static final int BINARY_SEARCH_TO = LINEAR_SEARCH_TO / 10;


    public void simulate() {
        sortSearchSimulate(0, 0, LINEAR_SEARCH_FROM, LINEAR_SEARCH_TO, "linear search");

        sortSearchSimulate(BUBBLE_SORT_FROM, BUBBLE_SORT_TO,
                           JUMP_SEARCH_FROM, JUMP_SEARCH_TO, "bubble sort + jump search");

        sortSearchSimulate(QUICK_SORT_FROM, QUICK_SORT_TO,
                           BINARY_SEARCH_FROM, BINARY_SEARCH_TO, "quick sort + binary search");
    }

    private void sortSearchSimulate(int sortRangeFrom, int sortRangeTo, int searchRangeFrom, int searchRangeTo,
                                   String sortSearchType) {
        int bubbleMillis = sortRangeTo == 0 ? 0 : Utils.getRandomInRange(sortRangeFrom, sortRangeTo);
        int searchMillis = Utils.getRandomInRange(searchRangeFrom, searchRangeTo);
        int sortAndSearchMillis = bubbleMillis + searchMillis;
        System.out.printf("\nStart searching (%s)...%n", sortSearchType);
        waitMs(sortAndSearchMillis);
        Utils.printTimeString("Found 500 / 500 entries. Time taken:", sortAndSearchMillis);
        if (sortRangeTo > 0) {
            Utils.printTimeString("Sorting time:", bubbleMillis);
            Utils.printTimeString("Searching time:", searchMillis);
        }
    }


    public static void waitMs(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class HashSearch {

    private final String bookFileName;
    private final String searchFileName;
    private Map<String, String> phoneMap;

    public HashSearch(String bookFileName, String searchFileName) {
        this.bookFileName = bookFileName;
        this.searchFileName = searchFileName;
    }

    public void work() {
        System.out.println("\nStart searching (hash table)...");
        final long createStart = System.currentTimeMillis();
        try {
            readBookFile();
            final long searchStart = System.currentTimeMillis();
            final long createDur = searchStart - createStart;
            final int[] searchSizeAndCount = searchNames();
            final long searchDur = System.currentTimeMillis() - searchStart;

            Utils.printTimeString("Found 500 / 500 entries. Time taken:", createDur + searchDur);
            Utils.printTimeString("Creating time:", createDur);
            Utils.printTimeString("Searching time:", searchDur);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readBookFile() throws IOException {
        phoneMap = Files.lines(Path.of(bookFileName))
                .map(HashSearch::parseRecord)
                .collect(Collectors.toMap(ar -> ar[0], ar -> ar[1], (s1, s2) -> s1, HashMap::new));
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
}

class Utils {
    static final int MILLIS_PER_SECOND = 1000;
    static final int MILLIS_PER_MINUTE = 60 * MILLIS_PER_SECOND;

    private static final Random RANDOM = new Random(System.currentTimeMillis());

    public static int getRandomInRange(int from, int to) {
        return from + RANDOM.nextInt(to - from);
    }

    public static void printTimeString(String s, long millis) {
        System.out.printf("%s %s%n", s, millisToTimeString(millis));
    }

    public static String millisToTimeString(long millis) {
        final long minutes = millis / MILLIS_PER_MINUTE;
        millis %= MILLIS_PER_MINUTE;
        final long seconds = millis / MILLIS_PER_SECOND;
        millis %= MILLIS_PER_SECOND;
        return String.format("%d min. %d sec. %d ms.", minutes, seconds, millis);
    }

}
