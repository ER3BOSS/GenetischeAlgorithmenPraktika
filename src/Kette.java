import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


class Kette {

    private int cellSize = 80;
    //Vars
    private String kette;
    private ArrayList<Node> kette2d = new ArrayList<>();

    //Constructor
    public Kette(String new_string) {
        kette = new_string;
    }

    //generates random coords for the nodes
    public boolean generateRandom() {
        int new_value = Character.getNumericValue(kette.charAt(0));
        kette2d.add(new Node(0, 0, new_value));
        int next_x = 0;
        int next_y = 0;

        for (int i = 1; i < kette.length(); i++) {
            boolean nextFree = false;
            int counter = 0;

            int current_x = kette2d.get(kette2d.size() - 1).getX();
            int current_y = kette2d.get(kette2d.size() - 1).getY();

            while (!nextFree) {
                int direction = new Random().nextInt(4);
                switch (direction) {
                    case 0: //up
                        next_x = current_x;
                        next_y = current_y + 1;
                        break;
                    case 1: //down
                        next_x = current_x;
                        next_y = current_y - 1;
                        break;
                    case 2: //left
                        next_x = current_x - 1;
                        next_y = current_y;
                        break;
                    case 3: //right
                        next_x = current_x + 1;
                        next_y = current_y;
                        break;
                }
                if (checkNext(next_x, next_y)) {
                    nextFree = true;
                }
                if (counter >= 30) { //activates check after 30 consecutive fails
                    if (checkBlocked()) {
                        System.out.println("Aborted... no where to go from here");
                        kette2d.clear();
                        return false;
                    } else {
                        System.out.print("triggered suroundcheck");
                        counter = 0;
                    }
                }
                counter++;
            }
            kette2d.add(new Node(next_x, next_y, Character.getNumericValue(kette.charAt(i))));

        }
        return true;
    }

    public int calcMinEnergie() {
        int counter = 0;
        for (int i = 0; i < kette2d.size(); i++) {
            if (kette2d.get(i).getValue() == 1) {

                int i_x = kette2d.get(i).getX();
                int i_y = kette2d.get(i).getY();

                for (int j = 0; j < kette2d.size(); j++) {

                    if (i != j && kette2d.get(j).getValue() == 1) { //not the same and both 1

                        if (i - 1 != j && i + 1 != j) { //not connected

                            int j_x = kette2d.get(j).getX();
                            int j_y = kette2d.get(j).getY();

                            if (Math.abs(i_x - j_x) == 1  && i_y == j_y) { // x is +-1 y the same
                                counter ++;
                            }else if (Math.abs(i_y - j_y) == 1 && i_x == j_x) { // y is +-1 x the same
                                counter ++;
                            }
                        }
                    }
                }
            }
        }
        return counter/2; //every connection is listed 2 times (a to b and b to a)
    }

