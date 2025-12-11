package Trio;

public class Team {
    public enum Color { RED, BLUE, GREEN, YELLOW }

    private String pseudo;
    private String color;

    public Team() {}

    public Team(String pseudo, String color) {
        this.pseudo = pseudo;
        this.color = color;
    }

    public String getPseudo() { return pseudo; }
    public void setPseudo(String pseudo) { this.pseudo = pseudo; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    @Override
    public String toString() {
        return "Team{" +
               "pseudo='" + pseudo + '\'' +
               ", color=" + color +
               '}';
    }
}
