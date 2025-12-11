package Trio;

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
        
        // --- ÉTAPE 1: DEMANDER LE NOMBRE DE JOUEURS ---
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

            if (playerCount < 3 || playerCount > 6) {
                System.out.println("Attention : le jeu se joue uniquement avec 3, 4, 5 ou 6 joueurs.\n");
            }
        } while (playerCount < 3 || playerCount > 6);
        
        System.out.println("\nParfait ! La partie sera configurée pour " + playerCount + " joueurs.\n");

        // --- ÉTAPE 2: CRÉER LES JOUEURS ---
        List<Student> players = new ArrayList<>();
        for (int i = 1; i <= playerCount; i++) {
            System.out.println("--- Configuration du Joueur " + i + " ---");
            System.out.print("Entrez le pseudo : ");
            String pseudo = scanner.next(); // On lit le pseudo

            System.out.print("Entrez la spécialité (ex: MECA, RIEN, INFO) : ");
            String specialty = scanner.next(); // On lit la spécialité

            // On crée le joueur et on l'ajoute à la liste
            // Pour l'instant, on assigne une équipe par défaut.
            players.add(new Student(i, pseudo, specialty, new Team("Equipe " + Integer.toString(i), "Couleur " + Integer.toString(i))));
            System.out.println("Joueur " + pseudo + " (" + specialty + ") ajouté !\n");
        }
        
        // On n'a plus besoin du scanner, on le ferme.
        scanner.close();

        // --- ÉTAPE 3: LANCER LA PARTIE ---
        System.out.println("\n####################################");
        System.out.println("### DÉBUT DE LA PARTIE ###");
        System.out.println("####################################\n");

        Partie partie = new Partie("Multi", "Picante");
        partie.shuffleAndDeal(players);

        // --- ÉTAPE 4: VÉRIFIER LA DISTRIBUTION ---
        System.out.println("\n--- VÉRIFICATION DE LA DISTRIBUTION ---");
        for (Student player : players) {
            System.out.println("Le joueur " + player.getPseudo() + " a reçu " + player.getSubjects().size() + " cartes.");
        }
        System.out.println("La pioche centrale contient " + partie.getCenterPile().size() + " cartes.");
        System.out.println("Total des cartes distribuées : " + (partie.getAllSubjects().size()));
        System.out.println("\nLa partie est prête à commencer !");
    }
}
