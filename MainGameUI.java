package Trio;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.*;
import javax.swing.border.TitledBorder;

import static com.sun.java.swing.ui.CommonUI.createButton;

public class MainGameUI extends JFrame {

    // -- Useful screen objects --
    private final JPanel mainPanel;
    private final CardLayout cardLayout;

    // -- Ecran de jeu --
    private JPanel gamePanel;

    // -- Ecran de prompt de redémarrage du jeu --
    private JPanel restartPromptPanel;


    private final Game game;
    private final List<Student> players;

    //private boolean restartGame = true;

    public MainGameUI(Game game, List<Student> players) {
        this.game = game;
        this.players = players;

        setTitle("Plateau de Jeu - Trio");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        initComponents();

        // Start the gameloop
        cardLayout.show(mainPanel, "GAME_PANEL");

    }

    private void initComponents() {

        createGamePanel();
        mainPanel.add(gamePanel, "GAME_PANEL");

        createRestartPromptPanel();
        mainPanel.add(restartPromptPanel, "RESTART_PROMPT_PANEL");

        add(mainPanel);
        cardLayout.show(mainPanel, "GAME_PANEL");
    }

    private void createGamePanel() {

        gamePanel = new JPanel(new GridBagLayout());

        gamePanel.setLayout(new BorderLayout(10, 10));

        JPanel playersPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        playersPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (Student player : players) {
            playersPanel.add(createPlayerPanel(player));
        }
        gamePanel.add(playersPanel, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Zone de Jeu"));
        JLabel centerPileLabel = new JLabel("Pioche centrale : " + game.getCenterPile().size() + " cartes", SwingConstants.CENTER);
        centerPileLabel.setFont(new Font("Arial", Font.BOLD, 16));
        centerPanel.add(centerPileLabel, BorderLayout.NORTH);

        gamePanel.add(centerPanel, BorderLayout.SOUTH);

    }


    private JPanel createPlayerPanel(Student player) {
        JPanel panel = new JPanel(new BorderLayout());
        
        String title = player.getPseudo() + " (" + player.getTeam().getPseudo() + ")";
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), title, TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14)
        ));

        JPanel cardsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        
        for (Subject subject : player.getSubjects()) {
            cardsPanel.add(createSubjectCard(subject));
        }

        JScrollPane scrollPane = new JScrollPane(cardsPanel);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createSubjectCard(Subject subject) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        card.setBackground(new Color(240, 240, 240));
        card.setPreferredSize(new Dimension(100, 60));

        JLabel idLabel = new JLabel(subject.getId());
        idLabel.setFont(new Font("Arial", Font.BOLD, 14));
        idLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel creditLabel = new JLabel("Coeff: " + subject.getCredit());
        creditLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        creditLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(idLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(creditLabel);
        
        return card;
    }

    private void createRestartPromptPanel() {
        restartPromptPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = createGbc();
        addTitle(restartPromptPanel, "Souhaitez-vous refaire une partie?", gbc);

        JButton yesButton = createButton("OUI");
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        restartPromptPanel.add(yesButton, gbc);
        JButton noButton = createButton("NON");
        gbc.gridx = 1; gbc.gridy = 1;
        restartPromptPanel.add(noButton, gbc);


        yesButton.addActionListener(e->{
            //restartGame = true;
            cardLayout.show(mainPanel, "GAME_PANEL");
        });
        noButton.addActionListener(e->{
            //restartGame = false;
            this.dispose();
        });
    }



    private void gameLoop() {
        // Initialize the game loop
        GameLoop gameLoop = new GameLoop(game, players, new Scanner(System.in));

        // Main game loop
        while (!gameLoop.isGameOver()) {
            // Display the current player's turn
            Student currentPlayer = players.get(gameLoop.getCurrentPlayerIndex());
            updateTurnDisplay(currentPlayer);

            // Display the turn menu
            displayTurnMenu(currentPlayer, gameLoop);


            // Wait for the player to take an action
            waitForPlayerAction(gameLoop);

            // Check if the game is over
            if (gameLoop.isGameOver()) {
                displayGameEnd();
                break;
            }

            // Move to the next turn
            gameLoop.nextTurn();
        }
    }

    private void updateTurnDisplay(Student currentPlayer) {
        // Update the GUI to show the current player's turn
        JLabel turnLabel = new JLabel("Tour de: " + currentPlayer.getPseudo());
        turnLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gamePanel.add(turnLabel, BorderLayout.NORTH);
    }

    private void displayTurnMenu(Student currentPlayer, GameLoop gameLoop) {
        // Create a panel for the turn menu
        JPanel turnMenuPanel = new JPanel(new GridLayout(0, 1));

        JButton viewHandButton = new JButton("Voir ma main");
        viewHandButton.addActionListener(e -> displayHand(currentPlayer));
        turnMenuPanel.add(viewHandButton);

        JButton drawCardButton = new JButton("Tirer une carte de la pioche centrale");
        drawCardButton.addActionListener(e -> drawFromCenterPile(currentPlayer, gameLoop));
        turnMenuPanel.add(drawCardButton);

        JButton validateTriosButton = new JButton("Voir mes trios validés");
        validateTriosButton.addActionListener(e -> displayValidatedTrios(currentPlayer));
        turnMenuPanel.add(validateTriosButton);

        JButton endTurnButton = new JButton("Terminer mon tour");
        endTurnButton.addActionListener(e -> gameLoop.endTurn());
        turnMenuPanel.add(endTurnButton);

        gamePanel.add(turnMenuPanel, BorderLayout.CENTER);
    }

    private void waitForPlayerAction(GameLoop gameLoop) {
        // Wait for the player to take an action
        while (!gameLoop.isTurnEnded()) {
            try {
                Thread.sleep(100); // Polling interval
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void displayGameEnd() {
        // Display the game end screen
        JOptionPane.showMessageDialog(this, "La partie est terminée!", "Fin de la partie", JOptionPane.INFORMATION_MESSAGE);
    }

    private GridBagConstraints createGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        return gbc;
    }

    private void addTitle(JPanel panel, String title, GridBagConstraints gbc) {
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(titleLabel, gbc);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 18));
        button.setPreferredSize(new Dimension(150, 50));
        return button;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    private void displayHand(Student player) {
        // Display the player's hand in a dialog
        JPanel handPanel = new JPanel(new GridLayout(0, 1));
        for (Subject subject : player.getSubjects()) {
            JLabel cardLabel = new JLabel(subject.getId() + " (Coefficient: " + subject.getCredit() + ")");
            handPanel.add(cardLabel);
        }
        JOptionPane.showMessageDialog(this, handPanel, "Votre Main", JOptionPane.INFORMATION_MESSAGE);
    }

    private void drawFromCenterPile(Student player, GameLoop gameLoop) {
        // Display the center pile and allow the player to draw a card
        List<Subject> centerPile = game.getCenterPile();
        if (centerPile.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La pioche centrale est vide !", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPanel centerPanel = new JPanel(new GridLayout(0, 1));
        ButtonGroup buttonGroup = new ButtonGroup();
        JRadioButton[] cardButtons = new JRadioButton[centerPile.size()];

        for (int i = 0; i < centerPile.size(); i++) {
            Subject subject = centerPile.get(i);
            cardButtons[i] = new JRadioButton("Carte n°" + (i + 1));
            buttonGroup.add(cardButtons[i]);
            centerPanel.add(cardButtons[i]);
        }

        int result = JOptionPane.showConfirmDialog(this, centerPanel, "Choisissez une carte à tirer", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            for (int i = 0; i < cardButtons.length; i++) {
                if (cardButtons[i].isSelected()) {
                    Subject drawnCard = centerPile.remove(i);
                    player.addSubjectToHand(drawnCard);
                    JOptionPane.showMessageDialog(this, "Vous avez tiré: " + drawnCard.getId() + " (Coefficient: " + drawnCard.getCredit() + ")", "Carte Tirée", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            }
        }
    }

    private void displayValidatedTrios(Student player) {
        // Display the player's validated trios in a dialog
        JPanel triosPanel = new JPanel(new GridLayout(0, 1));
        List<Subject> validatedSubjects = player.getValidatedSubjects();
        if (validatedSubjects.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vous n'avez pas encore validé de trio.", "Trios Validés", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (Subject subject : validatedSubjects) {
            JLabel trioLabel = new JLabel(subject.getId() + " (Coefficient: " + subject.getCredit() + ")");
            triosPanel.add(trioLabel);
        }
        JOptionPane.showMessageDialog(this, triosPanel, "Trios Validés", JOptionPane.INFORMATION_MESSAGE);
    }
}
