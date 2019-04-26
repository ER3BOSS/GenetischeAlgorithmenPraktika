class Main {
    public static void main(String[] args) {
        GenerationHandler generationHandler = new GenerationHandler("11010101011110100010001000010001000101111010101011");
        generationHandler.initializeGeneration(50);
        generationHandler.evolve(1000,50, 0);
        generationHandler.printResult();
    }
}