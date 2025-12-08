package Projet;

import java.util.ArrayList;
import java.util.List;

public class Subject {
    private int id;
    private int number;
    private boolean state;
    private List<Competence> competences = new ArrayList<>();

    public Subject() {}

    public Subject(int id, int number, boolean state) {
        this.id = id;
        this.number = number;
        this.state = state;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

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
