package Trio.console;

import Trio.model.Game;
import Trio.model.classes.Student;
import Trio.model.classes.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner; // On importe l'outil pour lire l'entrée de l'utilisateur

public class Main {

    public static void main(String[] args) {
        
        // Outil pour lire les entrées de la console
        Scanner scanner = new Scanner(System.in);

        System.out.println("####################################");
        System.out.println("### BIENVENUE DANS LE JEU DE TRIO ###");
        System.out.println("####################################\n");
        
        // --- ÉTAPE 1: DEMANDER LE MODE DE JEU ---
        String gameMode = selectGameMode(scanner);

        // --- ÉTAPE 1.5: DEMANDER LA DIFFICULTÉ ---
        String difficulty = selectDifficulty(scanner);
        
        // --- ÉTAPE 2: DEMANDER LE NOMBRE DE JOUEURS ---
        int playerCount = 0;
        // On boucle tant que l'utilisateur ne donne pas un nombre entre 3 et 6
        do {
            System.out.print("Veuillez entrer le nombre de joueurs (entre 3 et 6) : ");
            // On s'assure que l'utilisateur entre bien un nombre
            while (!scanner.hasNextInt()) {
                System.out.println("Erreur : veuillez entrer un nombre valide.");
                scanner.next(); // On vide l'entrée invalide
            }
            playerCount = scanner.nextInt();
            scanner.nextLine(); // Consommer la nouvelle ligne

            if (playerCount < 3 || playerCount > 6) {
                System.out.println("Attention : le jeu se joue uniquement avec 3, 4, 5 ou 6 joueurs.\n");
            }
        } while (playerCount < 3 || playerCount > 6);
        
        System.out.println("\nParfait ! La partie sera configurée pour " + playerCount + " joueurs en mode " + gameMode + " (difficulté: " + difficulty + ").\n");

        // --- ÉTAPE 3: CRÉER LES JOUEURS ---
        List<Student> players = new ArrayList<>();
        List<Team> teams = new ArrayList<>();
        
        if (gameMode.equals("TEAM")) {
            // En mode équipe, créer les équipes d'abord
            int teamCount = playerCount / 2;
            for (int i = 1; i <= teamCount; i++) {
                System.out.print("Entrez le nom de l'équipe " + i + " : ");
                String teamName = scanner.nextLine();
                teams.add(new Team(teamName, "Couleur " + i));
            }
        }
        
        for (int i = 1; i <= playerCount; i++) {
            System.out.println("\n--- Configuration du Joueur " + i + " ---");
            System.out.print("Entrez le pseudo : ");
            String pseudo = scanner.nextLine(); // On lit le pseudo

            System.out.print("Entrez la spécialité (ex: MECA, RIEN, INFO) : ");
            String specialty = scanner.nextLine(); // On lit la spécialité

            // Assigner à une équipe
            Team team;
            if (gameMode.equals("TEAM")) {
                System.out.print("Entrez le numéro de l'équipe (1-" + teams.size() + ") : ");
                int teamIndex = scanner.nextInt() - 1;
                scanner.nextLine();
                team = teams.get(Math.min(Math.max(teamIndex, 0), teams.size() - 1));
            } else {
                team = new Team("Equipe " + i, "Couleur " + i);
            }

            // On crée le joueur et on l'ajoute à la liste
            players.add(new Student(i, pseudo, specialty, team));
            System.out.println("✅ Joueur " + pseudo + " (" + specialty + ") ajouté à " + team.getPseudo() + " !");
        }

        // --- ÉTAPE 4: CRÉER ET LANCER LA PARTIE ---
        System.out.println("\n####################################");
        System.out.println("### DÉBUT DE LA PARTIE ###");
        System.out.println("####################################\n");

        Game game = new Game(gameMode, difficulty);
        
        // --- ÉTAPE 5: AFFICHER LES INFOS DE DISTRIBUTION ---
        System.out.println("\n--- INFORMATIONS DU JEU ---");
        System.out.println("Mode: " + gameMode);
        System.out.println("Difficulté: " + difficulty);
        System.out.println("Nombre de joueurs: " + playerCount);
        System.out.println("Nombre de trios disponibles: " + game.getAllCompetences().size());
        System.out.println("Nombre de cartes totales: " + game.getAllSubjects().size());
        
        // --- ÉTAPE 6: LANCER LA BOUCLE DE JEU ---
        int playAgain = 1;
        while(playAgain == 1){
        
            GameLoop gameLoop = new GameLoop(game, players, scanner);
            gameLoop.play();

            // --- ÉTAPE 7: PROPOSER DE REJOUER ---
            playAgain = restartGameMenu(scanner);
        
        }    
        // Fermer le scanner après la partie
        scanner.close();
    }
    
    /**
     * Permet à l'utilisateur de sélectionner le mode de jeu
     */
    private static String selectGameMode(Scanner scanner) {
        System.out.println("\n📋 SÉLECTIONNEZ LE MODE DE JEU:");
        System.out.println("1 - SOLO (chaque joueur joue individuellement)");
        System.out.println("2 - TEAM (les joueurs sont groupés par équipes)");
        
        int choice = 0;
        do {
            System.out.print("\nChoisissez le mode (1 ou 2): ");
            while (!scanner.hasNextInt()) {
                System.out.println("Erreur : veuillez entrer 1 ou 2.");
                scanner.next();
            }
            choice = scanner.nextInt();
            scanner.nextLine(); // Consommer la nouvelle ligne
            
            if (choice != 1 && choice != 2) {
                System.out.println("Choix invalide. Veuillez entrer 1 ou 2.");
            }
        } while (choice != 1 && choice != 2);
        
        return choice == 1 ? "SOLO" : "TEAM";
    }

    private static String selectDifficulty(Scanner scanner) {
        System.out.println("\n📋 SÉLECTIONNEZ LA DIFFICULTÉ:");
        System.out.println("1 - SIMPLE");
        System.out.println("2 - PICANTE");

        int choice = 0;
        do {
            System.out.print("\nChoisissez la difficulté (1 ou 2): ");
            while (!scanner.hasNextInt()) {
                System.out.println("Erreur : veuillez entrer 1 ou 2.");
                scanner.next();
            }
            choice = scanner.nextInt();
            scanner.nextLine();
            if (choice != 1 && choice != 2) {
                System.out.println("Choix invalide. Veuillez entrer 1 ou 2.");
            }
        } while (choice != 1 && choice != 2);

        return choice == 1 ? "SIMPLE" : "PICANTE";
    }

    private static void clearConsole() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            System.out.println("Erreur lors du nettoyage de la console.");
        }
    }

    private static int restartGameMenu(Scanner scanner) {
        System.out.println("\nVoulez-vous rejouer ? (O/N)");

        scanner = new Scanner(System.in);
        String input = scanner.nextLine().trim().toUpperCase();
        if (input.equals("O")) {
            clearConsole();
            return 1;    
        } else {
            System.out.println("Merci d'avoir joué à TRIO ! À bientôt !");
            return 0;
        }
        
    }

}
