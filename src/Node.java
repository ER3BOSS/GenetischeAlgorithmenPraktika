public class Node {

    private int x;
    private int y;
    private int value;
    private int gene;

    public Node(int x, int y, int value) {
        this.x = x;
        this.y = y;
        this.value = value;
    }

    public Node(int x, int y, int value, int gene) {
        this.x = x;
        this.y = y;
        this.value = value;
        this.gene = gene;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setXY(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getValue() {
        return value;
    }

    public int getGene() {
        return gene;
    }

    public void setGene(int gene) {
        this.gene = gene;
    }
}
