class Main {
    public static void main(String[] args) {

        GenerationHandler generationHandler = new GenerationHandler("11010101011110100010001000010001000101111010101011");
        generationHandler.initializeGeneration(1000);
        generationHandler.evolve(50,1000, 0);
        generationHandler.drawResult(2);

        /*
        einfacherGenetischerAlgorithmus a = new einfacherGenetischerAlgorithmus("11010101011110100010001000010001000101111010101011");
        a.einfacherGenetischerAlgorithmus(100);
        */
    }
}