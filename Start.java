package Trio;

import javax.swing.*;

public class Start {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameSetupUI setupUI = new GameSetupUI();
            setupUI.setVisible(true);
        });
    }
}