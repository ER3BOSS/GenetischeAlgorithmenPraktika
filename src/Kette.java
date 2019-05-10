import java.util.ArrayList;

class Kette {
    //Vars
    private String kette;
    private ArrayList<Node> phenotype = new ArrayList<>();
    //Todo: Atm every entity has its own random2DGenerator Obj that's kinda silly
    private Random2DGenerator random2DGenerator = new Random2DGenerator();
    //Todo: Implement the Chromosome here (that way it there is no need to calculate it every time)
    private double fitness;

    //Constructor
    Kette(String new_string) {
        kette = new_string;
        //could generate phenotype here
    }

    Kette(String new_string, ArrayList<Node> phenotype) {
        kette = new_string;
        this.phenotype = phenotype;
    }

    ArrayList<Node> getPhenotype() {
        return phenotype;
    }

    void generateByRngNoOverlap() {
        while (phenotype.size() == 0) // 0 means returned graph is invalid
            phenotype = random2DGenerator.generateRandomGraphNoOverlap(kette);

        fitness = calcFitness();
    }

    void generateByRng(){
        phenotype = random2DGenerator.generateRandomGraph(kette);
        fitness = calcFitness();
    }

    void generateByIntelligentRng(){
        phenotype = random2DGenerator.generateIntelligentRandomGraph(kette);
        fitness = calcFitness();
    }

    int calcMinEnergy() {
        int counter = 0;
        for (int i = 0; i < phenotype.size(); i++) {
            if (phenotype.get(i).getValue() == 1) {

                int i_x = phenotype.get(i).getX();
                int i_y = phenotype.get(i).getY();

                for (int j = 0; j < phenotype.size(); j++) {

                    if (Math.abs(i - j) > 1 && phenotype.get(j).getValue() == 1) { //not the same and both 1

                        int j_x = phenotype.get(j).getX();
                        int j_y = phenotype.get(j).getY();

                        if (Math.abs(i_x - j_x) == 1 && i_y == j_y) { // x is +-1 y the same
                            counter++;
                        } else if (Math.abs(i_y - j_y) == 1 && i_x == j_x) { // y is +-1 x the same
                            counter++;
                        }
                    }
                }
            }
        }
        return counter / 2; //every connection is listed 2 times (a to b and b to a)
    }

    int calcOverlap(){
        int counter = 0;
        for (int i = 0; i < phenotype.size(); i++) {
            int a_x = phenotype.get(i).getX();
            int a_y = phenotype.get(i).getY();

            for (int j = 0; j < phenotype.size(); j++) {
                int b_x = phenotype.get(j).getX();
                int b_y = phenotype.get(j).getY();

                int distance = getDistance(a_x, a_y, b_x, b_y);

                if (distance > 2){
                    j += distance - 1;
                }else if (a_x == b_x && a_y == b_y && phenotype.get(i) != phenotype.get(j)){
                    counter ++;
                }
            }
        }
        return counter/2;
    }

    private int getDistance(int a_x, int a_y, int b_x, int b_y) {
        int distanceX = flipIfNegativ(a_x - b_x);
        int distanceY = flipIfNegativ(a_y - b_y);
        return distanceX + distanceY;
    }

    private int flipIfNegativ(int number){
        return (number<0)?number*-1:number;
    }

    double calcFitness (){
        double countOfPairs = calcMinEnergy();
        double countOfOverlap = calcOverlap();
        return (((1 + countOfPairs)* 7.55) / ((1 + countOfOverlap) * 12.21));
    }

    void printValues(){
        System.out.println();
        System.out.println("Minimale Energie: " + calcMinEnergy());
        System.out.println("Overlap: " + calcOverlap());
        System.out.println("Fitness: " + calcFitness());
    }

    public double getFitness() {
        return fitness;
    }
}
