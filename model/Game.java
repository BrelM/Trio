package Trio.model;

import Trio.competence.Competence;
import Trio.competence.ManagementCompetence;
import Trio.competence.ScientificCompetence;
import Trio.competence.SocietalCompetence;
import Trio.model.classes.Student;
import Trio.model.classes.Subject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game {
    private String mode; // "SOLO" ou "TEAM"
    private String difficulty; // "SIMPLE" ou "PICANTE"
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
        // === SCIENTIFIC COMPETENCE (4 trios) ===
        // Programmation / Mathématiques / Veille Technologique (Coeff 12)
        Subject lo41 = new Subject("LO41", 12, true);
        Subject ap4a = new Subject("AP4A", 12, true);
        Subject si4a = new Subject("SI4A", 12, true);
        
        // Mathématiques / Mécanique / Programmation (Coeff 11)
        Subject mt2a = new Subject("MT2A", 11, true);
        Subject mt9a = new Subject("MT9A", 11, true);
        Subject mt4b = new Subject("MT4B", 11, true);
        
        // Mécanique / Expression & Argumentation / Mathématiques (Coeff 10)
        Subject mc2a = new Subject("MC2A", 10, true);
        Subject mc3a = new Subject("MC3A", 10, true);
        Subject mc4a = new Subject("MC4A", 10, true);
        
        // Expression & Argumentation / Gestion de Projet / Mécanique (Coeff 6)
        Subject ea1a = new Subject("EA1A", 6, true);
        Subject ea2b = new Subject("EA2B", 6, true);
        Subject ea3c = new Subject("EA3C", 6, true);

        // === MANAGEMENT COMPETENCE (4 trios) ===
        // Gestion de Projet / Marketing / Expression & Argumentation (Coeff 9)
        Subject ge1a = new Subject("GE1A", 9, true);
        Subject ge2b = new Subject("GE2B", 9, true);
        Subject ge3c = new Subject("GE3C", 9, true);
        
        // Marketing / Finance / Gestion de Projet (Coeff 8)
        Subject mk1a = new Subject("MK1A", 8, true);
        Subject mk2b = new Subject("MK2B", 8, true);
        Subject mk3c = new Subject("MK3C", 8, true);
        
        // Finance / Histoire de Techniques / Marketing (Coeff 5)
        Subject fi1a = new Subject("FI1A", 5, true);
        Subject fi2b = new Subject("FI2B", 5, true);
        Subject fi3c = new Subject("FI3C", 5, true);
        
        // Histoire des Techniques / Communication / Finance (Coeff 4)
        Subject ht01 = new Subject("HT01", 4, true);
        Subject ht02 = new Subject("HT02", 4, true);
        Subject ht03 = new Subject("HT03", 4, true);

        // === SOCIETAL COMPETENCE (4 trios) ===
        // Communication / Droit & Société / Veille Technologique (Coeff 3)
        Subject co1a = new Subject("CO1A", 3, true);
        Subject co2b = new Subject("CO2B", 3, true);
        Subject co3c = new Subject("CO3C", 3, true);
        
        // Droit & Société / Interculturel / Communication (Coeff 2)
        Subject dr01 = new Subject("DR01", 2, true);
        Subject dr02 = new Subject("DR02", 2, true);
        Subject dr03 = new Subject("DR03", 2, true);
        
        // Interculturel / Veille Technologique / Droit & Société (Coeff 1)
        Subject ic01 = new Subject("IC01", 1, true);
        Subject ic02 = new Subject("IC02", 1, true);
        Subject ic03 = new Subject("IC03", 1, true);
        
        // Veille Technologique / Programmation / Interculturel (Coeff 7)
        Subject vt01 = new Subject("VT01", 7, true);
        Subject vt02 = new Subject("VT02", 7, true);
        Subject vt03 = new Subject("VT03", 7, true);
        
        // Ajouter toutes les UEs à la liste principale
        this.allSubjects.addAll(List.of(
            // Scientific
            lo41, ap4a, si4a, mt2a, mt9a, mt4b, mc2a, mc3a, mc4a, ea1a, ea2b, ea3c,
            // Management
            ge1a, ge2b, ge3c, mk1a, mk2b, mk3c, fi1a, fi2b, fi3c, ht01, ht02, ht03,
            // Societal
            co1a, co2b, co3c, dr01, dr02, dr03, ic01, ic02, ic03, vt01, vt02, vt03
        ));
        
        // === CREATE TRIOS ===
        // Scientific Competence Trios
        createTrio("Trio-Coeff12-Scientific", lo41, ap4a, si4a);
        createTrio("Trio-Coeff11-Scientific", mt2a, mt9a, mt4b);
        createTrio("Trio-Coeff10-Scientific", mc2a, mc3a, mc4a);
        createTrio("Trio-Coeff6-Scientific", ea1a, ea2b, ea3c);
        
        // Management Competence Trios
        createTrio("Trio-Coeff9-Management", ge1a, ge2b, ge3c);
        createTrio("Trio-Coeff8-Management", mk1a, mk2b, mk3c);
        createTrio("Trio-Coeff5-Management", fi1a, fi2b, fi3c);
        createTrio("Trio-Coeff4-Management", ht01, ht02, ht03);
        
        // Societal Competence Trios
        createTrio("Trio-Coeff3-Societal", co1a, co2b, co3c);
        createTrio("Trio-Coeff2-Societal", dr01, dr02, dr03);
        createTrio("Trio-Coeff1-Societal", ic01, ic02, ic03);
        createTrio("Trio-Coeff7-Societal", vt01, vt02, vt03);
        
        
        // Lier chaque compétence à deux autres en topologie circulaire (voisins précédent/suivant)
        // Ainsi si A est lié à (B,C) alors B et C auront A dans leurs liens également
        int n = this.allCompetences.size();
        for (int i = 0; i < n; i++) {
            Competence c = this.allCompetences.get(i);
            Competence prev = this.allCompetences.get((i - 1 + n) % n);
            Competence next = this.allCompetences.get((i + 1) % n);
            c.setLinkedCompetences(prev, next);
        }
        
    }

    private void createTrio(String name, Subject s1, Subject s2, Subject s3) {
        Competence trio;
        if(name.contains("Scientific"))
            trio = new ScientificCompetence(name);
        else if (name.contains("Societal")) {
            trio = new SocietalCompetence(name);
        } else {//if (name.contains("Management")) {
            trio = new ManagementCompetence(name);
        }
        trio.addSubject(s1);
        trio.addSubject(s2);
        trio.addSubject(s3);
        this.allCompetences.add(trio);
    }
    

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

    /** Empty the center pile */
    public void emptyCenterPile() {
        this.centerPile = new ArrayList<>();
    }


    /** Getters */
    public List<Subject> getCenterPile() {
        return centerPile;
    }
    
    public List<Subject> getAllSubjects() {
        return allSubjects;
    }
    
    public List<Competence> getAllCompetences() {
        return allCompetences;
    }

    public String getMode() { return mode; }
    public String getDifficulty() { return difficulty; }

    public List<Competence> getValidatedCompetences(Student player) {
        return player.getCompetences();
    }
}
