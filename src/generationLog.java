import java.util.ArrayList;
import java.util.List;

/*
    Generation, averageFitness for each generation, fitness of the best canidate in each generation,
    fitness, energy and overlap of the best overall canidate
*/

public class generationLog {
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

}
