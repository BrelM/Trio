package Trio.src.model.classes;

import Trio.src.competence.Competence;

public class Subject {
    private String id;
    private int credit; // C'est le coefficient qui nous intéresse
    private boolean state;
    private Competence competence;

    public Subject(String id, int credit, boolean state) {
        this.id = id;
        this.credit = credit;
        this.state = state;
    }

    // Getters et Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public int getCredit() { return credit; }
    public void setCredit(int credit) { this.credit = credit; }

    public boolean isState() { return state; }
    public void setState(boolean state) { this.state = state; }

    public void setCompetence(Competence c) { competence = c; }
    public Competence getCompetence() { return competence; }


    @Override
    public String toString() {
        return "Subject{" +
               "id=" + id +
               ", number=" + credit +
               ", state=" + state +
               ", competence=" + competence +
               '}';
    }
}
