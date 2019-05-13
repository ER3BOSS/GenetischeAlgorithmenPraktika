import java.awt.*;

public class ImageTextWriter {
    private ImageTextWriter() {
        //Do not call!!!
    }

    static public void writeDataToImage(Graphics2D g2, double fitness, int overlap, int minEnergy, int imageWidth, int Imageheigth){
        int xMargin = 20;
        int yMargin = 60;

        g2.setColor(Color.BLACK);
        String fitnessString = "Fitness: " + Double.toString(fitness).substring(0,4);
        String overlapString = "Overlap: " + Integer.toString(overlap);
        String minEnergyString = "Energy: " + Integer.toString(minEnergy);
        String combinedString = fitnessString+" "+overlapString+" "+minEnergyString;

        if (g2.getFontMetrics().stringWidth(combinedString)+xMargin*2 < imageWidth){
            g2.drawString(combinedString,xMargin,yMargin);
        }else if (g2.getFontMetrics().stringWidth(overlapString+minEnergyString)+xMargin*2 < imageWidth){
            g2.drawString(fitnessString,xMargin,yMargin);
            g2.drawString(overlapString+" "+minEnergyString, xMargin, Imageheigth-yMargin/3);
        }


    }
}
