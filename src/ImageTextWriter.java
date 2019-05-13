import java.awt.*;

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

        String fitnessString = "Fitness: " + Double.toString(fitness).substring(0,4);
        String overlapString = "Overlap: " + Integer.toString(overlap);
        String minEnergyString = "Energy: " + Integer.toString(minEnergy);
        String combinedString = fitnessString+" | "+overlapString+" | "+minEnergyString;

        if (StringFits(combinedString, g2, imageWidth, xMargin)){
            g2.drawString(combinedString, xMargin, yMarginTop);
        }else {//recursion with reduced font size
            int updatedFontSize = fontSize - fontSize / 10; //subtract one tenth
            writeDataToImage(g2,fitness,overlap,minEnergy,imageWidth, updatedFontSize);
        }
    }

    private static boolean StringFits(String combinedString, Graphics2D g2, int imageWidth, int xMargin) {
        return g2.getFontMetrics().stringWidth(combinedString)+xMargin*2 < imageWidth;
    }
}
