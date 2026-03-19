package level1;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Represents one leaderboard record.
 *
 * CSV columns:
 * username,score,hits,misses,timestampEpoch,level
 */
public class ScoreEntry {
    private String username;
    private int score;
    private int hits;
    private int misses;
    private long timestampEpoch;
    private String level;

    public ScoreEntry(String username, int score, int hits, int misses, long timestampEpoch, String level) {
        this.username = username;
        this.score = score;
        this.hits = hits;
        this.misses = misses;
        this.timestampEpoch = timestampEpoch;
        this.level = level;
    }

    public String getUsername() { return username; }
    public int getScore() { return score; }
    public int getHits() { return hits; }
    public int getMisses() { return misses; }
    public long getTimestampEpoch() { return timestampEpoch; }
    public String getLevel() { return level; }

    public void setUsername(String username) { this.username = username; }
    public void setScore(int score) { this.score = score; }
    public void setHits(int hits) { this.hits = hits; }
    public void setMisses(int misses) { this.misses = misses; }
    public void setTimestampEpoch(long timestampEpoch) { this.timestampEpoch = timestampEpoch; }
    public void setLevel(String level) { this.level = level; }

    /** Human-friendly timestamp for UI. */
    public String getTimestampISO() {
        return DateTimeFormatter.ISO_LOCAL_DATE_TIME
                .withZone(ZoneId.systemDefault())
                .format(Instant.ofEpochMilli(timestampEpoch));
    }

    @Override
    public String toString() {
        return username + " score=" + score + " hits=" + hits + " misses=" + misses +
                " time=" + timestampEpoch + " level=" + level;
    }
}
