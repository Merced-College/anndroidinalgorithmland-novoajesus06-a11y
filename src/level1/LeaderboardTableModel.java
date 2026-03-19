package level1;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardTableModel extends AbstractTableModel {
    private final String[] cols = {"Rank", "Username", "Score", "Hits", "Misses", "When", "Level"};
    private List<ScoreEntry> data = new ArrayList<>();

    public void setData(List<ScoreEntry> newData) {
        this.data = newData;
        fireTableDataChanged();
    }

    @Override public int getRowCount() { return data.size(); }
    @Override public int getColumnCount() { return cols.length; }
    @Override public String getColumnName(int column) { return cols[column]; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ScoreEntry e = data.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> rowIndex + 1;
            case 1 -> e.getUsername();
            case 2 -> e.getScore();
            case 3 -> e.getHits();
            case 4 -> e.getMisses();
            case 5 -> e.getTimestampISO();
            case 6 -> e.getLevel();
            default -> "";
        };
    }
}
