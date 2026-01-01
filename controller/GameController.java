package Trio.controller;

import Trio.CompetencePackage.Competence;
import java.util.*;

import Trio.Game;
import Trio.Student;
import Trio.Subject;
import Trio.Team;

public class GameController {

    private Game game;
    private List<Student> players;
    private int currentPlayerIndex = 0;
    private List<String> turnAnnouncements = new ArrayList<>();
    private Map<Team, Integer> teamScores = new HashMap<>();

    // Turn buffering
    private static class TurnCard {
        Subject card;
        enum Origin { CENTER, PLAYER }
        Origin origin;
        int centerIndex = -1;
        Student sourcePlayer = null;
        int handIndex = -1;

        TurnCard(Subject card, Origin origin) { this.card = card; this.origin = origin; }
    }

    private List<TurnCard> turnBuffer = new ArrayList<>();
    private Integer bufferCredit = null;
    private boolean turnMustEnd = false;
    private Team winner = null;
    private boolean badDraw = false;

    public GameController(Game game, List<Student> players) {
        this.game = game;
        this.players = players;

        // Initialize team scores
        for (Student player : players) {
            teamScores.putIfAbsent(player.getTeam(), 0);
        }

        // Shuffle and distribute at game start
        game.shuffleAndDeal(players);
    }

    /** Reset the configuration e.g to start a new game */
    public void reinit() {

        // Emptying cards holders
        for(Student player : players) player.emptyHand();
        game.emptyCenterPile();

        // Shuffle and distribute again
        game.shuffleAndDeal(players);

        // Resetting game variables
        currentPlayerIndex = 0;
        turnAnnouncements = new ArrayList<>();
        teamScores = new HashMap<>();
        turnBuffer = new ArrayList<>();
        bufferCredit = null;
        turnMustEnd = false;
        winner = null;
        badDraw = false;

    }


    /** Returns the current player */
    public Student getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    /** Returns all players */
    public List<Student> getAllPlayers() {
        return players;
    }

    /** Returns current player teammates **/
    public List<Student> getCurrentPlayerTeamMates() {
        List<Student> team = new ArrayList<>(List.of());
        players.forEach(e-> {
             if (e.getTeam().equals(getCurrentPlayer().getTeam())) team.add(e);
        });
        return team;
    }

    /** Returns turn announcements since last action */
    public List<String> getTurnAnnouncements() {
        List<String> announcements = new ArrayList<>(turnAnnouncements);
        //turnAnnouncements.clear();
        return announcements;
    }

    /** Draw a card from the center pile */
    public Subject drawFromCenter(int index) {
        List<Subject> center = game.getCenterPile();
        if (index < 0 || index >= center.size()) return null;

        Subject drawn = center.remove(index);
        TurnCard tc = new TurnCard(drawn, TurnCard.Origin.CENTER);
        tc.centerIndex = index;
        addToTurnBuffer(getCurrentPlayer(), tc);

        return drawn;
    }

    /**
     * Draw the min/max card from a player’s hand
     */
    public void drawFromPlayerHand(Student target, boolean pickMax) {
        if (target.getSubjects().isEmpty()) return;

        Subject selected = target.getSubjects().getFirst();
        for (Subject s : target.getSubjects()) {
            if ((pickMax && s.getCredit() > selected.getCredit())
                || (!pickMax && s.getCredit() < selected.getCredit())) {
                selected = s;
            }
        }

        int handIndex = target.getSubjects().indexOf(selected);
        target.getSubjects().remove(selected);

        TurnCard tc = new TurnCard(selected, TurnCard.Origin.PLAYER);
        tc.sourcePlayer = target;
        tc.handIndex = handIndex;
        addToTurnBuffer(getCurrentPlayer(), tc);

    }

