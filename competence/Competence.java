package Trio.competence;

import Trio.model.classes.Subject;
import java.util.ArrayList;
import java.util.List;

public class Competence {
    private String name;
    private List<Competence> linkedCompetences; // Uniquement 2 compétences
    private List<Subject> subjects = new ArrayList<>(); // Uniquement 3 sujets

    public Competence(String name) {
        this.name = name;
    }

    public void addSubject(Subject subject) {
        if (this.subjects.size() < 3) {
            subject.setCompetence(this);
            this.subjects.add(subject);
        }
    }

    public void setLinkedCompetences(List<Competence> competences) { this.linkedCompetences = competences; }
    public void setLinkedCompetences(Competence comp1, Competence comp2) { this.linkedCompetences = new ArrayList<Competence> (List.of(comp1, comp2)); }

    public String getName() { return name; }
    public List<Subject> getSubjects() { return subjects; }
    public List<Competence> getLinkedCompetences() { return linkedCompetences; }

    // Une méthode pratique pour vérifier si la compétence est complète (un Trio)
    public boolean isComplete() {
        return this.subjects.size() == 3;
    }

    @Override
    public String toString() {
        return "Compétence " + name + " (contient " + subjects.size() + " UEs) et liée aux compétences: "
                + linkedCompetences.getFirst().getName() + ", " + linkedCompetences.get(1).getName();
    }
}
