import java.util.ArrayList;

class Main {
    public static void main(String[] args) {

        double curretBestFitness = 0;
        Kette bestKette = new Kette("11010101011110100010001000010001000101111010101011");

        ArrayList<Kette> individualsList = new ArrayList<>();

        for (int i = 0; i < 1000000; i++){
            Kette kette = new Kette("11010101011110100010001000010001000101111010101011");
            kette.generateByInteligentRng();

            kette.printValues();
            double debugVar = kette.calcFitness();

            if (debugVar > curretBestFitness){
                bestKette = kette;
                curretBestFitness = kette.calcFitness();
            }

        }
        ImageCreator imageCreator = new ImageCreator();
        imageCreator.createImage(bestKette.getKette2d());
        System.out.println();
        System.out.println("### BEST ###");
        bestKette.printValues();

    }
}