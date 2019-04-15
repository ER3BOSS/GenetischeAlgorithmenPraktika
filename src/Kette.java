import java.util.ArrayList;
import java.util.Random;

class Kette {
    //Vars
    private String kette;
    private ArrayList<Node> kette2d = new ArrayList<>();

    //Constructor
    Kette(String new_string) {
        kette = new_string;
    }

    ArrayList<Node> getKette2d() {
        return kette2d;
    }

    void generateByRNG() {
        while (kette2d.size() == 0) // 0 means returned graph is invalid
            kette2d = Random2DGenerator.generateRandomGraph(kette);
    }

    int calcMinEnergie() {
        int counter = 0;
        for (int i = 0; i < kette2d.size(); i++) {
            if (kette2d.get(i).getValue() == 1) {

                int i_x = kette2d.get(i).getX();
                int i_y = kette2d.get(i).getY();

                for (int j = 0; j < kette2d.size(); j++) {

                    if (Math.abs(i - j) > 1 && kette2d.get(j).getValue() == 1) { //not the same and both 1

                        int j_x = kette2d.get(j).getX();
                        int j_y = kette2d.get(j).getY();

                        if (Math.abs(i_x - j_x) == 1 && i_y == j_y) { // x is +-1 y the same
                            counter++;
                        } else if (Math.abs(i_y - j_y) == 1 && i_x == j_x) { // y is +-1 x the same
                            counter++;
                        }
                    }
                }
            }
        }
        return counter / 2; //every connection is listed 2 times (a to b and b to a)
    }

}
