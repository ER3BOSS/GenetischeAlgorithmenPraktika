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

public class GenerationLog {
    public Kette bestIndividual = new Kette("");
    private List<Double> fitnessListCurrentGeneration = new ArrayList<>();
    private List<Double> generationsAverageFitness = new ArrayList<>();
    private List<Double> generationsBestFitness = new ArrayList<>();

    public void saveGeneration(List<Kette> individuals) {
        fitnessListCurrentGeneration.clear();
        individuals.sort((Kette ketteA, Kette ketteB) -> Double.compare(ketteB.calcFitness(),ketteA.calcFitness()));
        generationsBestFitness.add(individuals.get(0).calcFitness());
        if(this.bestIndividual.calcFitness() < individuals.get(0).calcFitness())
        this.bestIndividual = individuals.get(0);

        for (Kette individual : individuals) {
            fitnessListCurrentGeneration.add(individual.calcFitness());
        }
        generationsAverageFitness.add(calcAverageFitness());
    }

    void addFitness(double fitness){

    }
    double getSumOfFintessIn(int generation){
        return generationsAverageFitness.get(generation) * fitnessListCurrentGeneration.size();
    }

    public double getAverageFitnessIn(int generation) {
        return generationsAverageFitness.get(generation);
    }

    public double getGenerationsBestFitnessIn(int generation) {
        return generationsBestFitness.get(generation);
    }

    private Double calcAverageFitness(){
        Double avr = 0.0;
        for (Double individual : fitnessListCurrentGeneration){
            avr += individual;
        }
        avr = avr/fitnessListCurrentGeneration.size();
        return avr;
    }

    public void printLogTxt(int generation, DefaultCategoryDataset dataset){
        try (PrintWriter out = new PrintWriter(new FileWriter(new File("/ga" + File.separator +"!Log.txt"),true))) {
            out.print((Integer.toString(generation) + "," + getAverageFitnessIn(generation)) + "," +
                    getGenerationsBestFitnessIn(generation) + "," + bestIndividual.calcFitness() + "," +
                    bestIndividual.calcMinEnergy() + "," + bestIndividual.calcOverlap());

            out.print("\n");

            dataset.addValue(getGenerationsBestFitnessIn(generation) , "current best" , Integer.toString(generation));
            dataset.addValue(bestIndividual.calcFitness() , "overall best" , Integer.toString(generation));
            dataset.addValue(getAverageFitnessIn(generation) , "average" , Integer.toString(generation));

        }catch (java.io.IOException e){
            System.out.println("Log file not found");
        }
        System.out.print(MessageFormat.format("Generation: {0} \t Average: {1} \t Best: {2} \n", Integer.toString(generation),
                Double.toString(getAverageFitnessIn(generation)), Double.toString(getGenerationsBestFitnessIn(generation))));

    }

    public void crateImageOfBestIndividual(){
        ImageCreator imageCreator = new ImageCreator();
        imageCreator.createImage(bestIndividual.getPhenotype(), "!BestIndividual" + ".png");
    }
}
