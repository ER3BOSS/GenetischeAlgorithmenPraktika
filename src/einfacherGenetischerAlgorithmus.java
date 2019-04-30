import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

class einfacherGenetischerAlgorithmus {

    private String sequence;
    private int generationSize = 100;
    private ArrayList<Kette> individuals = new ArrayList<>();
    // todo: Fitness class with average, highest, sum and maybe more
    private double fitness = 0;
    private ImageCreator imageCreator = new ImageCreator();
    private Kette bestIndividual;

    einfacherGenetischerAlgorithmus(String sequence) {
        this.sequence = sequence;
    }

    void einfacherGenetischerAlgorithmus(int maxGeneration) {
        int generation = 0;
        initializeGeneration(generationSize);
        fitness = evaluateFitnessofGeneration(generation);
        bestIndividual = individuals.get(0);
        printLogTxt(generation);
        // todo: Fitness class and evaluate weather f needs to be the max or average
        while (fitness < 1000 && generation < maxGeneration) {
            generation++;
            selection();
            crossover();
            mutation();
            fitness = evaluateFitnessofGeneration(generation);
            printLogTxt(generation);
            printBestOfGeneration(generation);
        }
        createImages();
    }

    private void initializeGeneration(int generationSize) {
        for (int i = 0; i < generationSize; i++) {
            individuals.add(new Kette(sequence));
            individuals.get(i).generateByIntelligentRng();
        }
    }

    //todo write a actually good log -> as .txt
    private double evaluateFitnessofGeneration(int generation){
        double sum = 0;
        for (Kette kette : individuals){
            sum += kette.calcFitness();
        }
        double out = sum/individuals.size();
        System.out.println( generation + ". Average: " + out + "\t Sum:" + sum);
        return sum;
    }
    // https://www.youtube.com/watch?v=9JzFcGdpT8E
    private int fitnessBiasedSelection() {
        Random rng = new Random();
        double rand = fitness * rng.nextDouble();
        double partialSum = 0;
        for (int x = generationSize - 1; x >= 0; x--) {
            partialSum += individuals.get(x).calcFitness();
            if (partialSum >= rand) {
                return x;
            }
        }
        return -1;
    }

    private void selection(){
        for(int i = 0; i < generationSize; i++){
            individuals.add(individuals.get(fitnessBiasedSelection()));
        }
        shitCleanUp();
    }

    private void shitCleanUp(){
        for(int j = 0; j < generationSize; j++){
            individuals.remove(j);
        }
    }

    private void crossover(){ //Todo refactor!!!
        //create 2 offspring's
        for (int i = 0; i < generationSize / 8; i++) {
            ArrayList<Integer> chromosomeA = extractChromosome(individuals.get(fitnessBiasedSelection()).getPhenotype());
            ArrayList<Integer> chromosomeB = extractChromosome(individuals.get(fitnessBiasedSelection()).getPhenotype());

            ArrayList<Integer> childA = crossoverChromosome(chromosomeA, chromosomeB);
            ArrayList<Integer> childB = crossoverChromosome(chromosomeB, chromosomeA);

            individuals.add(chromosome2phenotype(childA, sequence));
            individuals.add(chromosome2phenotype(childB, sequence));
        }
    }

    private void mutation(){
        int initialPop = individuals.size();
        while (individuals.size() < generationSize * 2){
            int randomNum = ThreadLocalRandom.current().nextInt(0, initialPop);
            ArrayList<Integer> chromosomeMutant = extractChromosome(individuals.get(randomNum).getPhenotype());
            ArrayList<Integer> mutant = mutateChromosome(chromosomeMutant, 0.05);
            individuals.add(chromosome2phenotype(mutant, sequence));
        }
        shitCleanUp();
    }
/*
Generationsnummer, durchschnittliche Fitness in dieser Generation, Fitness des besten
Lösungskandidaten in dieser Generation, Fitness des besten bisher gefundenen Lösungskandidaten,
Anzahl der hydrophob/hydrophob-Kontakte des besten bisher gefunden Lösungskandidaten, Anzahl
der Überlappungen im besten bisher gefundenen Lösungskandidaten
 */
    private void printLogTxt(int generation){
        individuals.sort((Kette ketteA, Kette ketteB) -> Double.compare(ketteB.calcFitness(),ketteA.calcFitness()));
        if(bestIndividual.calcFitness() < individuals.get(0).calcFitness())
            bestIndividual = individuals.get(0);
        //create folder
        String folder = "/ga";
        if (!new File(folder).exists()) {
            //noinspection ResultOfMethodCallIgnored
            new File(folder).mkdirs();
        }
        try (PrintWriter out = new PrintWriter(new FileWriter(new File("/ga" + File.separator +"Log.txt"),true))) {
                out.print((Integer.toString(generation) + "," + fitness / generationSize) + "," +
                        individuals.get(0).calcFitness() + "," + bestIndividual.calcFitness() + "," +
                        bestIndividual.calcMinEnergie() + "," + bestIndividual.calcOverlap());

            out.print("\n");
        }catch (java.io.IOException e){
            System.out.println("Log file not found");
        }
    }

    private void createImages() {
        for (int i = 0; i < 10; i++){
            imageCreator.createImage(individuals.get(i).getPhenotype(), i + ".png");
            System.out.println();
            individuals.get(i).printValues();
        }
    }

    private void printBestOfGeneration(int generation){

        individuals.sort((Kette ketteA, Kette ketteB) -> Double.compare(ketteB.calcFitness(),ketteA.calcFitness()));
            imageCreator.createImage(individuals.get(0).getPhenotype(), "Generation_" + Integer.toString(generation)+ ".png");
            System.out.println();
            //individuals.get(0).printValues();
        }


    private static Kette chromosome2phenotype(ArrayList<Integer> chromosome, String sequence){
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

    private static ArrayList<Integer> extractChromosome(ArrayList<Node> phenotype){
        ArrayList<Integer> chromosome = new ArrayList<>();

        for(Node node : phenotype){
            chromosome.add(node.getGene());
        }
        return chromosome;
    }

    private static ArrayList<Integer> crossoverChromosome(ArrayList<Integer> chromosomeA, ArrayList<Integer> chromosomeB){
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

    private static ArrayList<Integer> mutateChromosome(ArrayList<Integer> chromosome, double mutationRate){
        for (int i = 0; i < chromosome.size(); i++) {
            double random = Math.random();
            if (random <= mutationRate){
                if (random < mutationRate/2){
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
