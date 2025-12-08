package Trio;

public class Partie {
    public enum Mode { SOLO, MULTI }
    public enum Difficulty { EASY, MEDIUM, HARD }

    private Mode mode;
    private Difficulty difficulty;

    public Partie() {}

    public Partie(Mode mode, Difficulty difficulty) {
        this.mode = mode;
        this.difficulty = difficulty;
    }

    public Mode getMode() { return mode; }
    public void setMode(Mode mode) { this.mode = mode; }

    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }

    @Override
    public String toString() {
        return "Partie{" + "mode=" + mode + ", difficulty=" + difficulty + '}';
    }
}
