import java.util.ArrayList;
import java.util.List;

public class Log {
    private int generation = 0;
    private List<Double> fitnessList = new ArrayList<>();

    double getCurrentFitness(){
        return -1;
    }
}
