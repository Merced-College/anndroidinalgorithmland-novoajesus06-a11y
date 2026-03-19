package level1;
 
import javax.swing.*;
import java.awt.*;
 
public class AppRouter {
    private final JPanel root;
    private final CardLayout cards;
 
    private final MainMenuPanel menu;
    private final RabbitGamePanel rabbit;
    private final LeaderboardPanel leaderboard;
 
    public static final String SCREEN_MENU = "menu";
    public static final String SCREEN_RABBIT = "rabbit";
    public static final String SCREEN_LEADERBOARD = "leaderboard";
 
    public AppRouter() {
        this.cards = new CardLayout();
        this.root = new JPanel(cards);
 
        this.menu = new MainMenuPanel(this);
        this.rabbit = new RabbitGamePanel(this);
        this.leaderboard = new LeaderboardPanel(this);
 
        root.add(menu, SCREEN_MENU);
        root.add(rabbit, SCREEN_RABBIT);
        root.add(leaderboard, SCREEN_LEADERBOARD);
 
        goToMenu(); // starts music + shows menu
    }
 
    public JPanel getRoot() { return root; }
 
    // Keep show() dumb: ONLY switches cards
    public void show(String screen) {
        cards.show(root, screen);
    }
 
    /** Menu screen: start menu music and stop ticking. */
    public void goToMenu() {
        Assets.stopLoopingSfxWav();
        // Start menu music off the UI thread (avoid UI stutter)
        new Thread(() -> Assets.playLoopingWav("Kid_Charlemagne.wav"), "menu-music").start();
        show(SCREEN_MENU);
    }
 
    /** Leaderboard screen: keep menu music, stop ticking. */
    public void goToLeaderboard() {
        Assets.stopLoopingSfxWav();
        show(SCREEN_LEADERBOARD);
    }
 
    /** Start game: stop menu music, start game, show screen. */
    public void startRabbitLevel() {
        Assets.stopLoopingWav();     // stop menu music quickly
        rabbit.startRoundFromMenu();
        show(SCREEN_RABBIT);
    }
}
 