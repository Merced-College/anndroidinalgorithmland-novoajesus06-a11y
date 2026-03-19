package level1;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MainMenuPanel extends JPanel {

    public MainMenuPanel(AppRouter router) {
        setPreferredSize(new Dimension(900, 540));
        setLayout(new BorderLayout());

        BufferedImage splashImg = Assets.loadImage("splash.png");

        JLayeredPane layered = new JLayeredPane();
        layered.setPreferredSize(new Dimension(900, 540));
        add(layered, BorderLayout.CENTER);

        // 1) Splash background (scaled to fill)
        SplashPanel splashPanel = new SplashPanel(splashImg);
        splashPanel.setBounds(0, 0, 900, 540);
        layered.add(splashPanel, Integer.valueOf(0));

        // 2) kat3.gif on top (encapsulated)
        RoundedImageComponent gifComp = createKatGif(250, 90);
        gifComp.setSize(gifComp.getPreferredSize());
        gifComp.setLocation(40, 540 - gifComp.getHeight());
        layered.add(gifComp, Integer.valueOf(100));

        // 3) Overlay panel for buttons (on top of splash)
        JPanel overlay = new JPanel();
        overlay.setOpaque(false);
        overlay.setLayout(new GridBagLayout());
        overlay.setBounds(0, 0, 900, 540);

        JButton startBtn = new JButton("Start Level 1: Find the White Rabbit");
        startBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        startBtn.addActionListener(e -> router.startRabbitLevel());

        JButton leaderboardBtn = new JButton("Leaderboard Terminal");
        leaderboardBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        leaderboardBtn.addActionListener(e -> router.goToLeaderboard());

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(0, 0, 0, 170));
        card.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));

        startBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        leaderboardBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(startBtn);
        card.add(Box.createVerticalStrut(10));
        card.add(leaderboardBtn);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.insets = new Insets(0, 300, 35, 0);
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        overlay.add(card, gbc);
        layered.add(overlay, Integer.valueOf(200));

        // Handle resize nicely
        layered.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int w = layered.getWidth();
                int h = layered.getHeight();
                splashPanel.setBounds(0, 0, w, h);
                overlay.setBounds(0, 0, w, h);

                // Keep gif near bottom-left; adjust last number to move up/down
                gifComp.setLocation(40, h - gifComp.getHeight() - 60);
            }
        });
    }

    /** Encapsulates loading, scaling, and rounding the kat gif. */
    private RoundedImageComponent createKatGif(int widthPx, int cornerArcPx) {
        ImageIcon gifIcon = new ImageIcon(Assets.class.getResource("/kat3.gif"));
        Image scaledGif = gifIcon.getImage().getScaledInstance(widthPx, -1, Image.SCALE_DEFAULT);
        return new RoundedImageComponent(new ImageIcon(scaledGif), cornerArcPx);
    }

    /** Splash background panel that scales image to fill */
    private static class SplashPanel extends JPanel {
        private final BufferedImage img;

        SplashPanel(BufferedImage img) {
            this.img = img;
            setOpaque(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(img, 0, 0, getWidth(), getHeight(), null);
        }
    }

    /**
     * Draws an ImageIcon clipped to rounded corners.
     * This hides white corner artifacts while keeping GIF animation.
     */
    private static class RoundedImageComponent extends JComponent {
        private final ImageIcon icon;
        private final int arc;

        RoundedImageComponent(ImageIcon icon, int arc) {
            this.icon = icon;
            this.arc = arc;
            setOpaque(false);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(icon.getIconWidth(), icon.getIconHeight());
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Shape clip = new java.awt.geom.RoundRectangle2D.Double(
                    0, 0, getWidth(), getHeight(),
                    arc, arc
            );
            g2.setClip(clip);

            icon.paintIcon(this, g2, 0, 0);
            g2.dispose();
        }
    }
}
