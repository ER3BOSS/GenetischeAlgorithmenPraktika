import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;


public class GenerationHandler {

    private String sequence;
    private ArrayList<Kette> individuals = new ArrayList<>();
    private ImageCreator imageCreator = new ImageCreator();
    private int maxGenerations = 0;
    private int generationSize = 0;
    private int newBloodAmount = 0;

    public GenerationHandler(String sequence) {
        this.sequence = sequence;
    }


    void initializeGeneration(int generationSize) {
        for (int i = 0; i < generationSize; i++) {
            individuals.add(new Kette(sequence));
            individuals.get(i).generateByIntelligentRng();
        }
    }

    void evolve(int maxGenerations, int generationSize, int newBloodAmount){
        this.maxGenerations = maxGenerations;
        this.generationSize = generationSize;
        this.newBloodAmount = newBloodAmount;


        for (int generation = 0; generation < maxGenerations; generation++){

            //sort the list so the best individuals are on top (0 and 1)
            individuals.sort((Kette ketteA, Kette ketteB) -> Double.compare(ketteB.calcFitness(),ketteA.calcFitness()));
            individuals.subList(2, individuals.size()).clear(); // kill all but the 2 best

            makeSomeBabys();

            makeSomeMutants(generation);

            makeSomeNewBlood(generation);

            printAverageFitness(generation); //
        }

    }

    private void makeSomeBabys(){ //Todo refactor!!!
        //create 2 offspring's
        ArrayList<Integer> chromosomeA = ChromosomeHandler.extractChromosome(individuals.get(0).getPhenotype());
        ArrayList<Integer> chromosomeB = ChromosomeHandler.extractChromosome(individuals.get(1).getPhenotype());

        ArrayList<Integer> childA = ChromosomeHandler.crossoverChromosome(chromosomeA,chromosomeB);
        ArrayList<Integer> childB = ChromosomeHandler.crossoverChromosome(chromosomeB,chromosomeA);

        individuals.add(ChromosomeHandler.convertChromosome2NewGraph(childA, sequence));
        individuals.add(ChromosomeHandler.convertChromosome2NewGraph(childB, sequence));
    }

    private void makeSomeMutants(int generation){
        int initialPop = individuals.size();
        // fill the generationSize while leaving space for newBlood also no need to do that in the last gen
        while (individuals.size() < generationSize - newBloodAmount && generation != maxGenerations -1 ){
            int randomNum = ThreadLocalRandom.current().nextInt(0, initialPop);
            ArrayList<Integer> chromosomeMutant = ChromosomeHandler.extractChromosome(individuals.get(randomNum).getPhenotype());
            ArrayList<Integer> mutant = ChromosomeHandler.mutateChromosome(chromosomeMutant);
            individuals.add(ChromosomeHandler.convertChromosome2NewGraph(mutant, sequence));
        }
    }

    private void makeSomeNewBlood(int generation){
        while (individuals.size() < generationSize && generation != maxGenerations -1 ){
            individuals.add(new Kette(sequence));
            individuals.get(individuals.size()-1).generateByIntelligentRng();
        }
    }

    //todo write a actually good log -> as .txt
    private void printAverageFitness(int generation){
        double avr = 0;
        for (Kette kette : individuals){
            avr += kette.calcFitness();
        }
        avr = avr/individuals.size();
        System.out.println( generation + ". Average: " + avr);
    }

    void printResult() { //todo: move image creation somewhere else
        for (int i = 0; i < individuals.size(); i++){
            imageCreator.createImage(individuals.get(i).getPhenotype(), Integer.toString(i)+ ".png");
            System.out.println();
            individuals.get(i).printValues();
        }
    }

}
