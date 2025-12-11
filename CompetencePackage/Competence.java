package Trio.CompetencePackage;

import Trio.Subject;
import java.util.ArrayList;
import java.util.List;

public class Competence {
    private String name;
    private List<Subject> linkedSubjects = new ArrayList<>();

    public Competence(String name) {
        this.name = name;
    }

    public void addLinkedSubject(Subject subject) {
        if (this.linkedSubjects.size() < 3) {
            this.linkedSubjects.add(subject);
        }
    }

    public String getName() { return name; }
    public List<Subject> getLinkedSubjects() { return linkedSubjects; }
    
    // Une méthode pratique pour vérifier si la compétence est complète (un Trio)
    public boolean isComplete() {
        return this.linkedSubjects.size() == 3;
    }

    @Override
    public String toString() {
        return "Competence " + name + " (liée à " + linkedSubjects.size() + " UEs)";
    }
}
