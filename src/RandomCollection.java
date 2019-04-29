import java.util.NavigableMap;
import java.util.Objects;
import java.util.Random;
import java.util.TreeMap;

// see https://stackoverflow.com/questions/6409652/random-weighted-selection-in-java

public class RandomCollection<E> {
    private final NavigableMap<Double, E> map = new TreeMap<Double, E>();
    private final Random random;
    private double total = 0;

    public RandomCollection() {
        this(new Random());
    }

    public RandomCollection(Random random) {
        this.random = random;
    }

    public RandomCollection<E> add(double weight, E result) {
        if (weight <= 0) return this;
        total += weight;
        map.put(total, result);
        return this;
    }

    public E next() {
        double value;
        int count = 0;
        do {
            value = random.nextDouble() * total;
            count ++;
        } while (map.higherEntry(value) == null);
        //return map.remove(map.higherEntry(value).getKey());
        return map.higherEntry(value).getValue();
    }
}