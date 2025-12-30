package Trio;

import Trio.controller.GameController;
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
            // Create the main game view
            SwingGameView view = new SwingGameView(controller);

            // Link view and controller
            view.setController(controller);

            // Create a JFrame to hold the game view
            JFrame gameFrame = new JFrame("Trio Game");
            gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            gameFrame.setSize(1000, 600); // adjust as needed
            gameFrame.setLocationRelativeTo(null);
            gameFrame.setLayout(new BorderLayout());

            gameFrame.add(view, BorderLayout.CENTER);

            // Show the frame
            gameFrame.setVisible(true);

            // Shuffle and deal cards to players
            model.shuffleAndDeal(players);

        });

    }
}
