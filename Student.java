package Trio;

import java.util.ArrayList;
import java.util.List;
import Trio.CompetencePackage.*;

public class Student {
    private int id;
    private String pseudo;
    private String speciality;
    private Team team;
    private List<Subject> subjects = new ArrayList<>();
    private List<Competence> competences = new ArrayList<>();
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
