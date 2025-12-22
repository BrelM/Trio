package Trio;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class MainGameUI extends JFrame {

    private Game game;
    private List<Student> players;

    public MainGameUI(Game game, List<Student> players) {
        this.game = game;
        this.players = players;

        setTitle("Plateau de Jeu - Trio");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel playersPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        playersPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        for (Student player : players) {
            playersPanel.add(createPlayerPanel(player));
        }
        add(playersPanel, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Zone de Jeu"));
        JLabel centerPileLabel = new JLabel("Pioche centrale : " + game.getCenterPile().size() + " cartes", SwingConstants.CENTER);
        centerPileLabel.setFont(new Font("Arial", Font.BOLD, 16));
        centerPanel.add(centerPileLabel, BorderLayout.NORTH);
        
        add(centerPanel, BorderLayout.SOUTH);
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
}
