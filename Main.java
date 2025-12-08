package Projet;

public class Main {
    public static void main(String[] args) {
        Team team = new Team("Alpha", Team.Color.BLUE);
        Student student = new Student(1, "JohnDoe", "Informatique", team);

        Subject subject = new Subject(101, 5, true);
        Competence comp = new Competence("Java", 10, 20);
        Partie partie = new Partie(Partie.Mode.SOLO, Partie.Difficulty.MEDIUM);

        // liens
        student.addSubject(subject);
        student.addCompetence(comp);
        student.addPartie(partie);
        subject.addCompetence(comp);

        System.out.println(student);
        System.out.println(subject);
        System.out.println(comp);
        System.out.println(partie);
    }
}
