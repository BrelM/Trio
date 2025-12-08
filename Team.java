package Trio;

public class Team {
    public enum Color { RED, BLUE, GREEN, YELLOW }

    private String pseudo;
    private Color color;

    public Team() {}

    public Team(String pseudo, Color color) {
        this.pseudo = pseudo;
        this.color = color;
    }

    public String getPseudo() { return pseudo; }
    public void setPseudo(String pseudo) { this.pseudo = pseudo; }

    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }

    @Override
    public String toString() {
        return "Team{" +
               "pseudo='" + pseudo + '\'' +
               ", color=" + color +
               '}';
    }
}
