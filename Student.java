package Trio;

import Trio.CompetencePackage.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Student {
    private int id;
    private String pseudo;
    private String speciality;
    private Team team;
    private List<Subject> subjects = new ArrayList<>(); //
    private List<Competence> competences= new ArrayList<>(); //competence valid (?)
    private List<Subject> validatedSubjects = new ArrayList<>();
    private List<Partie> parties = new ArrayList<>();

    public Student() {}

    public Student(int id, String pseudo, String speciality, Team team) {
        this.id = id;
        this.pseudo = pseudo;
        this.speciality = speciality;
        this.team = team;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPseudo() { return pseudo; }
    public void setPseudo(String pseudo) { this.pseudo = pseudo; }

    public String getSpeciality() { return speciality; }
    public void setSpeciality(String speciality) { this.speciality = speciality; }

    public Team getTeam() { return team; }
    public void setTeam(Team team) { this.team = team; }

    public void addSubject(Subject s) { subjects.add(s); }
    public void addCompetence(Competence c) { competences.add(c); }
    public void addPartie(Partie p) { parties.add(p); }

    public List<Subject> getSubjects() { return subjects; }
    public List<Competence> getCompetences() { return competences; }
    public List<Partie> getParties() { return parties; }

    public void addSubjectToHand(Subject subject) {
        this.subjects.add(subject);
    }

    public void addValidatedSubject(Subject subject) {
        this.validatedSubjects.add(subject);
    }
    
    public boolean hasWon(){
        if(validatedSubjects.size() < 3){
            return false;
        }
        Map<Integer, List<Subject>> uesGroupedByCoeff = this.validatedSubjects.stream()
            .collect(Collectors.groupingBy(Subject::getNumber));
        
         // On parcourt ensuite cette map pour voir si l'un des groupes atteint la taille requise.
        for (List<Subject> group : uesGroupedByCoeff.values()) {
            if (group.size() >= 3) {
                // Si on trouve un groupe de 3 UEs ou plus avec le même coefficient, la condition de victoire est remplie.
                System.out.println("Condition de victoire remplie pour " + this.pseudo + " avec le coefficient " + group.get(0).getNumber());
                return true;
            }
        }
        return false;
    }
    public List<Subject> getValidatedSubjects() {
        return validatedSubjects;
    }
    @Override
    public String toString() {
        return "Student{" +
               "id=" + id +
               ", pseudo='" + pseudo + '\'' +
               ", speciality='" + speciality + '\'' +
               ", team=" + team +
               '}';
    }
}
