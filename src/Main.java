import java.io.IOException;


class Main {
    public static void main(String[] args) throws IOException {

        String SEQMini = "1001"; //For testing purposes
        String SEQ20 = "10100110100101100101";
        String SEQ24 = "110010010010010010010011";
        String SEQ25 = "0010011000011000011000011";
        String SEQ36 = "000110011000001111111001100001100100";
        String SEQ48 = "001001100110000011111111110000001100110010011111";
        String SEQ50 = "11010101011110100010001000010001000101111010101011";



        GenerationHandler generationHandler = new GenerationHandler(SEQ20);
        generationHandler.initializeGeneration(1000);

        generationHandler.evolve(
                500,
                0,
                0.1,
                0.1,
                20,
                SelectType.TOURNAMENT,
                100
        );

        generationHandler.drawResult(0);
    }
}
