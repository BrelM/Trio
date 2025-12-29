package Trio;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;
import javax.swing.border.TitledBorder;

import Trio.Game;
import Trio.Student;
import Trio.Team;
import Trio.CompetencePackage.Competence;
import java.util.*;

public class GameloopUI extends JFrame {

    private Game game;
    private List<Student> players;
    private int currentPlayerIndex;

    private JLabel turnLabel;
    private JButton playButton;
    private JButton drawButton;

    public GameloopUI(Game game, List<Student> players) {
        this.game = game;
        this.players = players;
        this.currentPlayerIndex = 0;

        setTitle("Trio - Game Loop");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        turnLabel = new JLabel("Tour de: " + getCurrentPlayer().getPseudo(), SwingConstants.CENTER);
        turnLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(turnLabel, BorderLayout.NORTH);

        JPanel actionPanel = new JPanel();
        playButton = new JButton("Jouer");
        drawButton = new JButton("Piocher");

        actionPanel.add(playButton);
        actionPanel.add(drawButton);
        add(actionPanel, BorderLayout.CENTER);

        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handlePlayAction();
            }
        });

        drawButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleDrawAction();
            }
        });
    }

    private Student getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    private void handlePlayAction() {
        // Logic for playing a card
        JOptionPane.showMessageDialog(this, getCurrentPlayer().getPseudo() + " joue une carte.");
        nextTurn();
    }

    private void handleDrawAction() {
        // Logic for drawing a card
        JOptionPane.showMessageDialog(this, getCurrentPlayer().getPseudo() + " pioche une carte.");
        nextTurn();
    }

    private void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        turnLabel.setText("Tour de: " + getCurrentPlayer().getPseudo());
    }
}
