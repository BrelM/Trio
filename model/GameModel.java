package Trio.model;

import Trio.Game;
import Trio.Student;
import Trio.Subject;

import java.util.List;

public class GameModel {

    private final Game game;
    private final List<Student> players;
    private int currentPlayerIndex = 0;

    public GameModel(Game game, List<Student> players) {
        this.game = game;
        this.players = players;

        initGame();
    }

    // =========================
    // ===== INITIALIZATION ====
    // =========================

    private void initGame() {
        try {
            game.shuffleAndDeal(players);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la distribution", e);
        }
    }

    // =========================
    // ===== ACCESSORS =========
    // =========================

    public List<Student> getPlayers() {
        return players;
    }

    public Student getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public List<Subject> getCenterPile() {
        return game.getCenterPile();
    }

    // =========================
    // ===== TURN LOGIC ========
    // =========================

    public void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    // =========================
    // ===== GAME ACTIONS ======
    // =========================

    public void drawSubjectFromCenter(Subject subject) {
        Student player = getCurrentPlayer();

        if (!game.getCenterPile().contains(subject)) {
            throw new IllegalStateException("Carte inexistante dans la pile centrale");
        }

        game.getCenterPile().remove(subject);
        player.addSubjectToHand(subject);
    }
}