    //craft the image
    public void createImage() {

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

        //create all other nodes
        drawNodes(g2, start_x, start_y);

        //create image
        String folder = "/tmp/alex/ga";
        String filename = "Kette.png";
        if (!new File(folder).exists()) {
            new File(folder).mkdirs();
        }

        try {
            ImageIO.write(image, "png", new File(folder + File.separator + filename));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    //checks if a given coordinate is free
    private boolean checkNext(int x, int y) {
        for (Node node : kette2d) {
            if (node.getX() == x && node.getY() == y) {
                return false;
            }
        }
        return true;
    }

    private boolean checkBlocked() {

        int current_x = kette2d.get(kette2d.size() - 1).getX();
        int current_y = kette2d.get(kette2d.size() - 1).getY();

        int blocked = 0;
        //up
        if (!checkNext(current_x, current_y + 1)) {
            blocked++;
        }

        //down
        if (!checkNext(current_x, current_y - 1)) {
            blocked++;
        }

        //left
        if (!checkNext(current_x - 1, current_y)) {
            blocked++;
        }

        //right
        if (!checkNext(current_x + 1, current_y)) {
            blocked++;
        }

        return blocked == 4;
    }

    private int[] calcImageSize() {

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

        int[] returnValues = new int[4];

        //calc with/height in a way that everything fits (+ a border of one cellSize)
        returnValues[0] = cellSize + (low_x * -1 + high_x) * cellSize + x_lines + 3 * cellSize; //width
        returnValues[1] = cellSize + (low_y * -1 + high_y) * cellSize + y_lines + 3 * cellSize; //height

        int[] startPos = calcStartPos(low_x, low_y);
        returnValues[2] = startPos[0]; //start_x
        returnValues[3] = startPos[1]; //start_y

        return returnValues; // width, height, start_x, start_y
    }

    private void drawNodes(Graphics2D g2, int start_x, int start_y) {

        //Get color of the first node
        if (kette2d.get(0).getValue() == 1) { //hydrophil
            g2.setColor(Color.BLACK);
        } else {
            g2.setColor(Color.WHITE); //hydrophob
        }

        //create the initial node
        g2.fillRect(start_x, start_y, cellSize, cellSize);
        drawIndex(g2,0,start_x,start_y);

        //needed later to draw the lines between nodes
        int last_x = start_x;
        int last_y = start_y;
        //create all other nodes

        for (int i = 1; i < kette2d.size(); i++) { //starts at 1 bc first node is already created

            //calc current x/y in the image
            int current_x = start_x + (kette2d.get(i).getX() * cellSize * 2);
            int current_y = start_y + (kette2d.get(i).getY() * cellSize * 2);

            //get your color
            if (kette2d.get(i).getValue() == 1) { //hydrophil
                g2.setColor(Color.BLACK);
            } else {
                g2.setColor(Color.WHITE); //hydrophob
            }

            //draw yourself
            g2.fillRect(current_x, current_y, cellSize, cellSize);

            drawIndex(g2,i,current_x,current_y);

            //draw line
            drawLine(g2, current_x, current_y, last_x, last_y);

            //save your coords (needed to draw the next line)
            last_x = current_x;
            last_y = current_y;
        }
    }

    private int[] calcLowHighCoords() {
        //get the lowest and highest x/y coords

        int[] returnValues = new int[4];

        for (Node node : kette2d) {

            //get x and y from current node
            int x = node.getX();
            int y = node.getY();

            //check if there are new lowest/highest x/y values
            if (x < returnValues[0]) {
                returnValues[0] = x;//low x
            }
            if (x > returnValues[1]) {
                returnValues[1] = x; //high x
            }
            if (y < returnValues[2]) {
                returnValues[2] = y; //low y
            }
            if (y > returnValues[3]) {
                returnValues[3] = y; // high y
            }
        }
        return returnValues; //low_x, high_x, low_y, high_y
    }

    private int[] calcStartPos(int low_x, int low_y) {//calculates the position of the first node

        int[] returnValues = new int[2];

        returnValues[0] = 2 * -low_x * cellSize + cellSize; //start_x
        returnValues[1] = 2 * -low_y * cellSize + cellSize; //start_y

        return returnValues;
    }

    private void drawLine(Graphics2D g2, int current_x, int current_y, int last_x, int last_y) {
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

    private void drawIndex(Graphics2D g2, int index, int current_x, int current_y){

        // choose a color
        if (kette2d.get(index).getValue() == 1){
            g2.setColor(Color.WHITE); //white on black and the other way around
        }else{
            g2.setColor(Color.BLACK);
        }

        //create Text
        String label = Integer.toString(index);
        int fontSize = 60;
        Font font = new Font("Serif", Font.PLAIN, fontSize);
        g2.setFont(font);
        FontMetrics metrics = g2.getFontMetrics();
        int offset = fontSize/3; // center text
        int labelWidth = metrics.stringWidth(label);

        //draw Text
        g2.drawString(label, current_x + cellSize/2 - labelWidth/2 , current_y + cellSize/2 + offset);
    }
}
