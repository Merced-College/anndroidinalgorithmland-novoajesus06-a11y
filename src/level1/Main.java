//Jesus Novoa Vasquez Ryan Weber
//3.17.26
//main file
package level1;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Ann Droid in Algorithm Land - Desktop Edition");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            AppRouter router = new AppRouter();
            frame.setContentPane(router.getRoot());

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}