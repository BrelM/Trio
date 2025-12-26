package Trio;

import Trio.Game;
import Trio.Student;
import Trio.Team;
import Trio.CompetencePackage.Competence;
import java.util.*;

public class GameLoop {
    private Game game;
    private List<Student> players;
    private int currentPlayerIndex;
    private List<String> turnAnnouncements; // Annonces de trios trouvés au tour précédent
    private Map<Team, Integer> teamScores; // Scores des équipes (nombre de trios trouvés)
    private Scanner scanner; // Scanner partagé
    // Buffer temporaire pour les cartes piochées durant un tour (avec origine)
    private static class TurnCard {
        Subject card;
        enum Origin { CENTER, PLAYER }
        Origin origin;
        int centerIndex = -1; // si origin == CENTER
        Student sourcePlayer = null; // si origin == PLAYER
        int handIndex = -1; // original index in hand

            TurnCard(Subject card, Origin origin) { this.card = card; this.origin = origin; }
    }
    private List<TurnCard> turnBuffer = new ArrayList<>();
    private Integer bufferCredit = null; // crédit de la séquence en cours
    private boolean turnMustEnd = false; // drapeau pour forcer la fin du tour (ex: trio trouvé)

    public GameLoop(Game game, List<Student> players, Scanner scanner) {
        this.game = game;
        this.players = players;
        this.currentPlayerIndex = 0;
        this.turnAnnouncements = new ArrayList<>();
        this.teamScores = new HashMap<>();
        this.scanner = scanner;
        
        // Initialiser les scores des équipes
        for (Student player : players) {
            teamScores.putIfAbsent(player.getTeam(), 0);
        }
    }

    /**
     * Lance la boucle principale du jeu
     */
    public void play() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("DÉBUT DE LA PARTIE - MODE " + game.getMode());
        System.out.println("=".repeat(50) + "\n");

        // Mélanger et distribuer les cartes
        game.shuffleAndDeal(players);

        // Boucle principale du jeu
        while (!isGameOver()) {
            playTurn();
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        }

