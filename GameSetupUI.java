package Trio;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Importation des classes de votre logique de jeu
import Trio.Game;
import Trio.Student;
import Trio.Team;

public class GameSetupUI extends JFrame {

    // --- Panneaux de configuration ---
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private JPanel modeSelectionPanel;
    private JPanel difficultySelectionPanel;
    private JPanel playerCountPanel;
    private JPanel teamNamesPanel;
    private JPanel playerInfoPanel;

    // --- Composants pour récupérer les données ---
    private List<JTextField> teamNameFields;
    private List<JTextField> pseudoFields;
    private List<JComboBox<String>> specialtyCombos;

    // --- Données de configuration stockées ---
    private String gameMode;
    private String difficulty;
    private int playerCount;
    private List<String> teamNames;

    public GameSetupUI() {
        setTitle("Configuration de la partie de Trio");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        createModeSelectionPanel();
        mainPanel.add(modeSelectionPanel, "MODE_SELECTION");

        createDifficultySelectionPanel();
        mainPanel.add(difficultySelectionPanel, "DIFFICULTY_SELECTION");
        
        createPlayerCountPanel();
        mainPanel.add(playerCountPanel, "PLAYER_COUNT_SELECTION");

        add(mainPanel);
        cardLayout.show(mainPanel, "MODE_SELECTION");
    }

    private void createModeSelectionPanel() {
        modeSelectionPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = createGbc();
        addTitle(modeSelectionPanel, "Choisissez le mode de jeu", gbc);
        JButton soloButton = createButton("SOLO");
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        modeSelectionPanel.add(soloButton, gbc);
        JButton teamButton = createButton("ÉQUIPE");
        gbc.gridx = 1; gbc.gridy = 1;
        modeSelectionPanel.add(teamButton, gbc);
        
        ActionListener modeListener = e -> {
            this.gameMode = ((JButton) e.getSource()).getText();
            System.out.println("Mode choisi : " + this.gameMode);
            cardLayout.show(mainPanel, "DIFFICULTY_SELECTION");
        };
        
        soloButton.addActionListener(modeListener);
        teamButton.addActionListener(modeListener);
    }

    private void createDifficultySelectionPanel() {
        difficultySelectionPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = createGbc();
        addTitle(difficultySelectionPanel, "Choisissez la difficulté", gbc);
        JButton simpleButton = createButton("SIMPLE");
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        difficultySelectionPanel.add(simpleButton, gbc);
        JButton picanteButton = createButton("PICANTE");
        gbc.gridx = 1; gbc.gridy = 1;
        difficultySelectionPanel.add(picanteButton, gbc);
        
        ActionListener difficultyListener = e -> {
            this.difficulty = ((JButton) e.getSource()).getText();
            System.out.println("Difficulté choisie : " + this.difficulty);
            cardLayout.show(mainPanel, "PLAYER_COUNT_SELECTION");
        };
        
        simpleButton.addActionListener(difficultyListener);
        picanteButton.addActionListener(difficultyListener);
    }
    
    private void createPlayerCountPanel() {
        playerCountPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = createGbc();
        addTitle(playerCountPanel, "Nombre de joueurs", gbc);
        
        JPanel spinnerPanel = new JPanel();
        spinnerPanel.add(new JLabel("Joueurs (3-6) :"));
        SpinnerModel spinnerModel = new SpinnerNumberModel(4, 3, 6, 1);
        JSpinner playerCountSpinner = new JSpinner(spinnerModel);
        ((JSpinner.DefaultEditor) playerCountSpinner.getEditor()).getTextField().setColumns(3);
        spinnerPanel.add(playerCountSpinner);
        
        gbc.gridy = 1;
        playerCountPanel.add(spinnerPanel, gbc);
        
        JButton validateButton = new JButton("Valider");
        gbc.gridy = 2; gbc.insets = new Insets(20, 10, 10, 10);
        playerCountPanel.add(validateButton, gbc);

        validateButton.addActionListener(e -> {
            this.playerCount = (Integer) playerCountSpinner.getValue();
            
            if (gameMode.equals("ÉQUIPE")) {
                if (playerCount % 2 != 0) {
                    showError("Le mode ÉQUIPE requiert un nombre de joueurs pair.");
                    return;
                }
                createTeamNamesPanel();
                mainPanel.add(teamNamesPanel, "TEAM_NAMES");
                cardLayout.show(mainPanel, "TEAM_NAMES");
            } else {
                createPlayerInfoPanel();
                mainPanel.add(playerInfoPanel, "PLAYER_INFO");
                cardLayout.show(mainPanel, "PLAYER_INFO");
            }
        });
    }

