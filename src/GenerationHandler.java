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
    private double explorationFactor = 0;
    private ArrayList<Integer> challengerList = new ArrayList<>();
    private int maxGenerations = 0;


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
        this.maxGenerations = maxGenerations;
        this.newBloodAmount = newBloodAmount;
        generation = 1;

        long time = System.currentTimeMillis();
        System.out.println(time);
        while (generation < this.maxGenerations ) { //&& improving(breakCondition) && System.currentTimeMillis() < (time + 30000)

            selection(selectType);
            makeSomeNewBlood();
            crossover(crossoverRate);
            exploration();
            mutation(mutationRate);

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

    private void exploration() {
        if (generation > maxGenerations / 10) {
            //explorationFactor = Math.toRadians(generation % 90) / 1000;
            explorationFactor = Math.sin(generation/4) / 1000;
            System.out.println(explorationFactor);
        }
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
                fitnessBiasedSelection(generationSize - newBloodAmount);
                break;
            case TOURNAMENT:
                tournamentSelection(4, generationSize);
                break;
        }
    }

    private void crossover(double rate) {
        int one = -1;
        for (int i = 0; i < generationSize; i++) {
            double random = Math.random();
            if (one != -1) {
                createChild(one, i);
                one = -1;
            } else if (random <= rate) {
                one = i;
            }
        }
    }

    //Todo: make altering the mutation rate somewhat convenient
    private void mutation(double rate) {
        for (int i = 0; i < individuals.size(); i++){
            createMutant(rate + explorationFactor, i);
        }
    }

    private void createMutant(double rate, int target) {
        ArrayList<Integer> chromosomeMutant = ChromosomeHandler.extractChromosome(individuals.get(target).getPhenotype());
        ArrayList<Integer> mutant = ChromosomeHandler.mutateChromosome(chromosomeMutant, rate);
        individuals.set(target, ChromosomeHandler.chromosome2phenotype(mutant, sequence));
    }

    private void makeSomeNewBlood() {
        while (individuals.size() < generationSize) {
            individuals.add(new Kette(sequence));
            individuals.get(individuals.size() - 1).generateByIntelligentRng();
            individuals.get(individuals.size() - 1).calcFitness();
        }
    }

    // Program freezes if selection is bigger than generation Size
    private void fitnessBiasedSelection(int generationSize) {
        generateRandomCollection();

        individuals.clear();

        for (int i = 0; i < generationSize; i++) {
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
                int random = getNextChallenger(individuals.size());
                Kette challenger = individuals.get(random);
                if (challenger.getFitness() > bestFoundFitness) {
                    bestFoundFitness = challenger.getFitness();
                    champion = challenger;
                }
            }
            champions.add(champion);
            challengerList.clear();
        }
        individuals.clear();

        individuals.addAll(champions);
    }

    private int getNextChallenger(int size) {
        int selected;
        do {
            selected = ThreadLocalRandom.current().nextInt(0, size - 1);
        }while (isNotAllreadyChallenging(selected, challengerList));
        challengerList.add(selected);
        return selected;
    }

    private boolean isNotAllreadyChallenging(int selected, ArrayList<Integer> challengerList) {
        for (int challenger : challengerList){
            if (selected == challenger){
                return true;
            }
        }
        return false;
    }

    // todo: make it create 1 or 2 children
    private void createChild(int one, int two) {
        //get Chromosome
        ArrayList<Integer> chromosomeA = ChromosomeHandler.extractChromosome(individuals.get(one).getPhenotype());
        ArrayList<Integer> chromosomeB = ChromosomeHandler.extractChromosome(individuals.get(two).getPhenotype());

        //Do the crossover
        ArrayList<Integer> child = ChromosomeHandler.crossoverChromosome(chromosomeA, chromosomeB);
        ArrayList<Integer> child2 = ChromosomeHandler.crossoverChromosome(chromosomeB, chromosomeA);

        //Save result
        individuals.set(one, ChromosomeHandler.chromosome2phenotype(child, sequence));
        individuals.set(two, ChromosomeHandler.chromosome2phenotype(child2, sequence));

    }

    private void generateRandomCollection() {
        double overallFitness = log.getSumOfFintessIn(this.generation - 1);
        double overallWeight = 0;
        for (Kette individual : individuals) {
            double weight = (individual.getFitness() / overallFitness);
            weight = weight * 100;
            //overallWeight += weight;
            randomCollection.add(weight, individual);
        }
        //System.out.println("Overall weight in gen " + generation + " = " + overallWeight);
    }
    
    void drawResult(int top) { // top defines the best x you want the image of
        individuals.sort((Kette ketteA, Kette ketteB) -> Double.compare(ketteB.getFitness(), ketteA.getFitness()));
        for (int i = 0; i < top; i++) {
            imageCreator.createImage(individuals.get(i).getPhenotype(), individuals.get(i).getFitness(), individuals.get(i).calcOverlap(), individuals.get(i).calcMinEnergy(), i + ".png");
            System.out.println();
            //individuals.get(i).printValues();
        }
    }

    private void createImageOfTheBestIn() {
        individuals.sort((Kette ketteA, Kette ketteB) -> Double.compare(ketteB.getFitness(), ketteA.getFitness()));
        imageCreator.createImage(individuals.get(0).getPhenotype(), individuals.get(0).getFitness(), individuals.get(0).calcOverlap(), individuals.get(0).calcMinEnergy(), "Generation_" + this.generation + ".png");
        //System.out.println();
        //individuals.get(0).printValues();
    }

}
