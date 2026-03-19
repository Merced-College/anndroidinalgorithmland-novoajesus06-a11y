package level1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

public class RabbitGamePanel extends JPanel {
    private static final int WIDTH = 900;
    private static final int HEIGHT = 540;

    private static final int ROUND_SECONDS = 26;
    private static final int MOVE_EVERY_MS = 1000;

    private final AppRouter router;

    private final BufferedImage bg;
    private final BufferedImage rabbit;

    private final Random rng = new Random();

    private int rabbitX = 200;
    private int rabbitY = 200;

    private int hits = 0;
    private int misses = 0;
    private int secondsLeft = ROUND_SECONDS;

    private Timer moveTimer;
    private Timer countdownTimer;

    private Rectangle rabbitRect = new Rectangle(0, 0, 1, 1);

    // ✅ NEW: prevents game from running until Start is pressed
    private boolean roundRunning = false;

    public RabbitGamePanel(AppRouter router) {
        this.router = router;
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);

        bg = Assets.loadImage("leavesbg.png");
        rabbit = Assets.loadImage("rabbit.png");

        // place rabbit somewhere reasonable even before starting
        repositionRabbit();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
        });

        // ❌ DO NOT startRound() here
        // startRound();
    }

    /** Called by AppRouter when the user presses Start on the menu. */
    public void startRoundFromMenu() {
        requestFocusInWindow();
        startRound();
    }

    private void startRound() {
        // Prevent double-starts (ex: double-clicking Start)
        if (roundRunning) return;
        roundRunning = true;
        Assets.playLoopingSfxWav("tick.wav");

        hits = 0;
        misses = 0;
        secondsLeft = ROUND_SECONDS;

        repositionRabbit();

        stopTimersIfRunning();

        moveTimer = new Timer(MOVE_EVERY_MS, e -> {
            repositionRabbit();
            repaint();
        });

        countdownTimer = new Timer(1000, e -> {
            secondsLeft--;
            if (secondsLeft <= 0) {
                endRound();
            }
            repaint();
        });

        moveTimer.start();
        countdownTimer.start();

        repaint();
    }

    private void endRound() {
        roundRunning = false;
        stopTimersIfRunning();
        Assets.stopLoopingSfxWav();

        double total = hits + misses;
        double ratio = total == 0 ? 0.0 : (hits / total) * 100.0;

        String msg = String.format(
                "Time!%nHits: %d%nMisses: %d%nHit Ratio: %.1f%%",
                hits, misses, ratio
        );

        int choice = JOptionPane.showOptionDialog(
                this,
                msg,
                "Round Results",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{"Play Again", "Back to Menu"},
                "Play Again"
        );

        if (choice == 0) {
            // ✅ restart only if they choose Play Again
            startRound();
        } else {
            // ✅ go back to menu and DO NOT auto-start again
            //router.show(AppRouter.SCREEN_MENU);
        	router.goToMenu();
        }
    }

    private void stopTimersIfRunning() {
        if (moveTimer != null) {
            moveTimer.stop();
            moveTimer = null;
        }
        if (countdownTimer != null) {
            countdownTimer.stop();
            countdownTimer = null;
        }
    }

    private void repositionRabbit() {
        int w = rabbit.getWidth();
        int h = rabbit.getHeight();

        int maxX = Math.max(1, WIDTH - w - 10);
        int maxY = Math.max(1, HEIGHT - h - 10);

        rabbitX = 5 + rng.nextInt(maxX);
        rabbitY = 5 + rng.nextInt(maxY);

        rabbitRect = new Rectangle(rabbitX, rabbitY, w, h);
    }

    private void handleClick(int x, int y) {
        // ✅ ignore clicks until the round is running
        if (!roundRunning) return;

        if (rabbitRect.contains(x, y)) {
            hits++;
            Assets.playWav("rabbit.wav");
            repositionRabbit();
        } else {
            misses++;
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(bg, 0, 0, WIDTH, HEIGHT, null);

        // HUD
        g.setFont(new Font("SansSerif", Font.BOLD, 18));
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRoundRect(10, 10, 330, 90, 18, 18);
        g.setColor(Color.WHITE);

        g.drawString("Find the White Rabbit", 20, 38);
        g.setFont(new Font("SansSerif", Font.PLAIN, 16));

        if (roundRunning) {
            g.drawString("Time left: " + secondsLeft + "s", 20, 62);
            g.drawString("Hits: " + hits + "   Misses: " + misses, 20, 84);
        } else {
            g.drawString("Press Start on the menu to begin!", 20, 62);
        }

        g.drawImage(rabbit, rabbitX, rabbitY, null);
    }
}
