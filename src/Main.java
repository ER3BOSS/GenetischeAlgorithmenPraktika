class Main {
    public static void main(String[] args) {
        Kette kette = new Kette("11010101011110100010001000010001000101111010101011");
        boolean success;
        do { //generate a valid graph
            success = kette.generateRandom();
        } while (!success);
        kette.createImage();
        System.out.println("Minimale Energie: " + kette.calcMinEnergie());
    }
}