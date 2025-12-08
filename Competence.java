package Projet;

public class Competence {
    private String type;
    private int linkedNumber1;
    private int linkedNumber2;

    public Competence() {}

    public Competence(String type, int linkedNumber1, int linkedNumber2) {
        this.type = type;
        this.linkedNumber1 = linkedNumber1;
        this.linkedNumber2 = linkedNumber2;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getLinkedNumber1() { return linkedNumber1; }
    public void setLinkedNumber1(int linkedNumber1) { this.linkedNumber1 = linkedNumber1; }

    public int getLinkedNumber2() { return linkedNumber2; }
    public void setLinkedNumber2(int linkedNumber2) { this.linkedNumber2 = linkedNumber2; }

    @Override
    public String toString() {
        return "Competence{" +
               "type='" + type + '\'' +
               ", linkedNumber1=" + linkedNumber1 +
               ", linkedNumber2=" + linkedNumber2 +
               '}';
    }
}
