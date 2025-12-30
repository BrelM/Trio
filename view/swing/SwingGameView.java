package Trio.view.swing;

import Trio.CompetencePackage.Competence;
import Trio.Student;
import Trio.Subject;
import Trio.controller.GameController;


import Trio.*;
import Trio.controller.GameController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class SwingGameView extends JPanel {

        private GameController controller;

    // Main sections
    private JPanel leftPanel;    // Players / Teams
    private JPanel centerPanel;  // Turn menu / Cards
    private JPanel rightPanel;   // Turn log

    private JTextArea turnLog;

    public SwingGameView(GameController controller) {
        this.controller = controller;
        setLayout(new BorderLayout());

        // Left panel: Players/Teams
        leftPanel = new JPanel(new BorderLayout());
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        add(leftPanel, BorderLayout.WEST);

        // Center panel: Turn menu / Card options
        centerPanel = new JPanel();
        centerPanel.setLayout(new FlowLayout());
        add(centerPanel, BorderLayout.CENTER);

        // Right panel: Turn log
        rightPanel = new JPanel(new BorderLayout());
        turnLog = new JTextArea(20, 25);
        turnLog.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(turnLog);
        rightPanel.add(scrollPane, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        populatePlayers();
        showTurnMenu();
    }


    /** Create a JButton for a card or action */
    public JButton createButton(String label, ActionListener action) {
        JButton button = new JButton(label);
        button.addActionListener(action);
        return button;
    }

    /** Populate left panel with team/player buttons */
    private void populatePlayers() {
        leftPanel.removeAll();
        for (Student s : controller.getCurrentPlayerTeamMates()) {
            JButton btn = createButton(s.getPseudo(), e -> showValidatedCompetences(s));
            leftPanel.add(btn);
        }
        revalidate();
        repaint();
    }

    /** Display validated competences for a player in left panel */
    private void showValidatedCompetences(Student player) {
        leftPanel.removeAll();
        JLabel label = new JLabel(player.getPseudo() + "'s validated competences:");
        leftPanel.add(label);
        for (Competence c : controller.getValidatedCompetencesForPlayer(player)) {
            leftPanel.add(new JLabel("• " + c.getName()));
        }
        revalidate();
        repaint();
    }

    /** Display turn menu in the center panel */
    public void showTurnMenu() {
        centerPanel.removeAll();

        JButton drawCenterBtn = createButton("Draw from center", e -> { showCenterCards(); revalidate(); repaint(); });
        JButton drawMinBtn = createButton("Draw min from player", e -> { drawFromPlayer(false); revalidate(); repaint(); });
        JButton drawMaxBtn = createButton("Draw max from player", e -> { drawFromPlayer(true); revalidate(); repaint(); });
        JButton endTurnBtn = createButton("End Turn", e -> { controller.endTurn(); revalidate(); repaint();} );

        centerPanel.add(drawCenterBtn);
        centerPanel.add(drawMinBtn);
        centerPanel.add(drawMaxBtn);
        centerPanel.add(endTurnBtn);

        revalidate();
        repaint();
    }

    /** Show available cards from center as buttons */
    private void showCenterCards() {
        centerPanel.removeAll();
        List<Subject> centerPile = controller.getGame().getCenterPile();

        for (int i = 0; i < centerPile.size(); i++) {
            Subject s = centerPile.get(i);
            int finalI = i;
            JButton btn = createButton("Card " + finalI, e -> { controller.drawFromCenter(finalI); revalidate(); repaint(); });
            centerPanel.add(btn);
        }

        revalidate();
        repaint();
    }

    /** Draw min/max from selected player (simplified: picks first teammate except current) */
    private void drawFromPlayer(boolean pickMax) {
        Student current = controller.getCurrentPlayer();
        controller.getCurrentPlayerTeamMates().stream()
                .filter(s -> !s.equals(current))
                .findFirst().ifPresent(target -> { controller.drawFromPlayerHand(target, pickMax); revalidate(); repaint(); });
    }

    /** Update center pile, player hands, and turn log */
    public void updateCenterPile() {
        // Could be extended to show center pile visually
    }

    public void updatePlayerHands() {
        // Could be extended to show current player's hand visually
        updateTurnLog();
    }

    /** Update right panel turn log */
    private void updateTurnLog() {
        turnLog.setText("");
        Student current = controller.getCurrentPlayer();
        turnLog.append("Current player: " + current.getPseudo() + "\n\n");

        for (Subject s : controller.getTurnBufferSubjects()) {
            turnLog.append("Drawn: " + s.getId() + " (" + s.getCredit() + ")\n");
        }
    }

    /** Setter */
    public void setController(GameController controller) {
        this.controller = controller;
    }
}
