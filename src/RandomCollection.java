import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

class RandomCollection<E> {
    private final NavigableMap<Double, E> map = new TreeMap<>();
    private final Random random;
    private double total = 0;

    RandomCollection() {
        this(new Random());
    }

    private RandomCollection(Random random) {
        this.random = random;
    }

    void add(double weight, E result) {
        if (weight <= 0) return;
        total += weight;
        map.put(total, result);
    }

    E next() {
        double value;
        do {
            value = random.nextDouble() * total;
        } while (map.higherEntry(value) == null);
        return map.remove(map.higherEntry(value).getKey());
        //return map.higherEntry(value).getValue();
    }

    void clear() {
        map.clear();
        total = 0;
    }
}