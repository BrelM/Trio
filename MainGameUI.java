package Trio;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
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

        // Present the main screen with three main sections in a horizontal layout
        //1. First section in the leftmost part of the screen contains buttons corresponding to teams/players
        //      Clicking on one of them reveals the validated competences
        // 2. Second section in the middle changes according to the instant. It could display the turn menu,
        //      it could display the options relatives to a selected option of the turn menu.
        //      In case, the player wanted to draw a card from the center pile, it should display the available
        //      cards as buttons.
        // 3. Third section in the rightmost part of the screen represent the log of the turn. It contains only
        //      the informations the current player is allowed to see (the card he drew during the turn,
        //      the announcements). It is cleared then updated everytime a player takes a turn.
        // Everytime a card needs to be represented, a simple button might be enough. Use the createButton method.

        // This method uses GameLoop class' play and playTurn methods.
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

}
