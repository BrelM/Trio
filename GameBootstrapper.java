package Trio;

import Trio.model.Game;
import Trio.controller.GameController;
import Trio.model.classes.Student;
import Trio.view.swing.SwingGameView;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GameBootstrapper {

    /**
     * Launch the main game window after setup.
     * @param mode "SOLO" or "TEAM"
     * @param difficulty "SIMPLE" or "PICANTE"
     * @param players list of initialized Student objects
     */
    public static void launchGame(String mode, String difficulty, List<Student> players) {
        // Create the game model
        Game model = new Game(mode, difficulty);

        // Create the controller
        GameController controller = new GameController(model, players);

        SwingUtilities.invokeLater(() -> {

            // Create a JFrame to hold the game view
            JFrame gameFrame = new JFrame("Trio Game");
            gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            gameFrame.setSize(1000, 600); // adjust as needed
            gameFrame.setLocationRelativeTo(null);
            gameFrame.setLayout(new BorderLayout());

            // Create the main game view
            SwingGameView view = new SwingGameView(controller, gameFrame::dispose);

            gameFrame.add(view, BorderLayout.CENTER);

            // Show the frame
            gameFrame.setVisible(true);

        });

    }
}
