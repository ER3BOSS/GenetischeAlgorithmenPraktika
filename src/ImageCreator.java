import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

class ImageCreator {

     private static int cellSize = 80;
     private static ArrayList<Node> kette2d = new ArrayList<>();

    static void createImage(ArrayList<Node> new_kette2d) {
        
        kette2d = new_kette2d;

        int[] imageData = calcImageSize();
        int width = imageData[0];
        int height = imageData[1];
        int start_x = imageData[2];
        int start_y = imageData[3];


        //initialize the graphic
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        //create Background
        g2.setColor(Color.GRAY);
        g2.fillRect(0, 0, width, height);

        drawNodes(g2, start_x, start_y); //each node also draws an index and a line

        //create folder
        String folder = "/ga";
        if (!new File(folder).exists()) {
            //noinspection ResultOfMethodCallIgnored
            new File(folder).mkdirs();
        }

        //create image
        String filename = "Kette.png";
        try {
            ImageIO.write(image, "png", new File(folder + File.separator + filename));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    private static int[] calcImageSize() {

        int[] lowHighXY = calcLowHighCoords();
        int low_x = lowHighXY[0];
        int high_x = lowHighXY[1];
        int low_y = lowHighXY[2];
        int high_y = lowHighXY[3];

        //calc the spacing needed for the lines (used in the width/high calc)
        int x_lines = ((low_x * -1 + high_x) - 1) * cellSize;
        if (x_lines < 0) {
            x_lines = 0;
        }

        int y_lines = ((low_y * -1 + high_y) - 1) * cellSize;
        if (y_lines < 0) {
            y_lines = 0;
        }

        //calc with/height in a way that everything fits (+ a border of one cellSize)
        int width = cellSize + (low_x * -1 + high_x) * cellSize + x_lines + 3 * cellSize; //width
        int height = cellSize + (low_y * -1 + high_y) * cellSize + y_lines + 3 * cellSize; //height

        int[] startPos = calcStartPos(low_x, low_y);
        int start_x = startPos[0]; //start_x
        int start_y = startPos[1]; //start_y

        return new int[]{width, height, start_x, start_y};
    }

    private static void drawNodes(Graphics2D g2, int start_x, int start_y) {

        //Set color of the first node
        chooseNodeColor(g2, 0);

        //create the initial node
        g2.fillRect(start_x, start_y, cellSize, cellSize);
        drawIndex(g2, 0, start_x, start_y);

        //needed later to draw the lines between nodes
        int last_x = start_x;
        int last_y = start_y;
        //create all other nodes

        for (int i = 1; i < kette2d.size(); i++) { //starts at 1 bc first node is already created

            //calc current x/y in the image
            int current_x = start_x + (kette2d.get(i).getX() * cellSize * 2);
            int current_y = start_y + (kette2d.get(i).getY() * cellSize * 2);

            chooseNodeColor(g2, i);

            //draw yourself
            g2.fillRect(current_x, current_y, cellSize, cellSize);

            drawIndex(g2, i, current_x, current_y);

            drawLine(g2, current_x, current_y, last_x, last_y);

            //save your coords (needed to draw the next line)
            last_x = current_x;
            last_y = current_y;
        }
    }

    private static int[] calcLowHighCoords() {//get the lowest and highest x/y coords

        int low_x, high_x, low_y, high_y;
        low_x = high_x = low_y = high_y = 0;

        for (Node node : kette2d) {

            //get x and y from current node
            int x = node.getX();
            int y = node.getY();

            //check if there are new lowest/highest x/y values
            if (x < low_x) {
                low_x = x;
            }
            if (x > high_x) {
                high_x = x;
            }
            if (y < low_y) {
                low_y = y;
            }
            if (y > high_y) {
                high_y = y;
            }
        }
        return new int[]{low_x, high_x, low_y, high_y};
    }

    private static int[] calcStartPos(int low_x, int low_y) {//calculates the position of the first node

        int start_x = 2 * -low_x * cellSize + cellSize; //start_x
        int start_y = 2 * -low_y * cellSize + cellSize; //start_y

        return new int[]{start_x, start_y};
    }

    private static void chooseNodeColor(Graphics2D g2, int index) {
        if (kette2d.get(index).getValue() == 1) { //hydrophil
            g2.setColor(Color.BLACK);
        } else {
            g2.setColor(Color.WHITE); //hydrophob
        }
    }

    private static void drawIndex(Graphics2D g2, int index, int current_x, int current_y) {

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
        g2.drawString(label, current_x + cellSize / 2 - labelWidth / 2, current_y + cellSize / 2 + offset);
    }

    private static void drawLine(Graphics2D g2, int current_x, int current_y, int last_x, int last_y) {
        //draw line
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(5));
        if (current_x == last_x) { // y changed
            if (current_y > last_y) {
                g2.drawLine(current_x + cellSize / 2, current_y, last_x + cellSize / 2, last_y + cellSize);//up
            } else {
                g2.drawLine(current_x + cellSize / 2, current_y + cellSize, last_x + cellSize / 2, last_y); //down
            }
        } else { //x changed
            if (current_x > last_x) {
                g2.drawLine(current_x, current_y + cellSize / 2, last_x + cellSize, last_y + cellSize / 2); //right
            } else {
                g2.drawLine(current_x + cellSize, current_y + cellSize / 2, last_x, last_y + cellSize / 2); //left
            }
        }
    }

    private static void chooseTextColor(Graphics2D g2, int index) {
        if (kette2d.get(index).getValue() == 1) { //hydrophil
            g2.setColor(Color.WHITE);
        } else {
            g2.setColor(Color.BLACK); //hydrophob
        }
    }
}
