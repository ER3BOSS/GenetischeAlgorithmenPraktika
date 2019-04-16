import java.util.ArrayList;

public class ChromosomeHandler {

    static private double mutationRate = 0.05;

    public ChromosomeHandler() {
    }

    static Kette convertChromosome2NewGraph(ArrayList<Integer> chromosome, String sequence){
        int x = 0;
        int y = 0;
        ArrayList<Node> kette2d = new ArrayList<>();
        kette2d.add(new Node(0, 0, Character.getNumericValue(sequence.charAt(0))));

        for (int i = 1; i < chromosome.size(); i++){
            int gene = chromosome.get(i);
            switch (gene) {
                case 0: //up
                    y += 1;
                    kette2d.add(new Node(x,y,Character.getNumericValue(sequence.charAt(i))));
                    break;
                case 1: //down
                    y -= 1;
                    kette2d.add(new Node(x,y,Character.getNumericValue(sequence.charAt(i))));
                    break;
                case 2: //left
                    x -= 1;
                    kette2d.add(new Node(x,y,Character.getNumericValue(sequence.charAt(i))));
                    break;
                case 3: //right
                    x += 1;
                    kette2d.add(new Node(x,y,Character.getNumericValue(sequence.charAt(i))));
                    break;
            }
        }

        return new Kette(sequence, kette2d, chromosome);
    }

    static ArrayList<Integer> extractChromosome(ArrayList<Node> kette2d){
        ArrayList<Integer> chromosome = new ArrayList<>();

        for(Node node : kette2d){
            chromosome.add(node.getGene());
        }
        return chromosome;
    }

    static void printChromosome (ArrayList<Integer> chromosome){
        System.out.println();
        System.out.println("### Chromosome ###");
        System.out.println();
        for (Integer gene : chromosome) {
            System.out.print(gene);
        }
    }

    static ArrayList<Integer> crossoverChromosome(ArrayList<Integer> chromosomeA, ArrayList<Integer> chromosomeB){
        ArrayList<Integer> crossover = new ArrayList<>();
        double random = Math.random();
        random = Math.ceil(chromosomeA.size()*random);
        for (int i = 0; i < chromosomeA.size(); i++){
            if (i < random){
                crossover.add(chromosomeA.get(i));
            }else{
                crossover.add(chromosomeB.get(i));
            }
        }

        return crossover;
    }

    static ArrayList<Integer> mutateChromosome(ArrayList<Integer> chromosome){
        for (int i = 0; i < chromosome.size(); i++) {
            double random = Math.random();
            if (random <= mutationRate){
                if (random < mutationRate/2){
                    int mutated = (chromosome.get(i) - 1) % 4;
                    if (mutated == -1){ //for some reason -1 is possible with % in java
                        mutated = 3;
                    }
                    chromosome.set(i, mutated);
                }else {
                    int mutated = (chromosome.get(i) + 2) % 4;
                    chromosome.set(i,mutated);
                }
            }
        }
        return chromosome;
    }
}
