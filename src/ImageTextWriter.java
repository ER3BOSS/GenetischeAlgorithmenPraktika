import java.awt.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;

class ImageTextWriter {
    private ImageTextWriter() { //Static class
        //Do not call!!!
    }

    static void writeDataToImage(Graphics2D g2, double fitness, int overlap, int minEnergy, int imageWidth, int fontSize){
        Font font = new Font("Serif", Font.PLAIN, fontSize);
        g2.setFont(font);
        int xMargin = 25;
        int yMarginTop = 40+fontSize/4; // just works ...

        g2.setColor(Color.BLACK);
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.UP);

        String combinedString = getCombinedString(fitness, overlap, minEnergy);

        if (StringFits(combinedString, g2, imageWidth, xMargin)){
            g2.drawString(combinedString, centerX(g2, imageWidth, xMargin, combinedString), yMarginTop);
        }else {//recursion with reduced font size
            int updatedFontSize = fontSize - fontSize / 10; //subtract one tenth
            writeDataToImage(g2,fitness,overlap,minEnergy,imageWidth, updatedFontSize);
        }
    }

    private static int centerX(Graphics2D g2, int imageWidth, int xMargin, String combinedString) {
        return imageWidth/2-(g2.getFontMetrics().stringWidth(combinedString)+xMargin*2)/2+xMargin;
    }

    private static String getCombinedString(double fitness, int overlap, int minEnergy) {
        fitness = Math.round(fitness * 100.0) / 100.0;
        String fitnessString = "Fitness: " + Double.toString(fitness);
        String overlapString = "Overlap: " + Integer.toString(overlap);
        String minEnergyString = "Energy: " + Integer.toString(minEnergy);
        return fitnessString+" | "+overlapString+" | "+minEnergyString;
    }

    private static boolean StringFits(String combinedString, Graphics2D g2, int imageWidth, int xMargin) {
        return g2.getFontMetrics().stringWidth(combinedString)+xMargin*2 < imageWidth;
    }
}
