import java.util.ArrayList;

class Kette {
    //Vars
    private String kette;
    private ArrayList<Node> phenotype = new ArrayList<>();
    //Todo: Atm every entity has its own random2DGenerator Obj that's kinda silly
    private Random2DGenerator random2DGenerator = new Random2DGenerator();
    //Todo: Implement the Chromosome here (that way it there is no need to calculate it every time)

    //Constructor
    Kette(String new_string) {
        kette = new_string;
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
    }

    void generateByRng(){
        phenotype = random2DGenerator.generateRandomGraph(kette);
    }

    void generateByIntelligentRng(){
        phenotype = random2DGenerator.generateIntelligentRandomGraph(kette);
    }

    private int calcMinEnergie() {
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

    private int calcOverlap(){
        int counter = 0;
        for (Node nodeA : phenotype){
            int a_x = nodeA.getX();
            int a_y = nodeA.getY();

            for (Node nodeB : phenotype){
                int b_x = nodeB.getX();
                int b_y = nodeB.getY();

                if (a_x == b_x && a_y == b_y && nodeA != nodeB){
                    counter ++;
                }
            }
        }
        return counter / 2;
    }

    double calcFitness (){
        double countOfPairs = calcMinEnergie();
        double countOfOverlap = calcOverlap();
        return ((1 + countOfPairs) / ((1 + countOfOverlap*4) * 10));
    }

    void printValues(){
        System.out.println();
        System.out.println("Minimale Energie: " + calcMinEnergie());
        System.out.println("Overlap: " + calcOverlap());
        System.out.println("Fitness: " + calcFitness());
    }

}
