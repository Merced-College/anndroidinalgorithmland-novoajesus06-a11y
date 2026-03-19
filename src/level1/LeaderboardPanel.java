package level1;
 
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
 
/**
 * Leaderboard Terminal screen.
 *
 * Uses LeaderboardRepository to load data from leaderboard.csv
 * and uses LeaderboardAlgorithms (student TODOs) for sorting/search.
 */
public class LeaderboardPanel extends JPanel {
    private static final int WIDTH = 900;
    private static final int HEIGHT = 540;
 
    private final AppRouter router;
 
    private final LeaderboardTableModel tableModel = new LeaderboardTableModel();
    private final JTable table = new JTable(tableModel);
 
    private final JTextField searchField = new JTextField(18);
    private final JTextField scoreSearchField = new JTextField(10);
    private final JLabel statusLabel = new JLabel("Load leaderboard.csv to begin.");
 
    private ArrayList<ScoreEntry> allEntries = new ArrayList<>();
 
    public LeaderboardPanel(AppRouter router) {
        this.router = router;
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setLayout(new BorderLayout());
 
        // Top controls — row 1: existing buttons + username search
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton loadBtn = new JButton("Load CSV");
        JButton top20Btn = new JButton("Top 20 (by Score)");
        JButton sortNameBtn = new JButton("Sort by Username");
        JButton searchBtn = new JButton("Search (Binary)");
        JButton backBtn = new JButton("Back to Menu");
 
        top.add(loadBtn);
        top.add(top20Btn);
        top.add(sortNameBtn);
        top.add(new JLabel("Username:"));
        top.add(searchField);
        top.add(searchBtn);
        top.add(backBtn);
 
        // Top controls — row 2: score binary search
        JPanel top2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton scoreSearchBtn = new JButton("Search Score (Binary)");
        top2.add(new JLabel("Score:"));
        top2.add(scoreSearchField);
        top2.add(scoreSearchBtn);
 
        JPanel topWrapper = new JPanel();
        topWrapper.setLayout(new BoxLayout(topWrapper, BoxLayout.Y_AXIS));
        topWrapper.add(top);
        topWrapper.add(top2);
 
        add(topWrapper, BorderLayout.NORTH);
 
        // Table center
        table.setFillsViewportHeight(true);
        add(new JScrollPane(table), BorderLayout.CENTER);
 
        // Status bottom
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(statusLabel, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
 
        // Actions
        loadBtn.addActionListener(e -> doLoad());
 
        top20Btn.addActionListener(e -> {
            if (allEntries.isEmpty()) { status("Load first."); return; }
 
            // TODO: student sorts by score descending
            ArrayList<ScoreEntry> copy = new ArrayList<>(allEntries);
            LeaderboardAlgorithms.sortByScoreDescending(copy);
 
            showRows(copy, 20);
            status("Showing Top 20 by score (requires sortByScoreDescending).");
        });
 
        sortNameBtn.addActionListener(e -> {
            if (allEntries.isEmpty()) { status("Load first."); return; }
 
            ArrayList<ScoreEntry> copy = new ArrayList<>(allEntries);
            LeaderboardAlgorithms.sortByUsernameAscending(copy);
            showRows(copy, 50);
            status("Showing first 50 sorted by username (requires sortByUsernameAscending).");
        });
 
        searchBtn.addActionListener(e -> {
            if (allEntries.isEmpty()) { status("Load first."); return; }
 
            String target = searchField.getText().trim();
            if (target.isEmpty()) { status("Enter a username."); return; }
 
            ArrayList<ScoreEntry> copy = new ArrayList<>(allEntries);
 
            // Ensure sorted before binary search
            LeaderboardAlgorithms.sortByUsernameAscending(copy);
 
            int idx = LeaderboardAlgorithms.binarySearchByUsername(copy, target);
 
            if (idx >= 0) {
                tableModel.setData(List.of(copy.get(idx)));
                status("Found user: " + target + " (binary search index " + idx + ")");
            } else {
                tableModel.setData(List.of());
                status("Not found: " + target + " (binary search returned -1)");
            }
        });
 
        // Score binary search:
        // 1. Sort the list by score descending (required before binary search)
        // 2. Run binarySearchByScore with the user-entered value
        // 3. Display the matching row, or report not found
        scoreSearchBtn.addActionListener(e -> {
            if (allEntries.isEmpty()) { status("Load first."); return; }
 
            String input = scoreSearchField.getText().trim();
            if (input.isEmpty()) { status("Enter a score to search for."); return; }
 
            int targetScore;
            try {
                targetScore = Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                status("Invalid score — please enter a whole number.");
                return;
            }
 
            // Step 1: sort by score descending — binary search requires sorted data
            ArrayList<ScoreEntry> sorted = new ArrayList<>(allEntries);
            LeaderboardAlgorithms.sortByScoreDescending(sorted);
 
            // Step 2: binary search for the target score
            int idx = LeaderboardAlgorithms.binarySearchByScore(sorted, targetScore);
 
            // Step 3: show result
            if (idx >= 0) {
                ScoreEntry found = sorted.get(idx);
                tableModel.setData(List.of(found));
                status("Score " + targetScore + " found at position " + (idx + 1)
                        + "  —  player: " + found.getUsername());
            } else {
                tableModel.setData(List.of());
                status("Score " + targetScore + " not found.");
            }
        });
 
        backBtn.addActionListener(e -> router.goToMenu());
    }
 
    private void doLoad() {
        try {
            allEntries = LeaderboardRepository.loadFromResource("leaderboard.csv");
            status("Loaded " + allEntries.size() + " entries from leaderboard.csv");
            // Show a preview
            showRows(allEntries, 10000);
        } catch (Exception ex) {
            ex.printStackTrace();
            status("ERROR loading leaderboard.csv: " + ex.getMessage());
        }
    }
 
    private void showRows(ArrayList<ScoreEntry> list, int max) {
        int n = Math.min(max, list.size());
        ArrayList<ScoreEntry> subset = new ArrayList<>(list.subList(0, n));
        tableModel.setData(subset);
    }
 
    private void status(String msg) {
        statusLabel.setText(msg);
    }
}
 