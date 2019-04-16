import java.util.ArrayList;

class Main {
    public static void main(String[] args) {

        double curretBestFitness = 0;
        Kette bestKette = new Kette("11010101011110100010001000010001000101111010101011");

        ArrayList<Kette> individualsList = new ArrayList<>();

        for (int i = 0; i < 100000; i++){
            individualsList.add(new Kette("11010101011110100010001000010001000101111010101011"));
            individualsList.get(i).generateByInteligentRng();

            individualsList.get(i).printValues();
            double debugVar = individualsList.get(i).calcFitness();

            if (debugVar > curretBestFitness){
                bestKette = individualsList.get(i);
                curretBestFitness = individualsList.get(i).calcFitness();
            }

        }
        ImageCreator imageCreator = new ImageCreator();
        imageCreator.createImage(bestKette.getKette2d());
        System.out.println();
        System.out.println("### BEST ###");
        bestKette.printValues();

    }
}