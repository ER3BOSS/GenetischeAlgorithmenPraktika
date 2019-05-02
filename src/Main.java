import java.io.IOException;

import org.jfree.chart.ChartFactory;

class Main {
    public static void main(String[] args) throws IOException {

        /* Test sequences
        String SEQ20 = "10100110100101100101";
        String SEQ24 = "110010010010010010010011";
        String SEQ25 = "0010011000011000011000011";
        String SEQ36 = "000110011000001111111001100001100100";
        String SEQ48 = "001001100110000011111111110000001100110010011111";
        String SEQ50 = "11010101011110100010001000010001000101111010101011";
        */

        GenerationHandler generationHandler = new GenerationHandler("10100110100101100101");
        generationHandler.initializeGeneration(100);
        generationHandler.evolve(100, 0);
        generationHandler.drawResult(2);

        }
}