        // Afficher le résumé final
        displayGameEnd();
    }

    /**
     * Exécute le tour d'un joueur
     */
    private void playTurn() {
        Student currentPlayer = players.get(currentPlayerIndex);

        // Étape 1: Écran de confidentialité pendant 5 secondes
        displayPrivacyScreen();

        // Étape 2: Afficher les annonces du tour précédent
        if (!turnAnnouncements.isEmpty()) {
            displayAnnouncements();
            turnAnnouncements.clear();
        }

        // Réinitialiser le buffer de tour
        turnBuffer.clear();
        bufferCredit = null;
        turnMustEnd = false;
        // Vérifier automatiquement si le joueur a déjà des trios dans sa main
        checkAndValidateTrios(currentPlayer);

        // Étape 3: Afficher le menu principal du tour
        System.out.println("\n" + "-".repeat(50));
        System.out.println("TOUR DE " + currentPlayer.getPseudo() + " (" + currentPlayer.getTeam().getPseudo() + ")");
        System.out.println("-".repeat(50));

        boolean turnEnded = false;

        while (!turnEnded) {
            displayTurnMenu(currentPlayer);
            System.out.print("\nChoisissez une action: ");
            
            if (!scanner.hasNextInt()) {
                System.out.println("❌ Entrée invalide. Veuillez entrer un nombre.");
                scanner.nextLine();
                continue;
            }

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consommer la nouvelle ligne

            switch (choice) {
                case 1:
                    displayHand(currentPlayer);
                    break;
                case 2:
                    if (game.getCenterPile().isEmpty()) {
                        System.out.println("❌ La pioche centrale est vide !");
                    } else {
                        drawFromCenterPile(currentPlayer);
                    }
                    break;
                case 3:
                    displayValidatedTrios(currentPlayer);
                    break;
                case 4:
                    displayOtherPlayersValidatedTrios();
                    break;
                case 5:
                    drawFromPlayerHand(currentPlayer);
                    break;
                case 6:
                    turnEnded = true;
                    break;
                case 0:
                    if (turnBuffer.isEmpty()) {
                        System.out.println("Vous n'avez encore rien pioché ce tour.");
                    } else {
                        System.out.println("\n🧾 Cartes piochées ce tour:");
                        for (int i = 0; i < turnBuffer.size(); i++) {
                            TurnCard t = turnBuffer.get(i);
                            String originDesc = (t.origin == TurnCard.Origin.CENTER) ? ("Pioche centrale (index " + t.centerIndex + ")") : ("Main de " + (t.sourcePlayer != null ? t.sourcePlayer.getPseudo() : "inconnu"));
                            System.out.println("  " + (i + 1) + ". " + t.card.getId() + " (Coefficient " + t.card.getCredit() + ") - " + originDesc);
                        }
                    }
                    break;
                default:
                    System.out.println("❌ Choix invalide. Veuillez réessayer.");
            }
            // Si une action (ex: validation d'un trio) a forcé la fin du tour
            if (turnMustEnd) {
                turnEnded = true;
                break;
            }
        }

        // Vérification de fin de partie après le tour
        if (isGameOver()) {
            return;
        }

        // Message de fin de tour
        displayTurnEndMessage(currentPlayer);
    }

    /**
     * Affiche l'écran de confidentialité pendant 5 secondes
     */
    private void displayPrivacyScreen() {
        System.out.println("\n" + "█".repeat(50));
        System.out.println("⚠️  ATTENTION - ÉCRAN CONFIDENTIEL ⚠️");
        System.out.println("█".repeat(50));
        System.out.println("\n🚨 Les autres joueurs ne doivent PAS regarder l'écran !");
        System.out.println("Le prochain joueur aura accès à sa main privée.\n");

        // Compte à rebours de 5 secondes
        for (int i = 5; i >= 1; i--) {
            System.out.println("⏱️  Affichage de la main dans " + i + " secondes... Ne regardez pas!");
            try {
                Thread.sleep(1000); // Pause 1 seconde
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("\n" + "█".repeat(50));
        System.out.println("C'est parti ! Voici votre main privée.");
        System.out.println("█".repeat(50));
    }

    /**
     * Affiche le menu des actions disponibles
     */
    private void displayTurnMenu(Student player) {
        System.out.println("\n📋 MENU DES ACTIONS:");
        System.out.println("1 - 📖 Voir ma main (" + player.getSubjects().size() + " cartes)");
        System.out.println("2 - 🎴 Tirer une carte de la pioche centrale (carte masquée, choisissez un numéro)");
        System.out.println("3 - ✅ Voir mes trios validés (" + player.getValidatedSubjects().size() + "/9 UEs)");
        System.out.println("4 - 👥 Voir les trios d'autres joueurs");
        System.out.println("5 - 🔁 Tirer d'une main (votre main ou celle d'un autre joueur) - choisir carte la plus faible ou la plus forte");
        System.out.println("6 - ⏹️  Terminer mon tour");
        if (!turnBuffer.isEmpty()) {
            System.out.println("0 - 🧾 Voir les cartes piochées ce tour (" + turnBuffer.size() + ")");
        }
    }

    /**
     * Affiche la main du joueur
     */
    private void displayHand(Student player) {
        System.out.println("\n📖 VOTRE MAIN:");
        List<Subject> hand = player.getSubjects();
        if (hand.isEmpty()) {
            System.out.println("Vous n'avez aucune carte.");
        } else {
            for (int i = 0; i < hand.size(); i++) {
                Subject subject = hand.get(i);
                System.out.println("  " + (i + 1) + ". " + subject.getId() + " (Coefficient " + subject.getCredit() + ")");
            }
        }
    }

    /**
     * Le joueur tire une carte de la pioche centrale
     */
    private void drawFromCenterPile(Student player) {
        if (game.getCenterPile().isEmpty()) {
            System.out.println("❌ La pioche centrale est vide !");
            return;
        }
        List<Subject> center = game.getCenterPile();

        // Afficher uniquement les indices, pas les cartes elles-mêmes
        System.out.println("\n🏪 Choisissez le numéro de la carte à tirer (1.." + center.size() + ") :");
        for (int i = 0; i < center.size(); i++) {
            System.out.println("  " + (i + 1) + ". Carte n°" + (i + 1));
        }

        int idx = -1;
        while (true) {
            System.out.print("Numéro: ");
            if (!scanner.hasNextInt()) {
                System.out.println("Entrée invalide. Veuillez entrer un nombre.");
                scanner.nextLine();
                continue;
            }
            idx = scanner.nextInt() - 1;
            scanner.nextLine();
            if (idx < 0 || idx >= center.size()) {
                System.out.println("Numéro hors de portée. Réessayez.");
                continue;
            }
            break;
        }

        Subject drawnCard = center.remove(idx);
        // Révéler seulement la carte sélectionnée
        System.out.println("\n🎴 Carte révélée: " + drawnCard.getId() + " (Coefficient " + drawnCard.getCredit() + ")");

        // Ajouter au buffer du tour et gérer la logique de séquence
        TurnCard tc = new TurnCard(drawnCard, TurnCard.Origin.CENTER);
        tc.centerIndex = idx;
        addToTurnBuffer(player, tc);
    }

    /**
     * Permet de tirer la carte la plus faible ou la plus forte depuis la main d'un joueur
     * Si la cible est le joueur courant, la carte est simplement révélée (pas déplacée)
     */
    private void drawFromPlayerHand(Student player) {
        // Lister les joueurs disponibles
        System.out.println("\n👥 Choisissez un joueur pour tirer une carte (1.." + players.size() + ") :");
        for (int i = 0; i < players.size(); i++) {
            Student p = players.get(i);
            String label = p.equals(player) ? "Ma main" : p.getPseudo();
            System.out.println("  " + (i + 1) + ". " + label + " (" + p.getTeam().getPseudo() + ") - " + p.getSubjects().size() + " cartes");
        }

        int targetIdx = -1;
        while (true) {
            System.out.print("Numéro du joueur cible: ");
            if (!scanner.hasNextInt()) {
                System.out.println("Entrée invalide.");
                scanner.nextLine();
                continue;
            }
            targetIdx = scanner.nextInt() - 1;
            scanner.nextLine();
            if (targetIdx < 0 || targetIdx >= players.size()) {
                System.out.println("Index hors de portée.");
                continue;
            }
            break;
        }

        Student target = players.get(targetIdx);
        if (target.getSubjects().isEmpty()) {
            System.out.println("Le joueur sélectionné n'a aucune carte.");
            return;
        }

        // Choix min ou max
        System.out.println("Choisissez: 1 - carte la PLUS FAIBLE (credit), 2 - carte la PLUS FORTE (credit)");
        int choice = -1;
        while (true) {
            System.out.print("Votre choix: ");
            if (!scanner.hasNextInt()) {
                System.out.println("Entrée invalide.");
                scanner.nextLine();
                continue;
            }
            choice = scanner.nextInt();
            scanner.nextLine();
            if (choice != 1 && choice != 2) {
                System.out.println("Choix invalide.");
                continue;
            }
            break;
        }

        // Trouver la carte min/max
        Subject selected = null;
        if (choice == 1) {
            int min = Integer.MAX_VALUE;
            for (Subject s : target.getSubjects()) {
                if (s.getCredit() < min) {
                    min = s.getCredit();
                    selected = s;
                }
            }
        } else {
            int max = Integer.MIN_VALUE;
            for (Subject s : target.getSubjects()) {
                if (s.getCredit() > max) {
                    max = s.getCredit();
                    selected = s;
                }
            }
        }

        if (selected == null) {
            System.out.println("Aucune carte trouvée.");
            return;
        }

        // Retirer la carte de la main cible (même si c'est vous) et l'ajouter au buffer du tour
        int handIndex = target.getSubjects().indexOf(selected);
        target.getSubjects().remove(selected);
        System.out.println("\n🔎 Carte sélectionnée: " + selected.getId() + " (Coefficient " + selected.getCredit() + ") de " + target.getPseudo());

        // Ajouter au buffer (les cartes prises ne vont pas directement dans la main)
        TurnCard tc = new TurnCard(selected, TurnCard.Origin.PLAYER);
        tc.sourcePlayer = target;
        tc.handIndex = handIndex;
        addToTurnBuffer(player, tc);
    }

    /**
     * Vérifie et valide les trios du joueur
     */
    /**
     * Ajoute une carte au buffer du tour et gère la logique de séquence:
     * - si c'est la première carte, initialise le crédit de référence
     * - si la carte a un crédit différent, la séquence s'arrête et les cartes du buffer vont dans la main
     * - si un trio est formé à partir des cartes disponibles (main + buffer), on valide le trio et on termine le tour
     */
    private void addToTurnBuffer(Student player, TurnCard tc) {
        turnBuffer.add(tc);
        System.out.println("(Temp) Carte ajoutée au buffer: " + tc.card.getId() + " (Credit " + tc.card.getCredit() + ")");

        if (bufferCredit == null) {
            bufferCredit = tc.card.getCredit();
        } else if (!bufferCredit.equals(tc.card.getCredit())) {
            System.out.println("🔒 Crédit différent (" + tc.card.getCredit() + " ≠ " + bufferCredit + "). Retour des cartes à leur place d'origine.");
            // Remettre les cartes du buffer à leur place d'origine
            returnBufferToOrigins();
            bufferCredit = null;
            turnMustEnd = true;
            return;
        }

        // Après chaque pioche, vérifier si un trio est formé en combinant la main et le buffer
        // Construire l'ensemble des cartes disponibles (main + buffer)
        List<Subject> available = new ArrayList<>(player.getSubjects());
        for (TurnCard t : turnBuffer) available.add(t.card);

        for (Competence competence : game.getAllCompetences()) {
            if (!hasAlreadyValidatedTrio(player, competence)) {
                if (new HashSet<>(available).containsAll(competence.getSubjects())) {
                    // Valider le trio en retirant les cartes pertinentes (de la main ou du buffer)
                    validateTrio(player, competence);
                    // turnMustEnd est défini dans validateTrio
                    return;
                }
            }
        }
    }


    /**
     * Verifie et valide les trios d'un joueur
     * @param player : Player
     */
    private void checkAndValidateTrios(Student player) {
        List<Competence> foundTrios = new ArrayList<>();

        for (Competence competence : game.getAllCompetences()) {
            // Vérifier si le joueur possède les 3 cartes du trio
            List<Subject> competenceSubjects = competence.getSubjects();
            int matchCount = 0;

            for (Subject subject : competenceSubjects) {
                if (player.getSubjects().contains(subject) || bufferContainsSubject(subject)) {
                    matchCount++;
                }
            }

            // Si le joueur a les 3 cartes et qu'il n'a pas déjà ce trio
            if (matchCount == 3 && !hasAlreadyValidatedTrio(player, competence)) {
                foundTrios.add(competence);
            }
        }

        // Valider les trios trouvés
        if (!foundTrios.isEmpty()) {
            for (Competence trio : foundTrios) {
                validateTrio(player, trio);
                // Si un trio est trouvé durant le tour, forcer la fin du tour
                turnMustEnd = true;
            }
        }
    }

    // Helper: vérifier si un Subject est présent dans le buffer de tour
    private boolean bufferContainsSubject(Subject s) {
        for (TurnCard t : turnBuffer) if (t.card.equals(s)) return true;
        return false;
    }

    // Remettre les cartes du buffer à leur place d'origine
    private void returnBufferToOrigins() {
        for (TurnCard t : new ArrayList<>(turnBuffer)) {
            if (t.origin == TurnCard.Origin.CENTER) {
                List<Subject> center = game.getCenterPile();
                int idx = Math.min(Math.max(t.centerIndex, 0), center.size());
                center.add(idx, t.card);
            } else if (t.origin == TurnCard.Origin.PLAYER) {
                Student src = t.sourcePlayer;
                if (src != null) {
                    // Use addSubjectToHand to keep hand ordered by credit
                    src.addSubjectToHand(t.card);
                }
            }
            turnBuffer.remove(t);
        }
    }

    /**
     * Valide un trio pour le joueur
     */
    private void validateTrio(Student player, Competence trio) {
        System.out.println("\n🎉 TRIO TROUVÉ : " + trio.getName() + " !");

        // Ajouter les cartes à validatedSubjects
            for (Subject subject : trio.getSubjects()) {
                player.addValidatedSubject(subject);
                // Retirer la carte de la main si elle s'y trouve, sinon du buffer du tour
                if (player.getSubjects().contains(subject)) {
                    player.getSubjects().remove(subject);
                } else {
                    // Retirer des TurnCards si présent
                    TurnCard toRemove = null;
                    for (TurnCard t : turnBuffer) {
                        if (t.card.equals(subject)) { toRemove = t; break; }
                    }
                    if (toRemove != null) turnBuffer.remove(toRemove);
                }
            }

        // Après validation, si des cartes restent dans le buffer (par ex. tirées avant), on les place dans la main
        if (!turnBuffer.isEmpty()) {
            for (TurnCard t : new ArrayList<>(turnBuffer)) {
                player.addSubjectToHand(t.card);
                turnBuffer.remove(t);
            }
        }
        bufferCredit = null; // réinitialiser la séquence

        // Mettre à jour le score de l'équipe (en mode équipe)
        if (game.getMode().equals("TEAM")) {
            teamScores.put(player.getTeam(), teamScores.get(player.getTeam()) + 1);
        }

        // Ajouter une annonce pour les autres joueurs
        turnAnnouncements.add(player.getPseudo() + " (" + player.getTeam().getPseudo() + ") a trouvé le trio: " + trio.getName());

        // Vérifier si le joueur/équipe a gagné
        if (isWinningConditionMet(player)) {
            return; // La partie se terminera au prochain appel de isGameOver()
        }

        // Le joueur doit arrêter de tirer après avoir trouvé un trio
        System.out.println("⏹️  Votre tour s'arrête ici.");
        turnMustEnd = true;
    }

    /**
     * Vérifie si le joueur a déjà validé ce trio
     * Une compétence est validée si au moins 1 de ses 3 sujets est dans validatedSubjects
     * (car les 3 sont toujours validés ensemble ou pas du tout)
     */
    private boolean hasAlreadyValidatedTrio(Student player, Competence competence) {
        List<Subject> competenceSubjects = competence.getSubjects();
        
        // Vérifier que la compétence est complète (3 sujets)
        if (competenceSubjects == null || competenceSubjects.size() != 3) {
            return false; // Compétence malformée, ne pas la valider
        }

        // Si un seul des 3 sujets est dans validatedSubjects, le trio entier est validé
        for (Subject subject : competenceSubjects) {
            if (player.getValidatedSubjects().contains(subject)) {
                return true; // Trio déjà validé
            }
        }

        return false; // Trio pas encore validé
    }

    /**
     * Affiche la pioche centrale
     */
    private void displayCenterPile() {
        System.out.println("\n🏪 PIOCHE CENTRALE (" + game.getCenterPile().size() + " cartes):");
        List<Subject> centerPile = game.getCenterPile();
        if (centerPile.isEmpty()) {
            System.out.println("La pioche centrale est vide.");
        } else {
            for (int i = 0; i < centerPile.size(); i++) {
                Subject subject = centerPile.get(i);
                System.out.println("  " + (i + 1) + ". " + subject.getId() + " (Coefficient " + subject.getCredit() + ")");
            }
        }
    }

    /**
     * Affiche les trios validés du joueur courant
     */
    private void displayValidatedTrios(Student player) {
        System.out.println("\n✅ VOS TRIOS VALIDÉS:");
        List<Subject> validatedSubjects = player.getValidatedSubjects();
        if (validatedSubjects.isEmpty()) {
            System.out.println("Vous n'avez pas encore validé de trio.");
        } else {
            System.out.println("Total: " + validatedSubjects.size() + "/9 UEs");
            for (Subject subject : validatedSubjects) {
                System.out.println("  - " + subject.getId() + " (Coefficient " + subject.getCredit() + ")");
            }
        }
    }

    /**
     * Affiche les trios validés des autres joueurs
     */
    private void displayOtherPlayersValidatedTrios() {
        System.out.println("\n👥 TRIOS DES AUTRES JOUEURS:");
        for (Student player : players) {
            if (!player.equals(players.get(currentPlayerIndex))) {
                System.out.println("\n" + player.getPseudo() + " (" + player.getTeam().getPseudo() + "): " + player.getValidatedSubjects().size() + "/9 UEs");
                if (!player.getValidatedSubjects().isEmpty()) {
                    for (Subject subject : player.getValidatedSubjects()) {
                        System.out.println("  - " + subject.getId() + " (Coefficient " + subject.getCredit() + ")");
                    }
                }
            }
        }
    }

    /**
     * Affiche les annonces du tour précédent
     */
    private void displayAnnouncements() {
        System.out.println("\n📢 ANNONCES DU TOUR PRÉCÉDENT:");
        for (String announcement : turnAnnouncements) {
            System.out.println("  🎉 " + announcement);
        }
    }

    /**
     * Affiche le message de fin de tour
     */
    private void displayTurnEndMessage(Student player) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("✋ Tour de " + player.getPseudo() + " terminé!");
        System.out.println("Le prochain joueur peut maintenant jouer.");
        System.out.println("=".repeat(50));
        
        // Pause avant le prochain tour
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Vérifie la condition de victoire
     */
    private boolean isWinningConditionMet(Student player) {
        String difficulty = game.getDifficulty();

        if (game.getMode().equals("SOLO")) {
            if ("SIMPLE".equals(difficulty)) {
                // SIMPLE difficulty: either 3 trios (9 UEs) or the trio with credit 7
                if (player.getValidatedSubjects().size() >= 9) return true;
                for (Subject s : player.getValidatedSubjects()) if (s.getCredit() == 7) return true;
                return false;
            } else if (game.getMode().equals("SOLO")) {
                // PICANTE: player must have two linked competences
                List<Competence> validated = getValidatedCompetencesForPlayer(player);
                Set<Competence> validatedSet = new HashSet<>(validated);
                for (Competence c : validated) {
                    List<Competence> linked = c.getLinkedCompetences();
                    if (linked != null) {
                        for (Competence l : linked) {
                            if (validatedSet.contains(l)) return true;
                        }
                    }
                }
                return false;
            }
        } else if (game.getMode().equals("TEAM")) {
            Team team = player.getTeam();
            if ("SIMPLE".equals(game.getDifficulty())) {
                // SIMPLE: either team has 3 trios (aggregate of validatedSubjects >=9 per player?)
                // We'll consider individual players reaching 9 UEs or any team member having the credit-7 trio
                for (Student teamMember : players) {
                    if (teamMember.getTeam().equals(team)) {
                        if (teamMember.getValidatedSubjects().size() >= 9) return true;
                        for (Subject s : teamMember.getValidatedSubjects()) if (s.getCredit() == 7) return true;
                    }
                }
                return false;
            } else if ("PICANTE".equals(game.getDifficulty())) {
                // PICANTE: team must have two linked competences among union of validated competences
                List<Competence> teamValidated = getValidatedCompetencesForTeam(team);
                Set<Competence> validatedSet = new HashSet<>(teamValidated);
                for (Competence c : teamValidated) {
                    List<Competence> linked = c.getLinkedCompetences();
                    if (linked != null) {
                        for (Competence l : linked) {
                            if (validatedSet.contains(l)) return true;
                        }
                    }
                }
                return false;
            }
        }
        return false;
        }

    // Retourne la liste des compétences (trios) que le joueur a déjà validées
    private List<Competence> getValidatedCompetencesForPlayer(Student player) {
        List<Competence> result = new ArrayList<>();
        Set<Subject> validated = new HashSet<>(player.getValidatedSubjects());
        for (Competence c : game.getAllCompetences()) {
            if (validated.containsAll(c.getSubjects())) result.add(c);
        }
        return result;
    }

    // Retourne la liste des compétences validées par l'équipe (union des sujets validés)
    private List<Competence> getValidatedCompetencesForTeam(Team team) {
        Set<Subject> teamValidatedSubjects = new HashSet<>();
        for (Student s : players) {
            if (s.getTeam().equals(team)) teamValidatedSubjects.addAll(s.getValidatedSubjects());
        }
        List<Competence> result = new ArrayList<>();
        for (Competence c : game.getAllCompetences()) {
            if (teamValidatedSubjects.containsAll(c.getSubjects())) result.add(c);
        }
        return result;
    }
    

    /**
     * Vérifie si la partie est terminée
     */
    private boolean isGameOver() {
        if (game.getMode().equals("SOLO")) {
            for (Student player : players) {
                if (isWinningConditionMet(player)) {
                    return true;
                }
            }
        } else if (game.getMode().equals("TEAM")) {
            for (Team team : teamScores.keySet()) {
                for (Student player : players) {
                    if (player.getTeam().equals(team) && isWinningConditionMet(player)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Affiche le résumé final et le gagnant
     */
    private void displayGameEnd() {
        System.out.println("\n" + "█".repeat(50));
        System.out.println("🏆 FIN DE LA PARTIE 🏆");
        System.out.println("█".repeat(50));

        if (game.getMode().equals("SOLO")) {
            for (Student player : players) {
                if (isWinningConditionMet(player)) {
                    System.out.println("\n🎉 GAGNANT: " + player.getPseudo());
                    System.out.println("   Équipe: " + player.getTeam().getPseudo());
                    System.out.println("   Trios validés: " + (player.getValidatedSubjects().size() / 3));
                    break;
                }
            }
        } else if (game.getMode().equals("TEAM")) {
            for (Student player : players) {
                if (isWinningConditionMet(player)) {
                    System.out.println("\n🎉 ÉQUIPE GAGNANTE: " + player.getTeam().getPseudo());
                    System.out.println("   Joueur décisif: " + player.getPseudo());
                    System.out.println("   Raison: Trio avec coefficient 7");
                    break;
                }
            }
        }

        // Afficher les statistiques finales
        displayFinalStatistics();
    }

    /**
     * Affiche les statistiques finales
     */
    private void displayFinalStatistics() {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("📊 STATISTIQUES FINALES:");
        System.out.println("-".repeat(50));

        if (game.getMode().equals("SOLO")) {
            for (Student player : players) {
                System.out.println(player.getPseudo() + ": " + player.getValidatedSubjects().size() + " UEs (" + (player.getValidatedSubjects().size() / 3) + " trios)");
            }
        } else if (game.getMode().equals("TEAM")) {
            for (Team team : teamScores.keySet()) {
                System.out.println(team.getPseudo() + ": " + teamScores.get(team) + " trios");
            }
        }

        System.out.println("=".repeat(50));
    }
}
