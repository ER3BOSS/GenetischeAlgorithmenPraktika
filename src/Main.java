import java.io.IOException;
import java.util.Random;

        /* Test sequences
        String SEQ20 = "10100110100101100101";
        String SEQ24 = "110010010010010010010011";
        String SEQ25 = "0010011000011000011000011";
        String SEQ36 = "000110011000001111111001100001100100";
        String SEQ48 = "001001100110000011111111110000001100110010011111";
        String SEQ50 = "11010101011110100010001000010001000101111010101011";
        */

class Main {
    public static void main(String[] args) throws IOException {

        GenerationHandler generationHandler = new GenerationHandler("10100110100101100101");
        generationHandler.initializeGeneration(1000);

        generationHandler.evolve(
                300,
                0,
                0.1,
                0.1,
                SelectType.TOURNAMENT
        );

        generationHandler.drawResult(1);

        }
}
