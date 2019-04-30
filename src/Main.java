class Main {
    public static void main(String[] args) {
        /*

        /* Test sequences
        String SEQ20 = "10100110100101100101";
        String SEQ24 = "110010010010010010010011";
        String SEQ25 = "0010011000011000011000011";
        String SEQ36 = "000110011000001111111001100001100100";
        String SEQ48 = "001001100110000011111111110000001100110010011111";
        String SEQ50 = "11010101011110100010001000010001000101111010101011";
        */
        /*
        GenerationHandler generationHandler = new GenerationHandler("11010101011110100010001000010001000101111010101011");
        generationHandler.initializeGeneration(1000);
        generationHandler.evolve(50,1000, 0);
        generationHandler.drawResult(2);
        */

        einfacherGenetischerAlgorithmus a = new einfacherGenetischerAlgorithmus("10100110100101100101");
        a.einfacherGenetischerAlgorithmus(100);

    }
}