class Main {
    public static void main(String[] args) {
        Kette kette = new Kette("11010101011110100010001000010001000101111010101011");
        kette.generateRandom();
        kette.createImage();
        System.out.println("Minimale Energie: " + kette.calcMinEnergie());
    }
}