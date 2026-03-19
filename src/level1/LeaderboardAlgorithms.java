package level1;
 
import java.util.ArrayList;
 
/**
 * Sorting and searching algorithms for the Leaderboard.
 *
 * Sorting algorithm used: Insertion Sort
 *
 * How Insertion Sort works:
 *   We walk through the list one element at a time.
 *   For each element we "insert" it into the correct position
 *   among the elements we have already seen (the sorted portion
 *   to the left).  We do this by shifting larger (or smaller)
 *   neighbours one spot to the right until we find the right slot.
 *
 * Time complexity: O(n²) worst case — fine for leaderboard sizes.
 */
public class LeaderboardAlgorithms {
 
    /**
     * Sorts the list by score in DESCENDING order (highest score first).
     *
     * Algorithm: Insertion Sort
     *
     * We maintain a "sorted" region on the left side of the list.
     * For each new entry we slide it leftward past every entry whose
     * score is LOWER, stopping when we find one with an equal or
     * higher score (or we reach the start of the list).
     *
     * @param list the ArrayList of ScoreEntry objects to sort in place
     */
    public static void sortByScoreDescending(ArrayList<ScoreEntry> list) {
        // Start at index 1; index 0 is already a sorted region of size 1
        for (int i = 1; i < list.size(); i++) {
 
            // The entry we are about to place in the correct position
            ScoreEntry current = list.get(i);
 
            // j will walk backward through the sorted region
            int j = i - 1;
 
            // Shift entries with a LOWER score one position to the right
            // to make room for 'current'
            while (j >= 0 && list.get(j).getScore() < current.getScore()) {
                list.set(j + 1, list.get(j)); // move the smaller entry right
                j--;
            }
 
            // Place 'current' in its correct sorted position
            list.set(j + 1, current);
        }
    }
 
    /**
     * Sorts the list by username in ASCENDING order (A → Z).
     *
     * Algorithm: Insertion Sort (same logic, different comparison)
     *
     * We walk forward through the list.  For each entry we slide it
     * leftward past every entry whose username comes AFTER it
     * alphabetically, until we find a username that is equal or comes
     * before it (or we reach the start).
     *
     * @param list the ArrayList of ScoreEntry objects to sort in place
     */
    public static void sortByUsernameAscending(ArrayList<ScoreEntry> list) {
        // Start at index 1; index 0 is already a sorted region of size 1
        for (int i = 1; i < list.size(); i++) {
 
            // The entry we are placing into the correct sorted position
            ScoreEntry current = list.get(i);
 
            // j walks backward through the already-sorted region
            int j = i - 1;
 
            // Shift entries whose username comes AFTER 'current' to the right
            while (j >= 0 && list.get(j).getUsername()
                                       .compareToIgnoreCase(current.getUsername()) > 0) {
                list.set(j + 1, list.get(j)); // move the later username right
                j--;
            }
 
            // Drop 'current' into its correct alphabetical slot
            list.set(j + 1, current);
        }
    }
 
    /**
     * Binary search for an EXACT username match (case-insensitive).
     *
     * PRECONDITION: the list must already be sorted by username ascending
     * (call sortByUsernameAscending first).
     *
     * How binary search works:
     *   We keep track of a "low" and "high" boundary.
     *   We repeatedly look at the entry in the MIDDLE of that range.
     *   If the middle username matches, we are done.
     *   If the target comes before the middle (alphabetically) we move
     *     "high" down — the answer must be in the left half.
     *   If the target comes after the middle we move "low" up — the
     *     answer must be in the right half.
     *   We stop when low > high, meaning the target is not in the list.
     *
     * @param list     ArrayList sorted by username ascending
     * @param username the username to search for
     * @return index of the matching entry, or -1 if not found
     */
    public static int binarySearchByUsername(ArrayList<ScoreEntry> list, String username) {
        int low  = 0;               // left boundary of the search range
        int high = list.size() - 1; // right boundary of the search range
 
        while (low <= high) {
            // Pick the index exactly in the middle to avoid overflow
            int mid = low + (high - low) / 2;
 
            // Compare the middle entry's username to the target
            int cmp = list.get(mid).getUsername().compareToIgnoreCase(username);
 
            if (cmp == 0) {
                // Found an exact match — return its index
                return mid;
            } else if (cmp < 0) {
                // Middle username comes BEFORE the target → search right half
                low = mid + 1;
            } else {
                // Middle username comes AFTER the target → search left half
                high = mid - 1;
            }
        }
 
        // Target username was not found in the list
        return -1;
    }
 
    /**
     * Binary search for a score value.
     *
     * PRECONDITION: the list must already be sorted by score DESCENDING
     * (highest first). Call sortByScoreDescending before calling this.
     *
     * How it works:
     *   We start with the full list as our search range: low = 0, high = last index.
     *   Each pass we calculate mid — the exact middle of the current range.
     *   We compare the score at mid to the target:
     *     • If they match, we are done — return mid.
     *     • If the target is HIGHER than mid's score, it must be in the
     *       LEFT half (remember: sorted highest-first), so we set high = mid - 1.
     *     • If the target is LOWER than mid's score, it must be in the
     *       RIGHT half, so we set low = mid + 1.
     *   Each iteration cuts the remaining search range in half.
     *   When low > high the range is empty — the score is not in the list.
     *
     * @param list        ArrayList sorted by score descending
     * @param targetScore the score value to find
     * @return index of the first matching entry, or -1 if not found
     */
    public static int binarySearchByScore(ArrayList<ScoreEntry> list, int targetScore) {
        // low is the leftmost index still in the search range
        int low = 0;
        // high is the rightmost index still in the search range
        int high = list.size() - 1;
 
        while (low <= high) {
            // mid is the index halfway between low and high.
            // Written as low + (high - low) / 2 to prevent integer overflow.
            int mid = low + (high - low) / 2;
 
            // Score of the entry at the midpoint
            int midScore = list.get(mid).getScore();
 
            if (midScore == targetScore) {
                // Exact match found — return this index
                return mid;
            } else if (targetScore > midScore) {
                // Target is larger than mid's score.
                // Because the list is sorted DESCENDING, larger values are
                // to the LEFT — shrink the right boundary.
                high = mid - 1;
            } else {
                // Target is smaller than mid's score.
                // Smaller values are to the RIGHT — raise the left boundary.
                low = mid + 1;
            }
        }
 
        // low has passed high: the target score is not in the list
        return -1;
    }
}