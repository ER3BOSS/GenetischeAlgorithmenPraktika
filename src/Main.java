class Main {
    public static void main(String[] args) {
        GenerationHandler generationHandler = new GenerationHandler("11010101011110100010001000010001000101111010101011");
        generationHandler.initializeGeneration(4000);
        generationHandler.evolve(50,4000, 0);
        generationHandler.printResult();
    }
}