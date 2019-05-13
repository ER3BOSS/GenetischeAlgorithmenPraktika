import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

class ImageCreator {

    private int cellSize = 80;
    private ArrayList<Node> phenotype = new ArrayList<>();

    void createImage(ArrayList<Node> phenotype, double fitness, int overlap, int minEnergy, String filename) {

        this.phenotype = phenotype;

        int[] imageData = calcImageSize();
        int width = imageData[0];
        int height = imageData[1];
        int startX = imageData[2];
        int startY = imageData[3];


        //initialize the graphic
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        //create Background
        g2.setColor(Color.GRAY);
        g2.fillRect(0, 0, width, height);

        drawNodes(g2, startX, startY); //each node also draws an index and a line

        ImageTextWriter.writeDataToImage(g2,fitness,overlap,minEnergy,width,height);

        //create folder
        String folder = "/ga";
        if (!new File(folder).exists()) {
            //noinspection ResultOfMethodCallIgnored
            new File(folder).mkdirs();
        }

        //create image
        try {
            ImageIO.write(image, "png", new File(folder + File.separator + filename));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    private int[] calcImageSize() {

        int[] lowHighXY = calcLowHighCoords();
        int lowX = lowHighXY[0];
        int highX = lowHighXY[1];
        int lowY = lowHighXY[2];
        int highY = lowHighXY[3];

        //calc the spacing needed for the lines (used in the width/high calc)
        int xLines = ((lowX * -1 + highX) - 1) * cellSize;
        if (xLines < 0) {
            xLines = 0;
        }

        int yLines = ((lowY * -1 + highY) - 1) * cellSize;
        if (yLines < 0) {
            yLines = 0;
        }

        //calc with/height in a way that everything fits (+ a border of one cellSize)
        int width = cellSize + (lowX * -1 + highX) * cellSize + xLines + 3 * cellSize; //width
        int height = cellSize + (lowY * -1 + highY) * cellSize + yLines + 3 * cellSize; //height

        int[] startPos = calcStartPos(lowX, lowY);
        int startX = startPos[0]; //start_x
        int startY = startPos[1]; //start_y

        return new int[]{width, height, startX, startY};
    }

    private void drawNodes(Graphics2D g2, int startX, int startY) {

        //Set color of the first node
        chooseNodeColor(g2, 0);

        //create the initial node
        g2.fillRect(startX, startY, cellSize, cellSize);
        drawIndex(g2, 0, startX, startY);

        //needed later to draw the lines between nodes
        int lastX = startX;
        int lastY = startY;
        //create all other nodes

        for (int i = 1; i < phenotype.size(); i++) { //starts at 1 bc first node is already created

            //calc current x/y in the image
            int currentX = startX + (phenotype.get(i).getX() * cellSize * 2);
            int currentY = startY + (phenotype.get(i).getY() * cellSize * 2);

            if (notOverlapping(phenotype.get(i).getX(), phenotype.get(i).getY(), i)) {
                chooseNodeColor(g2, i);
            } else {
                g2.setColor(Color.RED);
            }

            //draw yourself
            g2.fillRect(currentX, currentY, cellSize, cellSize);

            drawIndex(g2, i, currentX, currentY);

            drawLine(g2, currentX, currentY, lastX, lastY);

            //save your coords (needed to draw the next line)
            lastX = currentX;
            lastY = currentY;
        }
    }

    private int[] calcLowHighCoords() {//get the lowest and highest x/y coords

        int lowX, highX, lowY, highY;
        lowX = highX = lowY = highY = 0;

        for (Node node : phenotype) {

            //get x and y from current node
            int x = node.getX();
            int y = node.getY();

            //check if there are new lowest/highest x/y values
            if (x < lowX) {
                lowX = x;
            }
            if (x > highX) {
                highX = x;
            }
            if (y < lowY) {
                lowY = y;
            }
            if (y > highY) {
                highY = y;
            }
        }
        return new int[]{lowX, highX, lowY, highY};
    }

    private int[] calcStartPos(int lowX, int lowY) {//calculates the position of the first node

        int startX = 2 * -lowX * cellSize + cellSize; //start_x
        int startY = 2 * -lowY * cellSize + cellSize; //start_y

        return new int[]{startX, startY};
    }

    private void chooseNodeColor(Graphics2D g2, int index) {
        if (phenotype.get(index).getValue() == 1) { //hydrophil
            g2.setColor(Color.BLACK);
        } else {
            g2.setColor(Color.WHITE); //hydrophob
        }
    }

    private void drawIndex(Graphics2D g2, int index, int currentX, int currentY) {

        chooseTextColor(g2, index);

        //create Text
        String label = Integer.toString(index);
        int fontSize = 60;
        Font font = new Font("Serif", Font.PLAIN, fontSize);
        g2.setFont(font);
        FontMetrics metrics = g2.getFontMetrics();
        int offset = fontSize / 3; // center text
        int labelWidth = metrics.stringWidth(label);

        //draw Text
        g2.drawString(label, currentX + cellSize / 2 - labelWidth / 2, currentY + cellSize / 2 + offset);
    }

    private boolean notOverlapping(int x, int y, int index) {
        for (int i = 0; i < phenotype.size(); i++) { //checks if node is overlapping with a different node
            if (x == phenotype.get(i).getX() && y == phenotype.get(i).getY() && i != index) {
                return false;
            }
        }
        return true;
    }

    private void drawLine(Graphics2D g2, int currentX, int currentY, int lastX, int lastY) {
        //draw line
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(5));
        if (currentX == lastX) { // y changed
            if (currentY > lastY) {
                g2.drawLine(currentX + cellSize / 2, currentY, lastX + cellSize / 2, lastY + cellSize);//up
            } else {
                g2.drawLine(currentX + cellSize / 2, currentY + cellSize, lastX + cellSize / 2, lastY); //down
            }
        } else { //x changed
            if (currentX > lastX) {
                g2.drawLine(currentX, currentY + cellSize / 2, lastX + cellSize, lastY + cellSize / 2); //right
            } else {
                g2.drawLine(currentX + cellSize, currentY + cellSize / 2, lastX, lastY + cellSize / 2); //left
            }
        }
    }

    private void chooseTextColor(Graphics2D g2, int index) {
        if (phenotype.get(index).getValue() == 1) { //hydrophil
            g2.setColor(Color.WHITE);
        } else {
            g2.setColor(Color.BLACK); //hydrophob
        }
    }
}
