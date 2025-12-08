package Projet;

public class Competence {
    private String type;
    private int linkedUE1;
    private int linkedUE2;

    public Competence() {}

    public Competence(String type, int linkedUE, int linkedUE2) {
        this.type = type;
        this.linkedUE1 = linkedUE1;
        this.linkedUE2 = linkedUE2;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getLinkedUE1() { return linkedUE1; }
    public void setLinkedUE1(int linkedUE1) { this.linkedUE1 = linkedUE1; }

    public int getLinkedUE2() { return linkedUE2; }
    public void setLinkedUE2(int linkedUE2) { this.linkedUE2 = linkedUE2; }

    @Override
    public String toString() {
        return "Competence{" +
               "type='" + type + '\'' +
               ", linkedUE1=" + linkedUE1 +
               ", linkedUE2=" + linkedUE2 +
               '}';
    }
}
