import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RefineryUtilities;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


public class GenerationHandler {

    private String sequence;
    private ArrayList<Kette> individuals = new ArrayList<>();
    private ImageCreator imageCreator = new ImageCreator();
    private RandomCollection<Kette> randomCollection = new RandomCollection<>();
    private int generation;
    private int maxGenerations = 0;
    private int generationSize = 0;
    private int newBloodAmount = 0;
    private Kette bestIndividual = new Kette("");
    private DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
    private GenerationLog log = new GenerationLog();
    private JFrame frame = new JFrame();

    public GenerationHandler(String sequence) throws IOException {
        this.sequence = sequence;

        //create folder
        String folder = "/ga";
        if (!new File(folder).exists()) {
            //noinspection ResultOfMethodCallIgnored
            new File(folder).mkdirs();
        }
        else if(new File("/ga/!Log.txt").exists()){
            Path fileToDeletePath = Paths.get("/ga/!Log.txt");
            Files.delete(fileToDeletePath);
        }

        //creation of the Graph
        LineChart chart = new LineChart(
                "Fitness Graph" ,
                "Live line graph showing the current progress",
                dataset);

        chart.pack( );
        RefineryUtilities.centerFrameOnScreen( chart );
        chart.setVisible( true );
    }


    void initializeGeneration(int generationSize) {
        this.generationSize = generationSize;
        for (int i = 0; i < generationSize; i++) {
            individuals.add(new Kette(sequence));
            individuals.get(i).generateByRng();
        }
        log.saveGeneration(individuals);
        printLogTxt();
    }

    void evolve(int maxGenerations, int newBloodAmount){
        this.maxGenerations = maxGenerations;
        this.generationSize = generationSize;
        this.newBloodAmount = newBloodAmount;


        for (this.generation = 1; this.generation < this.maxGenerations; this.generation++){

            // selection Process
            //fitnessBiasedSelection(generationSize);
            tournamentSelection(5,500);


            //individuals.sort((Kette ketteA, Kette ketteB) -> Double.compare(ketteB.calcFitness(),ketteA.calcFitness()));
            log.saveGeneration(individuals);
            printLogTxt();

            // mutation, crossover etc.
            if (this.generation != this.maxGenerations -1) { // if not the last generation
                //crossover(0.25); //Broken!
                mutation(1);
                                //makeSomeNewBlood(generation);
                shitCleanUp();
            }else{ //if its the last generation
                //individuals.subList(5, individuals.size()).clear(); // kill all but the x best
            }

            //Warning: massive performance hit!!
            //createImageOfTheBestIn();
        }
        log.saveGeneration(individuals);
        printLogTxt();
    }


