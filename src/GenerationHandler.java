import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RefineryUtilities;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;


class GenerationHandler {

    private String sequence;
    private ArrayList<Kette> individuals = new ArrayList<>();
    private ImageCreator imageCreator = new ImageCreator();
    private RandomCollection<Kette> randomCollection = new RandomCollection<>();
    private int generation;
    private int generationSize = 0;
    private int newBloodAmount = 0;
    private DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    private GenerationLog log = new GenerationLog();
    private int selectionSize = 0;

    GenerationHandler(String sequence) throws IOException {
        this.sequence = sequence;
        createFolder();
        initializeGraph();
    }

    private void createFolder() throws IOException {
        //create folder
        String folder = "/ga";
        if (!new File(folder).exists()) {
            //noinspection ResultOfMethodCallIgnored
            new File(folder).mkdirs();
        } else if (new File("/ga/!Log.txt").exists()) {
            Path fileToDeletePath = Paths.get("/ga/!Log.txt");
            Files.delete(fileToDeletePath);
        }
    }

    private void initializeGraph() {
        //creation of the Graph
        LineChart chart = new LineChart(
                "Fitness Graph",
                "Live line graph showing the current progress",
                dataset
        );

        chart.pack();
        RefineryUtilities.centerFrameOnScreen(chart);
        chart.setVisible(true);
    }

    void initializeGeneration(int generationSize) {
        this.generationSize = generationSize;
        for (int i = 0; i < generationSize; i++) {
            individuals.add(new Kette(sequence));
            individuals.get(i).generateByRng();
        }
        log.saveGeneration(individuals);
        log.printLogTxt(generation, dataset);
    }

    void evolve(int maxGenerations, int newBloodAmount, double mutationRate, double crossoverRate, SelectType selectType, int breakCondition) {
        this.newBloodAmount = newBloodAmount;
        this.selectionSize = generationSize / 2;
        generation = 1;

        while (generation < maxGenerations && improving(breakCondition)) {

            selection(selectType);
            crossover(crossoverRate);
            mutation(mutationRate);
            makeSomeNewBlood();

            log.saveGeneration(individuals);
            log.printLogTxt(generation, dataset);

            //Warning: massive performance hit!!
            //createImageOfTheBestIn();

            generation ++;
        }
        log.saveGeneration(individuals);
        log.printLogTxt(generation, dataset);
        log.crateImageOfBestIndividual(sequence.length());
    }

    private boolean improving(int referenceGen){
        if (generation > referenceGen){
            double referenceAvrg = log.getAverageFitnessIn(generation - 1 - referenceGen);
            double currentAvrg = log.getAverageFitnessIn(generation - 1);
            return !(currentAvrg < referenceAvrg);
        }
        return true;
    }

    private void selection(SelectType selectType) {
        // selection Process
        switch (selectType){
            case FITNESS:
                fitnessBiasedSelection(selectionSize);
                break;
            case TOURNAMENT:
                tournamentSelection(4, selectionSize);
                break;
        }
    }

    private void crossover(double rate) {
        for (int i = 0; i < generationSize * rate; i++) {
            createChild();
        }
    }

    //Todo: make altering the mutation rate somewhat convenient
    private void mutation(double rate) {
        int initialPop = individuals.size();
        // fill the generationSize while leaving space for newBlood also no need to do that in the last gen
        while (individuals.size() < (generationSize - newBloodAmount)) {
            createMutant(rate, initialPop);
        }
    }

    private void createMutant(double rate, int initialPop) {
        int randomNum = ThreadLocalRandom.current().nextInt(0, initialPop);
        ArrayList<Integer> chromosomeMutant = ChromosomeHandler.extractChromosome(individuals.get(randomNum).getPhenotype());
        ArrayList<Integer> mutant = ChromosomeHandler.mutateChromosome(chromosomeMutant, rate);
        individuals.add(ChromosomeHandler.chromosome2phenotype(mutant, sequence));
    }

    private void makeSomeNewBlood() {
        while (individuals.size() < generationSize) {
            individuals.add(new Kette(sequence));
            individuals.get(individuals.size() - 1).generateByIntelligentRng();
            individuals.get(individuals.size() - 1).calcFitness();
        }
    }

    private void fitnessBiasedSelection(int selectionSize) { //Programm freezes if selection is bigger than generation Size
        generateRandomCollection();

        individuals.clear();

        for (int i = 0; i < selectionSize; i++) {
            individuals.add(randomCollection.next());
        }
        randomCollection.clear();
    }

    private void tournamentSelection(int tournamentSize, int numberOfTournaments) {
        ArrayList<Kette> champions = new ArrayList<>();
        Kette champion = new Kette("");
        for (int i = 0; i < numberOfTournaments; i++) {
            double bestFoundFitness = 0;
            for (int j = 0; j < tournamentSize; j++) {
                int random = ThreadLocalRandom.current().nextInt(0, individuals.size() - 1);
                Kette challenger = individuals.get(random);
                if (challenger.calcFitness() > bestFoundFitness) {
                    bestFoundFitness = challenger.calcFitness();
                    champion = challenger;
                }
            }
            champions.add(champion);
        }
        individuals.clear();

        individuals.addAll(champions);
    }

    // todo: make it create 1 or 2 children
    private void createChild() {
        int randA = ThreadLocalRandom.current().nextInt(0, selectionSize - 1);
        int randB = ThreadLocalRandom.current().nextInt(0, selectionSize - 1);

        //get Chromosome
        ArrayList<Integer> chromosomeA = ChromosomeHandler.extractChromosome(individuals.get(randA).getPhenotype());
        ArrayList<Integer> chromosomeB = ChromosomeHandler.extractChromosome(individuals.get(randB).getPhenotype());

        //Do the crossover
        ArrayList<Integer> child = ChromosomeHandler.crossoverChromosome(chromosomeA, chromosomeB);
        ArrayList<Integer> child2 = ChromosomeHandler.crossoverChromosome(chromosomeB, chromosomeA);

        //Save result
        individuals.add(ChromosomeHandler.chromosome2phenotype(child, sequence));
        individuals.add(ChromosomeHandler.chromosome2phenotype(child2, sequence));

    }

    private void generateRandomCollection() {
        double overallFitness = log.getSumOfFintessIn(this.generation - 1);
        double overallWeight = 0;
        for (Kette individual : individuals) {
            double weight = (individual.calcFitness() / overallFitness);
            weight = weight * 100;
            //overallWeight += weight;
            randomCollection.add(weight, individual);
        }
        //System.out.println("Overall weight in gen " + generation + " = " + overallWeight);
    }
    
    void drawResult(int top) { // top defines the best x you want the image of
        individuals.sort((Kette ketteA, Kette ketteB) -> Double.compare(ketteB.calcFitness(), ketteA.calcFitness()));
        for (int i = 0; i < top; i++) {
            imageCreator.createImage(individuals.get(i).getPhenotype(), Integer.toString(i) + ".png");
            System.out.println();
            individuals.get(i).printValues();
        }
    }

    private void createImageOfTheBestIn() {
        individuals.sort((Kette ketteA, Kette ketteB) -> Double.compare(ketteB.calcFitness(), ketteA.calcFitness()));
        imageCreator.createImage(individuals.get(0).getPhenotype(), "Generation_" + Integer.toString(this.generation) + ".png");
        //System.out.println();
        //individuals.get(0).printValues();
    }

}
