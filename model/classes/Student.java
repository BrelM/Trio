package Trio.model.classes;

import Trio.competence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Student {
    // private int id;
    private String pseudo;
    private String speciality;
    private Team team;
    private List<Subject> subjects = new ArrayList<>(); //
    private List<Competence> competences= new ArrayList<>(); //competence valid (?)
    private List<Subject> validatedSubjects = new ArrayList<>();
    // private List<Game> parties = new ArrayList<>();

    public Student() {}

    public Student(int id, String pseudo, String speciality, Team team) {
        this.pseudo = pseudo;
        this.speciality = speciality;
        this.team = team;
    }

    public String getPseudo() { return pseudo; }
    public void setPseudo(String pseudo) { this.pseudo = pseudo; }

    public String getSpeciality() { return speciality; }
    public void setSpeciality(String speciality) { this.speciality = speciality; }

    public Team getTeam() { return team; }
    public void setTeam(Team team) { this.team = team; }

    public void addSubject(Subject s) { subjects.add(s); }
    public void addCompetence(Competence c) { competences.add(c); }
    // public void addPartie(Game p) { parties.add(p); }

    public List<Subject> getSubjects() { return subjects; }
    public List<Competence> getCompetences() { return competences; }
    // public List<Game> getParties() { return parties; }

    public void addSubjectToHand(Subject subject) {
        // Insert the subject so that the hand remains ordered by credit (ascending)
        int i = 0;
        while (i < subjects.size() && subjects.get(i).getCredit() <= subject.getCredit()) {
            i++;
        }
        subjects.add(i, subject);
    }


    public void emptyHand() {
        // Empty the hand of the student
        subjects = new ArrayList<>();
    }

    public void addValidatedSubject(Subject subject) {
        this.validatedSubjects.add(subject);
        if(!competences.contains(subject.getCompetence()))
            addValidatedCompetence(subject.getCompetence());
    }

    public void addValidatedCompetence(Competence comp) {
        this.competences.add(comp);
    }
    
    public boolean hasWon(){
        if(validatedSubjects.size() < 3){
            return false;
        }
        Map<Integer, List<Subject>> uesGroupedByCoeff = this.validatedSubjects.stream()
            .collect(Collectors.groupingBy(Subject::getCredit));
        
         // On parcourt ensuite cette map pour voir si l'un des groupes atteint la taille requise.
        for (List<Subject> group : uesGroupedByCoeff.values()) {
            if (group.size() >= 3) {
                // Si on trouve un groupe de 3 UEs ou plus avec le même coefficient, la condition de victoire est remplie.
                System.out.println("Condition de victoire remplie pour " + this.pseudo + " avec le coefficient " + group.getFirst().getCredit());
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
               ", pseudo='" + pseudo + '\'' +
               ", speciality='" + speciality + '\'' +
               ", team=" + team +
               '}';
    }
}
