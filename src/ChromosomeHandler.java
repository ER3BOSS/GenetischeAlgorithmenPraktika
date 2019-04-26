import java.util.ArrayList;

public class ChromosomeHandler {

    public ChromosomeHandler() {
    }

    static Kette chromosome2phenotype(ArrayList<Integer> chromosome, String sequence){
        int x = 0;
        int y = 0;
        ArrayList<Node> phenotype = new ArrayList<>();
        phenotype.add(new Node(0, 0, Character.getNumericValue(sequence.charAt(0))));

        for (int i = 1; i < chromosome.size(); i++){
            int gene = chromosome.get(i);
            switch (gene) {
                case 0: //up
                    y += 1;
                    phenotype.add(new Node(x,y,Character.getNumericValue(sequence.charAt(i)), gene));
                    break;
                case 1: //down
                    y -= 1;
                    phenotype.add(new Node(x,y,Character.getNumericValue(sequence.charAt(i)), gene));
                    break;
                case 2: //left
                    x -= 1;
                    phenotype.add(new Node(x,y,Character.getNumericValue(sequence.charAt(i)), gene));
                    break;
                case 3: //right
                    x += 1;
                    phenotype.add(new Node(x,y,Character.getNumericValue(sequence.charAt(i)), gene));
                    break;
            }
        }

        return new Kette(sequence, phenotype);
    }

    static ArrayList<Integer> extractChromosome(ArrayList<Node> phenotype){
        ArrayList<Integer> chromosome = new ArrayList<>();

        for(Node node : phenotype){
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

        for (int i = 0; i < chromosomeA.size(); i++){
            if (i < chromosomeA.size()/2){
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
            double mutationRate = 0.05;
            if (random <= mutationRate){
                if (random < .50){
                    int mutated = Math.floorMod((chromosome.get(i) - 1),4);
                    chromosome.set(i, mutated);
                }else {
                    int mutated = Math.floorMod((chromosome.get(i) + 2), 4);
                    chromosome.set(i, mutated);
                }
            }
        }
        return chromosome;
    }
}
