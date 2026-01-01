package Trio.view.swing;

import Trio.competence.Competence;
import Trio.model.classes.Student;
import Trio.model.classes.Subject;
import Trio.controller.GameController;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.NoSuchElementException;

public class SwingGameView extends JPanel {

        private GameController controller;

    // Main sections
    private JPanel titlePanel;    // Player's name and team
    private JPanel leftPanel;    // Players / Teams
    private JPanel centerPanel;  // Turn playground area
    private JPanel upperPanel;   // Player's cards
    private JPanel lowerPanel;   // Turn menu
    private JPanel rightPanel;   // Turn log

    private JTextArea turnLog;
    private Runnable closer;

    /** Create a JButton for an or action */
    public JButton createButton(String label, ActionListener action) {
        JButton button = new JButton(label);
        button.addActionListener(action);
        return button;
    }

    /** Create a JButton for a card */
    public JButton createCardButton(String label) {
        JButton button = new JButton(label);
        button.setPreferredSize(new Dimension(100, 120));

        return button;
    }


    /** Populate upper panel with player's cards */
    private void populatePlayerCards() {
        upperPanel.removeAll();

        for (Subject s : controller.getCurrentPlayer().getSubjects()) {
            JButton btn = createCardButton(s.getId() + " (" + s.getCredit() + ")");
            upperPanel.add(btn);
        }

        revalidate();
        repaint();
    }

    /** Populate left panel with team/player buttons */
    private void populatePlayers() {
        leftPanel.removeAll();
        for (Student s : controller.getAllPlayers()) {
            final StringBuilder name = new StringBuilder();
            if(s.equals(controller.getCurrentPlayer()))
                name.append("<< You >>");
            else
                name.append(s.getPseudo()).append(" - ").append(controller.getCurrentPlayer().getTeam().getPseudo());

            JButton btn = createButton(name.toString(), e -> showValidatedCompetences(s));
            leftPanel.add(btn);
        }
        revalidate();
        repaint();
    }

    /** Display validated competences for a player in left panel */
    private void showValidatedCompetences(Student player) {
        StringBuilder label = new StringBuilder();
        if(controller.getValidatedCompetencesForPlayer(player).isEmpty())
            label.append(player.getPseudo()).append(" hasn't validated any competence");
        else {
            label.append(player.getPseudo()).append("'s validated competences:");
            for(Competence c : controller.getValidatedCompetencesForPlayer(player))
                label.append("\n").append(c.getName());

        }
        JOptionPane.showMessageDialog(this, label);

    }

    /** Display turn menu in the center panel */
    public void showTurnMenu() {
        titlePanel.removeAll();
        lowerPanel.removeAll();

        JLabel playersLabel = new JLabel(controller.getCurrentPlayer().getPseudo() + " - " + controller.getCurrentPlayer().getTeam().getPseudo());
        titlePanel.add(playersLabel);

        JButton drawCenterBtn = createButton("Draw from center", e -> showCenterCards());
        JButton drawMinBtn = createButton("Draw min credit subject from player", e -> showDrawFromPlayerMenu(false));
        JButton drawMaxBtn = createButton("Draw max credit subject from player", e -> showDrawFromPlayerMenu(true));
        JButton myCardsBtn = createButton("My cards", e -> { populatePlayerCards(); });
        JButton endTurnBtn = createButton("End Turn", e -> { controller.endTurn(); showPreTurnMenu(); });

        lowerPanel.add(drawCenterBtn);
        lowerPanel.add(drawMinBtn);
        lowerPanel.add(drawMaxBtn);
        lowerPanel.add(myCardsBtn);
        lowerPanel.add(endTurnBtn);

        revalidate();
        repaint();
    }

    /** Show a blank screen for before next turn */
    private void showPreTurnMenu() {
        upperPanel.removeAll();
        lowerPanel.removeAll();
        leftPanel.repaint();
        rightPanel.repaint();

        if(controller.isBadDraw())
            JOptionPane.showMessageDialog(
                    this,
                    "You have drawn subjects from different competences."
            );

        JOptionPane.showMessageDialog(
                this,
                controller.getCurrentPlayer().getPseudo() + "'s turn will now begin."
        );

        populatePlayerCards();
        populatePlayers();
        populateTurnLog();
        updateTurnLog();
        showTurnMenu();


    }


