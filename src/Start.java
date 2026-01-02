package Trio.src;

import Trio.src.view.GameSetupUI;

import javax.swing.SwingUtilities;

/**
 * Entry point for the Trio game.
 * Launches the setup UI to configure and start a new game.
 */
public class Start {

    public static void main(String[] args) {
        // Ensure GUI initialization happens on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            GameSetupUI setupUI = new GameSetupUI();
            setupUI.setVisible(true);
        });
    }
}
