import org.jfree.data.category.DefaultCategoryDataset;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/*
    Generation, averageFitness for each generation, fitness of the best canidate in each generation,
    fitness, energy and overlap of the best overall canidate
*/

class GenerationLog {
    private Kette bestIndividual = new Kette("");
    private List<Double> fitnessListCurrentGeneration = new ArrayList<>();
    private List<Double> generationsAverageFitness = new ArrayList<>();
    private List<Kette> generationsBestIndividual = new ArrayList<>();

    void saveGeneration(List<Kette> individuals) {
        fitnessListCurrentGeneration.clear();
        individuals.sort((Kette ketteA, Kette ketteB) -> Double.compare(ketteB.getFitness(),ketteA.getFitness()));
        generationsBestIndividual.add(individuals.get(0));
        if(this.bestIndividual.getFitness() < individuals.get(0).getFitness())
        this.bestIndividual = individuals.get(0);

        for (Kette individual : individuals) {
            fitnessListCurrentGeneration.add(individual.getFitness());
        }
        generationsAverageFitness.add(calcAverageFitness());
    }

    double getSumOfFintessIn(int generation){
        return generationsAverageFitness.get(generation) * fitnessListCurrentGeneration.size();
    }

    double getAverageFitnessIn(int generation) {
        return generationsAverageFitness.get(generation);
    }

    private Kette getGenerationsBestFitnessIn(int generation) {
        return generationsBestIndividual.get(generation);
    }

    private Double calcAverageFitness(){
        Double avr = 0.0;
        for (Double individual : fitnessListCurrentGeneration){
            avr += individual;
        }
        avr = avr/fitnessListCurrentGeneration.size();
        return avr;
    }

    void printLogTxt(int generation, DefaultCategoryDataset dataset){
        try (PrintWriter out = new PrintWriter(new FileWriter(new File("/ga" + File.separator +"!Log.txt"),true))) {
            out.print((generation + "," + getAverageFitnessIn(generation)) + "," +
                    getGenerationsBestFitnessIn(generation).getFitness() + "," + bestIndividual.getFitness() + "," +
                    bestIndividual.calcMinEnergy() + "," + bestIndividual.calcOverlap());

            out.print("\n");

            dataset.addValue(getGenerationsBestFitnessIn(generation).getFitness() , "current best", Integer.toString(generation));
            dataset.addValue(bestIndividual.getFitness() , "overall best" , Integer.toString(generation));
            dataset.addValue(getAverageFitnessIn(generation) , "average" , Integer.toString(generation));


        }catch (java.io.IOException e){
            System.out.println("Log file not found");
        }
        System.out.print(MessageFormat.format("Generation: {0} \t Average: {1} \t Best: {2} \t Energy: {3} \n", Integer.toString(generation),
                Double.toString(getAverageFitnessIn(generation)), Double.toString(getGenerationsBestFitnessIn(generation).getFitness()), Integer.toString(getGenerationsBestFitnessIn(generation).calcMinEnergy())));

    }

    void crateImageOfBestIndividual(int sequenzSize){
        ImageCreator imageCreator = new ImageCreator();
        imageCreator.createImage(
                bestIndividual.getPhenotype(),
                bestIndividual.getFitness(),
                bestIndividual.calcOverlap(),
                bestIndividual.calcMinEnergy(),
                "!BestIndividual_S" + sequenzSize + ".png");
        bestIndividual.printValues();
    }

    void getSumOfIndividualsWithOverlapp(List<Kette> individuals, int generation, DefaultCategoryDataset dataset){
        int counter = 0;
        for (Kette individual: individuals) {
            if(individual.calcOverlap() > 0)
                counter++;
        }
        dataset.addValue(counter / 1000, "Inividuals with Overlapps", Integer.toString(generation));
    }
}