    /** Show available cards from center as buttons */
    private void showCenterCards() {
        upperPanel.removeAll();
        List<Subject> centerPile = controller.getGame().getCenterPile();

        for (int i = 0; i < centerPile.size(); i++) {
            Subject s = centerPile.get(i);
            int finalI = i;
            JButton btn = createButton("Card " + finalI, e -> {
                controller.drawFromCenter(finalI);
                if (controller.isTurnOver()) {
                    controller.endTurn();
                    showPreTurnMenu();
                } else if (controller.isGameOver()) {
                    JOptionPane.showMessageDialog(this, "Team " + controller.getWinner().getPseudo() + " has won!");

                    /** TODO : Follow up action at the end of the game (restart or quit) */
                    int choice = JOptionPane.showConfirmDialog(this, "Restart the game?");
                    if(choice == 0) reinit();
                    else this.closer.run();

                } else {
                    showTurnMenu();
                    populatePlayerCards();
                    updateTurnLog();
                }
            });
            upperPanel.add(btn);
        }

        revalidate();
        repaint();
    }

    /** Display menu of players in order to draw min/max from a selected player*/
    private void showDrawFromPlayerMenu(boolean pickMax) {
        upperPanel.removeAll();
        for (Student s : controller.getAllPlayers()) {
            final StringBuilder name = new StringBuilder();
            if(s.equals(controller.getCurrentPlayer()))
                name.append("Vous");
            else
                name.append(s.getPseudo());

            JButton btn = createButton(name.toString(), e -> {
                controller.drawFromPlayerHand(s, pickMax);
                if (controller.isTurnOver()){
                    controller.endTurn();
                    showPreTurnMenu();
                }
                else {
                    showTurnMenu();
                    populatePlayerCards();
                    updateTurnLog();
                }
            });
            upperPanel.add(btn);
        }

        revalidate();
        repaint();
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
    private void populateTurnLog() {
        turnLog.setText("");

        Student current = controller.getCurrentPlayer();
        turnLog.append("Current player: " + current.getPseudo() + "\n\n");

        turnLog.append("Previous turns announcements:\n");
        if (controller.getTurnAnnouncements().isEmpty())
            turnLog.append("No announcements.");
        else
            for (String a : controller.getTurnAnnouncements()) {
                turnLog.append(a + "\n");
            }

        turnLog. append("\n\n");
    }

    /** Update right panel turn log */
    private void updateTurnLog() {
        try {
            Subject s = controller.getTurnBufferSubjects().getLast();
            turnLog.append("Drawn: " + s.getId() + " (" + s.getCredit() + ")\n");
        }
        catch (NoSuchElementException _) {}
    }

    /** Setter */
    public void setController(GameController controller) {
        this.controller = controller;
    }

    /** Add visible divisions between panels */
    private void addPanelBorders() {
        leftPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        upperPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        lowerPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        centerPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        rightPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    /** Constructor update to set preferred sizes for panels */
    public SwingGameView(GameController controller, Runnable windowsCloser) {
        this.controller = controller;
        this.closer = windowsCloser;

        setLayout(new BorderLayout());

        titlePanel = new JPanel();
        leftPanel = new JPanel();
        centerPanel = new JPanel();
        upperPanel = new JPanel();
        lowerPanel = new JPanel();
        rightPanel = new JPanel();
        turnLog = new JTextArea();

        // Set preferred sizes for panels
        titlePanel.setPreferredSize(new Dimension(600, 70));
        upperPanel.setPreferredSize(new Dimension(600, 400));
        lowerPanel.setPreferredSize(new Dimension(600, 200));
        leftPanel.setPreferredSize(new Dimension(200, 0)); // Fixed width for left panel
        rightPanel.setPreferredSize(new Dimension(300, 0)); // Fixed width for right panel

        add(leftPanel, BorderLayout.WEST);

        centerPanel.add(titlePanel, BorderLayout.NORTH);
        centerPanel.add(upperPanel, BorderLayout.CENTER);
        centerPanel.add(lowerPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);

        rightPanel.add(turnLog);
        add(rightPanel, BorderLayout.EAST);

        addPanelBorders(); // Add borders to panels

        showPreTurnMenu();
        //populatePlayers();
    }

    /** Reinitialize the game */
    private void reinit() {
        controller.reinit();

        titlePanel.removeAll();
        upperPanel.removeAll();
        lowerPanel.removeAll();
        leftPanel.removeAll();
        turnLog.setText("");

        showPreTurnMenu();
    }
}