    // https://www.youtube.com/watch?v=9JzFcGdpT8E
    private int fitnessBiasedSelection() {
        Random rng = new Random();
        double rand = log.getSumOfFintessIn(this.generation) * rng.nextDouble();
        double partialSum = 0;
        for (int x = generationSize - 1; x >= 0; x--) {
            partialSum += individuals.get(x).getFitness();
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
        int counter = 0;
        while(individuals.size() != generationSize){
            individuals.remove(counter);
        counter++;
        }
    }

    private void crossover(double rate){ //Todo refactor!!!
        //create 2 offspring's
        for (int i = 0; i < ((generationSize * rate/2)); i++) {
            ArrayList<Integer> chromosomeA = ChromosomeHandler.extractChromosome(individuals.get(fitnessBiasedSelection()).getPhenotype());
            ArrayList<Integer> chromosomeB = ChromosomeHandler.extractChromosome(individuals.get(fitnessBiasedSelection()).getPhenotype());

            ArrayList<Integer> childA = ChromosomeHandler.crossoverChromosome(chromosomeA, chromosomeB);
            ArrayList<Integer> childB = ChromosomeHandler.crossoverChromosome(chromosomeB, chromosomeA);

            individuals.add(ChromosomeHandler.chromosome2phenotype(childA, sequence));
            individuals.add(ChromosomeHandler.chromosome2phenotype(childB, sequence));
        }
    }

    private void fitnessBiasedSelection(int selectionSize){ //Programm freezes if selection is bigger than generation Size
        generateRandomCollection();

        individuals.clear();

        for (int i = 0; i < selectionSize; i++){
            individuals.add(randomCollection.next());
        }
    }

    private void tournamentSelection(int tournamentSize, int numberOfTournaments){
        ArrayList<Kette> champions = new ArrayList<>();
        Kette champion = new Kette("");
        for (int i = 0; i < numberOfTournaments; i++) {
            double bestFoundFitness = 0;
            for (int j = 0; j < tournamentSize; j++) {
                int random = getRandomIntInRange(0,individuals.size()-1);
                Kette challenger = individuals.get(random);
                if (challenger.calcFitness() > bestFoundFitness){
                    bestFoundFitness = challenger.calcFitness();
                    champion = challenger;
                }
            }
            champions.add(champion);
        }
        individuals.clear();

        individuals.addAll(champions);
    }

    private void generateRandomCollection() {
        double overallFitness = log.getSumOfFintessIn(this.generation -1);
        for (Kette individual : individuals){
            double weight = (individual.calcFitness()/overallFitness);
            weight = weight*100;
            randomCollection.add(weight,individual);
        }
    }

    private void makeSomeBabys(){ //Todo: remake function with crossover chance value
        //create 2 offspring's
        ArrayList<Integer> chromosomeA = ChromosomeHandler.extractChromosome(individuals.get(0).getPhenotype());
        ArrayList<Integer> chromosomeB = ChromosomeHandler.extractChromosome(individuals.get(1).getPhenotype());

        ArrayList<Integer> childA = ChromosomeHandler.crossoverChromosome(chromosomeA,chromosomeB);
        ArrayList<Integer> childB = ChromosomeHandler.crossoverChromosome(chromosomeB,chromosomeA);

        individuals.add(ChromosomeHandler.chromosome2phenotype(childA, sequence));
        individuals.add(ChromosomeHandler.chromosome2phenotype(childB, sequence));
    }

    //Todo: make altering the mutation rate somewhat convenient
    private void mutation(double rate){
        int initialPop = individuals.size();
        // fill the generationSize while leaving space for newBlood also no need to do that in the last gen
        while (individuals.size() < (generationSize * rate - newBloodAmount) + generationSize){
            int randomNum = ThreadLocalRandom.current().nextInt(0, initialPop);
            ArrayList<Integer> chromosomeMutant = ChromosomeHandler.extractChromosome(individuals.get(randomNum).getPhenotype());
            ArrayList<Integer> mutant = ChromosomeHandler.mutateChromosome(chromosomeMutant, 0.1);
            individuals.add(ChromosomeHandler.chromosome2phenotype(mutant, sequence));
        }
    }

    private void makeSomeNewBlood(){
        while (individuals.size() < generationSize){
            individuals.add(new Kette(sequence));
            individuals.get(individuals.size()-1).generateByRng();
        }
    }

    private double calcOverallFitness(){
        double avr = 0;
        for (Kette kette : individuals){
            avr += kette.calcFitness();
        }
        //avr = avr/individuals.size();
        return avr;
    }

    void drawResult(int top) { // top defines the best x you want the image of
        individuals.sort((Kette ketteA, Kette ketteB) -> Double.compare(ketteB.calcFitness(),ketteA.calcFitness()));
        for (int i = 0; i < top; i++){
            imageCreator.createImage(individuals.get(i).getPhenotype(), Integer.toString(i)+ ".png");
            System.out.println();
            individuals.get(i).printValues();
        }
    }

    private void printLogTxt(){
        try (PrintWriter out = new PrintWriter(new FileWriter(new File("/ga" + File.separator +"!Log.txt"),true))) {
            out.print((Integer.toString(this.generation) + "," + log.getAverageFitnessIn(this.generation)) + "," +
                    log.getGenerationsBestFitnessIn(this.generation) + "," + log.bestIndividual.calcFitness() + "," +
                    log.bestIndividual.calcMinEnergy() + "," + log.bestIndividual.calcOverlap());

            out.print("\n");

            dataset.addValue( log.getGenerationsBestFitnessIn(this.generation) , "current best" , Integer.toString(generation) );
            dataset.addValue( log.bestIndividual.calcFitness() , "overall best" , Integer.toString(generation) );
            dataset.addValue( log.getAverageFitnessIn(this.generation) , "average" , Integer.toString(generation) );

        }catch (java.io.IOException e){
            System.out.println("Log file not found");
        }
        System.out.print(MessageFormat.format("Generation: {0} \t Average: {1} \t Best: {2} \n", Integer.toString(this.generation),
                Double.toString(log.getAverageFitnessIn(this.generation)), Double.toString(log.getGenerationsBestFitnessIn(this.generation))));

    }

    private static int getRandomIntInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }


    private void createImageOfTheBestIn(){
        individuals.sort((Kette ketteA, Kette ketteB) -> Double.compare(ketteB.calcFitness(),ketteA.calcFitness()));
        imageCreator.createImage(individuals.get(0).getPhenotype(), "Generation_" + Integer.toString(this.generation)+ ".png");
        //System.out.println();
        //individuals.get(0).printValues();
    }

}