    /** End the current player’s turn */
    public void endTurn() {
        // Return remaining buffer cards to hand
        if (!turnBuffer.isEmpty()) {
            for (TurnCard t : new ArrayList<>(turnBuffer)) {
                if(players.contains(t.sourcePlayer))
                    players.get(players.indexOf(t.sourcePlayer)).addSubjectToHand(t.card);
                turnBuffer.remove(t);
            }
        }

        bufferCredit = null;
        turnMustEnd = false;
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    /** Adds a card to the turn buffer and handles credit logic / trio validation */
    private void addToTurnBuffer(Student player, TurnCard tc) {
        badDraw = false;
        turnBuffer.add(tc);

        if (bufferCredit == null) {
            bufferCredit = tc.card.getCredit();
        } else if (!bufferCredit.equals(tc.card.getCredit())) {
            // Credit mismatch → return buffer
            returnBufferToOrigins();
            bufferCredit = null;
            turnMustEnd = true;
            badDraw = true;
            return;
        }

        // Check if a trio can be validated
        // List<Subject> available = new ArrayList<>(player.getSubjects());
        List<Subject> available = new ArrayList<>();
        for (TurnCard t : turnBuffer) available.add(t.card);

        for (Competence c : game.getAllCompetences()) {
            if (!hasAlreadyValidatedTrio(player, c) && new HashSet<>(available).containsAll(c.getSubjects())) {
                validateTrio(player, c);
                return;
            }
        }

    }

    /** Validates a trio for a player */
    private void validateTrio(Student player, Competence trio) {
        for (Subject s : trio.getSubjects()) {
            player.addValidatedSubject(s);
            player.getSubjects().remove(s);
            turnBuffer.removeIf(t -> t.card.equals(s));
        }

        bufferCredit = null;
        turnMustEnd = true;

        // Update team score
        if (game.getMode().equals("TEAM")) {
            teamScores.put(player.getTeam(), teamScores.get(player.getTeam()) + 1);
        }

        // Add announcement
        turnAnnouncements.add(player.getPseudo() + " (" + player.getTeam().getPseudo() + ") a trouvé le trio: " + trio.getName());
    }

    private boolean hasAlreadyValidatedTrio(Student player, Competence competence) {
        for (Subject s : competence.getSubjects()) {
            if (player.getValidatedSubjects().contains(s)) return true;
        }
        return false;
    }

    private void returnBufferToOrigins() {
        for (TurnCard t : new ArrayList<>(turnBuffer)) {
            if (t.origin == TurnCard.Origin.CENTER) {
                game.getCenterPile().add(Math.min(t.centerIndex, game.getCenterPile().size()), t.card);
            } else if (t.origin == TurnCard.Origin.PLAYER && t.sourcePlayer != null) {
                t.sourcePlayer.addSubjectToHand(t.card);
            }
            turnBuffer.remove(t);
        }
    }

    /** Checks if the current player has met the win condition */
    public boolean isWinningConditionMet(Student player) {
        String difficulty = game.getDifficulty();

        if (game.getMode().equals("SOLO")) {
            if ("SIMPLE".equals(difficulty)) {
                if (player.getValidatedSubjects().size() >= 9) return true;
                return player.getValidatedSubjects().stream().anyMatch(s -> s.getCredit() == 7);
            } else if ("PICANTE".equals(difficulty)) {
                List<Competence> validated = getValidatedCompetencesForPlayer(player);
                Set<Competence> validatedSet = new HashSet<>(validated);
                for (Competence c : validated) {
                    if (c.getLinkedCompetences().stream().anyMatch(validatedSet::contains)) {
                        winner = player.getTeam();
                        return true;
                    }
                }
                return false;
            }
        } else if (game.getMode().equals("TEAM")) {
            Team team = player.getTeam();
            List<Competence> teamValidated = getValidatedCompetencesForTeam(team);
            Set<Competence> validatedSet = new HashSet<>(teamValidated);
            for (Competence c : teamValidated) {
                if (c.getLinkedCompetences().stream().anyMatch(validatedSet::contains)) {
                    winner = player.getTeam();
                    return true;
                }
            }
        }

        return false;
    }

    /** Retrieve validated competences for a player */
    public List<Competence> getValidatedCompetencesForPlayer(Student player) {
        return game.getValidatedCompetences(player);
    }

    public static List<Competence> getCompetences(Student player, Game game) {
        List<Competence> result = new ArrayList<>();
        Set<Subject> validated = new HashSet<>(player.getValidatedSubjects());
        for (Competence c : game.getAllCompetences()) {
            if (validated.containsAll(c.getSubjects())) result.add(c);
        }
        return result;
    }

    private List<Competence> getValidatedCompetencesForTeam(Team team) {
        Set<Subject> teamValidatedSubjects = new HashSet<>();
        for (Student s : players) if (s.getTeam().equals(team)) teamValidatedSubjects.addAll(s.getValidatedSubjects());

        List<Competence> result = new ArrayList<>();
        for (Competence c : game.getAllCompetences()) {
            if (teamValidatedSubjects.containsAll(c.getSubjects())) result.add(c);
        }
        return result;
    }

    /** Checks if the game is over */
    public boolean isGameOver() {
        return players.stream().anyMatch(this::isWinningConditionMet);
    }

    /** Returns turn buffer contents for GUI display */
    public List<Subject> getTurnBufferSubjects() {
        List<Subject> list = new ArrayList<>();
        for (TurnCard t : turnBuffer) list.add(t.card);
        return list;
    }

    /** Returns rather a turn is over or not */
    public boolean isTurnOver() {
        return turnMustEnd;
    }


    /** Returns Game attribute */
    public Game getGame() { return game;}

    /** Returns game winner */
    public Team getWinner() { return winner; }

    /** Returns rather it is the end of the first turn */
    public boolean isBadDraw() { return badDraw; }
}