    private void createTeamNamesPanel() {
        teamNamesPanel = new JPanel(new BorderLayout(10, 10));
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = createGbc();
        addTitle(formPanel, "Noms des équipes", gbc);

        int teamCount = playerCount / 2;
        teamNameFields = new ArrayList<>();
        
        gbc.gridwidth = 1;
        for (int i = 0; i < teamCount; i++) {
            gbc.gridy = i + 1;
            gbc.gridx = 0; gbc.anchor = GridBagConstraints.EAST;
            formPanel.add(new JLabel("Équipe " + (i + 1) + " :"), gbc);
            
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            JTextField teamNameField = new JTextField(15);
            teamNameFields.add(teamNameField);
            formPanel.add(teamNameField, gbc);
        }

        JButton validateButton = new JButton("Valider les équipes");
        validateButton.addActionListener(e -> {
            teamNames = new ArrayList<>();
            for (JTextField field : teamNameFields) {
                if (field.getText().trim().isEmpty()) {
                    showError("Tous les noms d'équipe doivent être remplis.");
                    return;
                }
                teamNames.add(field.getText().trim());
            }
            System.out.println("Noms d'équipes validés : " + teamNames);
            
            createPlayerInfoPanel();
            mainPanel.add(playerInfoPanel, "PLAYER_INFO");
            cardLayout.show(mainPanel, "PLAYER_INFO");
        });
        
        teamNamesPanel.add(new JScrollPane(formPanel), BorderLayout.CENTER);
        teamNamesPanel.add(validateButton, BorderLayout.SOUTH);
    }

    private void createPlayerInfoPanel() {
        playerInfoPanel = new JPanel(new BorderLayout(10, 10));
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = createGbc();
        addTitle(formPanel, "Informations des joueurs", gbc);

        pseudoFields = new ArrayList<>();
        specialtyCombos = new ArrayList<>();

        String[] specialties = {"Informatique", "Mécanique et ergonomie", "Génie industriel", "Énergie et Génie électrique"};

        if (gameMode.equals("ÉQUIPE")) {
            int playerIndex = 0;
            for (int i = 0; i < teamNames.size(); i++) {
                gbc.gridy++;
                gbc.gridx = 0; gbc.gridwidth = 5;
                gbc.insets = new Insets(15, 5, 5, 5);
                JLabel teamTitle = new JLabel("--- Équipe : " + teamNames.get(i) + " ---");
                teamTitle.setFont(new Font("Arial", Font.BOLD, 16));
                formPanel.add(teamTitle, gbc);
                gbc.insets = new Insets(5, 5, 5, 5);

                for (int j = 0; j < 2; j++) {
                    addPlayerFields(formPanel, gbc, playerIndex, specialties);
                    playerIndex++;
                }
            }
        } else {
            for (int i = 0; i < playerCount; i++) {
                addPlayerFields(formPanel, gbc, i, specialties);
            }
        }

        JButton launchButton = new JButton("Lancer la partie !");
        launchButton.addActionListener(e -> launchGame());

        playerInfoPanel.add(new JScrollPane(formPanel), BorderLayout.CENTER);
        playerInfoPanel.add(launchButton, BorderLayout.SOUTH);
    }

    private void addPlayerFields(JPanel panel, GridBagConstraints gbc, int playerIndex, String[] specialties) {
        gbc.gridy++;
        gbc.gridwidth = 1;
        
        gbc.gridx = 0; gbc.anchor = GridBagConstraints.CENTER;
        panel.add(new JLabel("Joueur " + (playerIndex + 1)), gbc);

        gbc.gridx = 1; gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Pseudo:"), gbc);
        gbc.gridx = 2; gbc.anchor = GridBagConstraints.WEST;
        JTextField pseudoField = new JTextField(12);
        panel.add(pseudoField, gbc);
        pseudoFields.add(pseudoField);

        gbc.gridx = 3; gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Spécialité:"), gbc);
        gbc.gridx = 4; gbc.anchor = GridBagConstraints.WEST;
        JComboBox<String> specialtyCombo = new JComboBox<>(specialties);
        panel.add(specialtyCombo, gbc);
        specialtyCombos.add(specialtyCombo);
    }
    
    private void launchGame() {
        List<Student> players = new ArrayList<>();
        Map<String, Team> teamsMap = new HashMap<>();

        if (gameMode.equals("ÉQUIPE")) {
            for (String name : teamNames) {
                teamsMap.put(name, new Team(name, "Couleur pour " + name)); // Couleur à définir
            }
        }

        for (int i = 0; i < playerCount; i++) {
            String pseudo = pseudoFields.get(i).getText();
            String specialty = (String) specialtyCombos.get(i).getSelectedItem();
            if (pseudo.trim().isEmpty()) {
                showError("Le pseudo du joueur " + (i + 1) + " ne peut pas être vide.");
                return;
            }

            Team playerTeam;
            if (gameMode.equals("ÉQUIPE")) {
                playerTeam = teamsMap.get(teamNames.get(i / 2));
            } else {
                playerTeam = new Team("Solo " + (i + 1), "Couleur " + (i + 1));
            }
            
            players.add(new Student(i + 1, pseudo, specialty, playerTeam));
        }

        String finalGameMode = gameMode.equals("ÉQUIPE") ? "TEAM" : "SOLO";
        Game game = new Game(finalGameMode, this.difficulty.toUpperCase());
        game.shuffleAndDeal(players);

        SwingUtilities.invokeLater(() -> {
            MainGameUI mainGameUI = new MainGameUI(game, players);
            mainGameUI.setVisible(true);
        });
        
        this.dispose();
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameSetupUI frame = new GameSetupUI();
            frame.setVisible(true);
        });
    }
}
