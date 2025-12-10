package Trio;

import Trio.CompetencePackage.Competence;
import java.util.ArrayList;
import java.util.List;

public class Subject {
    private String id;
    private int number; // C'est le coefficient qui nous intéresse
    private boolean state;
    private List<Competence> competences = new ArrayList<>();

    public Subject(String id, int number, boolean state) {
        this.id = id;
        this.number = number;
        this.state = state;
    }

    // Getters et Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }

    public boolean isState() { return state; }
    public void setState(boolean state) { this.state = state; }

    public void addCompetence(Competence c) { competences.add(c); }
    public List<Competence> getCompetences() { return competences; }


    @Override
    public String toString() {
        return "Subject{" +
               "id=" + id +
               ", number=" + number +
               ", state=" + state +
               ", competences=" + competences +
               '}';
    }
}
