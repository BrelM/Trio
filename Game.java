package Trio;

import Trio.CompetencePackage.Competence;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game {
    private String mode;
    private String difficulty;
    private List<Subject> centerPile = new ArrayList<>(); // Pioche centrale
    private List<Subject> allSubjects = new ArrayList<>(); // Toutes les cartes UE du jeu
    private List<Competence> allCompetences = new ArrayList<>(); // Toutes les combinaisons de Trio possibles

    public Game(String mode, String difficulty) {
        this.mode = mode;
        this.difficulty = difficulty;
        createGameDeck(); // On crée les cartes dès le début de la partie
    }

    /**
     * Crée l'ensemble des 36 cartes UE (Subjects) et les 12 Trios (Competences)
     * en se basant sur la liste fournie.
     */
    private void createGameDeck() {
        // Créer toutes les UEs
        Subject ap01 = new Subject("AP01", 1, true);
        Subject ap4a = new Subject("AP4A", 7, true);
        Subject ap03 = new Subject("AP03", 2, true);
        
        Subject mt01 = new Subject("MT01", 3, true);
        Subject mt02 = new Subject("MT02", 4, true);
        Subject mt03 = new Subject("MT03", 5, true);

        Subject mc01 = new Subject("MC01", 6, true);
        Subject mc02 = new Subject("MC02", 8, true);
        Subject mc03 = new Subject("MC03", 9, true);

        Subject ea01 = new Subject("EA01", 11, true);
        Subject ea02 = new Subject("EA02", 12, true);
        Subject ea03 = new Subject("EA03", 13, true);

        Subject ge01 = new Subject("GE01", 1, true);
        Subject ge02 = new Subject("GE02", 7, true);
        Subject ge03 = new Subject("GE03", 2, true);

        Subject mk01 = new Subject("MK01", 3, true);
        Subject mk02 = new Subject("MK02", 4, true);
        Subject mk03 = new Subject("MK03", 5, true);

        Subject fi01 = new Subject("FI01", 6, true);
        Subject fi02 = new Subject("FI02", 8, true);
        Subject fi03 = new Subject("FI03", 9, true);

        Subject ht01 = new Subject("HT01", 10, true);
        Subject ht02 = new Subject("HT02", 11, true);
        Subject ht03 = new Subject("HT03", 12, true);

        Subject co01 = new Subject("CO01", 1, true);
        Subject co02 = new Subject("CO02", 7, true);
        Subject co03 = new Subject("CO03", 2, true);
        
        Subject dr01 = new Subject("DR01", 3, true);
        Subject dr02 = new Subject("DR02", 4, true);
        Subject dr03 = new Subject("DR03", 5, true);
        
        Subject ic01 = new Subject("IC01", 6, true);
        Subject ic02 = new Subject("IC02", 8, true);
        Subject ic03 = new Subject("IC03", 9, true);
        
        Subject vt01 = new Subject("VT01", 10, true);
        Subject vt02 = new Subject("VT02", 11, true);
        Subject vt03 = new Subject("VT03", 12, true);
        
        // Ajouter toutes les UEs à la liste principale
        this.allSubjects.addAll(List.of(ap01, ap4a, ap03, mt01, mt02, mt03, mc01, mc02, mc03, ea01, ea02, ea03, 
                                        ge01, ge02, ge03, mk01, mk02, mk03, fi01, fi02, fi03, ht01, ht02, ht03, 
                                        co01, co02, co03, dr01, dr02, dr03, ic01, ic02, ic03, vt01, vt02, vt03));
        


        // Coefficient 1
        createTrio("Trio-Coeff1-1", ap01, ge01, co01);  
        // Coefficient 2
        createTrio("Trio-Coeff2-1", ap03, ge03, co03);     
        // Coefficient 7
        createTrio("Trio-Coeff7-1", ap4a, ge02, co02);     

        // Coefficient 3
        createTrio("Trio-Coeff3", mt01, mk01, dr01);

        // Coefficient 4
        createTrio("Trio-Coeff4", mt02, mk02, dr02);

        // Coefficient 5
        createTrio("Trio-Coeff5", mt03, mk03, dr03);

        // Coefficient 6
        createTrio("Trio-Coeff6", mc01, fi01, ic01);

        // Coefficient 8
        createTrio("Trio-Coeff8", mc02, fi02, ic02);

        // Coefficient 9
        createTrio("Trio-Coeff9", mc03, fi03, ic03);

        // Coefficient 10
        createTrio("Trio-Coeff10", ea01, ht01, vt01);

        // Coefficient 11
        createTrio("Trio-Coeff11", ea02, ht02, vt02);

        // Coefficient 12
        createTrio("Trio-Coeff12", ea03, ht03, vt03);
        
    }

    private void createTrio(String name, Subject s1, Subject s2, Subject s3) {
        Competence trio = new Competence(name);
        trio.addLinkedSubject(s1);
        trio.addLinkedSubject(s2);
        trio.addLinkedSubject(s3);
        this.allCompetences.add(trio);
    }
    

    // 

    // Mélanger et distribuer les cartes
public void shuffleAndDeal(List<Student> players) {
        System.out.println("Mélange du paquet et distribution des cartes...");

        // 1. Mélanger le paquet de cartes
        Collections.shuffle(this.allSubjects);

        int playerCount = players.size();
        int cardsPerPlayer = 0;
        
        // 2. Déterminer le nombre de cartes par joueur en fonction des règles
        switch (playerCount) {
            case 3:
                cardsPerPlayer = 9;
                break;
            case 4:
                cardsPerPlayer = 7;
                break;
            case 5:
                cardsPerPlayer = 6;
                break;
            case 6:
                cardsPerPlayer = 5;
                break;
            default:
                // Gérer les cas non prévus (ex: 2 joueurs ou plus de 6)
                System.out.println("Le nombre de joueurs (" + playerCount + ") n'est pas supporté par les règles de distribution.");
                return;
        }

        // 3. Distribuer les cartes aux joueurs
        int cardIndex = 0;
        for (Student player : players) {
            System.out.println("Distribution de " + cardsPerPlayer + " cartes à " + player.getPseudo() + "...");
            for (int i = 0; i < cardsPerPlayer; i++) {
                // Donne la carte au joueur et avance dans le paquet
                player.addSubjectToHand(this.allSubjects.get(cardIndex));
                cardIndex++;
            }
        }
        
        // 4. Placer les cartes restantes dans la pioche centrale
        System.out.println("Placement des cartes restantes au centre...");
        while (cardIndex < this.allSubjects.size()) {
            this.centerPile.add(this.allSubjects.get(cardIndex));
            cardIndex++;
        }
        // Mélanger la pioche centrale pour s'assurer qu'elle est aléatoire
        Collections.shuffle(this.centerPile);

        System.out.println("Distribution terminée.");
    }
    public List<Subject> getCenterPile() {
        return centerPile;
    }
    
    public List<Subject> getAllSubjects() {
        return allSubjects;
    }

    public List<Competence> getAllCompetences() {
        return allCompetences;
    }
}
