package level1;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Loads leaderboard.csv from the classpath into an ArrayList.
 *
 * IMPORTANT:
 * - In Eclipse, put leaderboard.csv in your resources folder.
 * - Mark resources as a Source Folder so getResourceAsStream works.
 */
public class LeaderboardRepository {

    public static ArrayList<ScoreEntry> loadFromResource(String resourceName) throws IOException {
        String path = "/" + resourceName;

        try (InputStream in = LeaderboardRepository.class.getResourceAsStream(path)) {
            if (in == null) {
                throw new FileNotFoundException("Missing resource: " + path +
                        " (Put " + resourceName + " in your resources source folder.)");
            }
            return parseCsv(in);
        }
    }

    private static ArrayList<ScoreEntry> parseCsv(InputStream in) throws IOException {
        ArrayList<ScoreEntry> entries = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line = br.readLine(); // header
            if (line == null) return entries;

            while ((line = br.readLine()) != null) {
                // Simple CSV parsing (no quoted commas in our generated data)
                String[] parts = line.split(",", -1);
                if (parts.length < 6) continue;

                String username = parts[0].trim();
                int score = safeParseInt(parts[1].trim());
                int hits = safeParseInt(parts[2].trim());
                int misses = safeParseInt(parts[3].trim());
                long ts = safeParseLong(parts[4].trim());
                String level = parts[5].trim();

                entries.add(new ScoreEntry(username, score, hits, misses, ts, level));
            }
        }
        return entries;
    }

    private static int safeParseInt(String s) {
        try { return Integer.parseInt(s); } catch (Exception e) { return 0; }
    }

    private static long safeParseLong(String s) {
        try { return Long.parseLong(s); } catch (Exception e) { return 0L; }
    }

    /**
     * Optional: save updated leaderboard back out.
     * NOTE: You typically shouldn't overwrite classpath resources in a packaged app.
     * In Eclipse, writing to a normal file next to your project is fine.
     */
    public static void saveToFile(String filename, ArrayList<ScoreEntry> entries) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(filename, false))) {
            out.println("username,score,hits,misses,timestampEpoch,level");
            for (ScoreEntry e : entries) {
                out.printf("%s,%d,%d,%d,%d,%s%n",
                        e.getUsername(), e.getScore(), e.getHits(), e.getMisses(), e.getTimestampEpoch(), e.getLevel());
            }
        }
    }
}
