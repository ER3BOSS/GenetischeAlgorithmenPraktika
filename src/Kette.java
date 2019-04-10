import javax.imageio.ImageIO;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class Kette {

    //Vars
    private String kette;
    private ArrayList<Node> kette2d = new ArrayList<>();

    //Constructor
    public Kette(String new_string) {
        kette = new_string;
    }

    //generates random coords for the nodes
    public void generateRandom(){
        int new_value = Character.getNumericValue(kette.charAt(0));
        kette2d.add(new Node(0,0,new_value));
        int next_x = 0;
        int next_y = 0;

        for (int i = 1; i < kette.length(); i++) {
            Boolean nextFree = false;
            int counter = 0;
            while (!nextFree) {
                int direction = new Random().nextInt(4);
                switch (direction){
                    case 0: //up
                        next_x = kette2d.get(kette2d.size()-1).getX();
                        next_y = kette2d.get(kette2d.size()-1).getY() + 1;
                        break;
                    case 1: //down
                        next_x = kette2d.get(kette2d.size()-1).getX();
                        next_y = kette2d.get(kette2d.size()-1).getY() - 1;
                        break;
                    case 2: //left
                        next_x = kette2d.get(kette2d.size()-1).getX() - 1;
                        next_y = kette2d.get(kette2d.size()-1).getY();
                        break;
                    case 3: //right
                        next_x = kette2d.get(kette2d.size()-1).getX() + 1;
                        next_y = kette2d.get(kette2d.size()-1).getY();
                        break;
                }
                if (checkNext(next_x,next_y)){
                    nextFree = true;
                }
                if (counter >= 30){
                    if (checkBlocked()){
                        System.out.println("Aborted... no where to go from here");
                        return;
                    }else {
                        System.out.print("triggered suroundcheck");
                        counter = 0;
                    }
                }
                counter ++;
            }
            kette2d.add(new Node(next_x,next_y,Character.getNumericValue(kette.charAt(i))));

        }
        return;
    }

    //checks if a given coordinate is free
    public boolean checkNext(int x, int y){
        for(Node node : kette2d){
            if(node.getX() == x && node.getY() == y){
                return false;
            }
        }
        return true;
    }

    public boolean checkBlocked(){
        int blocked = 0;
        //up
        int next_x = kette2d.get(kette2d.size()-1).getX();
        int next_y = kette2d.get(kette2d.size()-1).getY() + 1;
        if (!checkNext(next_x,next_y)){
            blocked ++;
        }

        //down
        next_x = kette2d.get(kette2d.size()-1).getX();
        next_y = kette2d.get(kette2d.size()-1).getY() - 1;
        if (!checkNext(next_x,next_y)){
            blocked ++;
        }

        //left
        next_x = kette2d.get(kette2d.size()-1).getX() - 1;
        next_y = kette2d.get(kette2d.size()-1).getY();
        if (!checkNext(next_x,next_y)){
            blocked ++;
        }

        //right
        next_x = kette2d.get(kette2d.size()-1).getX() + 1;
        next_y = kette2d.get(kette2d.size()-1).getY();
        if (!checkNext(next_x,next_y)){
            blocked ++;
        }

        if (blocked == 4){
            return true;
        }else {
            return false;
        }
    }

    //prints all coordinates
    public void printKette2D(){
        for(Node node : kette2d){
            System.out.print("X : " + node.getX());
            System.out.println(" Y : " + node.getY());
            System.out.print("");
        }
    }

    public Integer calcMinEnergie(){
        HashMap<Integer,Integer> map = new HashMap<>();
        for(int i = 0; i < kette2d.size(); i++){
            if (kette2d.get(i).getValue() == 1){

                int i_x = kette2d.get(i).getX();
                int i_y = kette2d.get(i).getY();

                for(int j = 0; j < kette2d.size(); j++){
                    if(i != j && kette2d.get(j).getValue() == 1){ //not the same and both 1
                        if (i - 1 != j && i + 1 != j){ //not connected
                            int j_x = kette2d.get(j).getX();
                            int j_y = kette2d.get(j).getY();
                            if ((i_x + 1 == j_x || i_x - 1 == j_x) && i_y == j_y){ // x is nearby y the same
                                map.put(i_x+j_x+i_y+j_y+i+j ,1); //hash map key is just a bunch of unique values for that connection
                            }
                            if ((i_y + 1 == j_y || i_y - 1 == j_y) && i_x == j_x){ // y is nearby x the same
                                map.put(i_x+j_x+i_y+j_y+i+j,1);
                            }
                        }
                    }
                }
            }
        }
        return map.size();
    }

    //craft the image
    public void createImage(){

        //get the lowest and highest x/y coords
        int low_y = 0;
        int low_x = 0;
        int high_y = 0;
        int high_x = 0;

        int cellSize = 80;

        for (Node node : kette2d){

            //get x and y from current node
            int x = node.getX();
            int y = node.getY();

            //check if there are new lowest/highest x/y values
            if (x < low_x){
                low_x = x;
            }
            if (x > high_x){
                high_x = x;
            }
            if (y < low_y){
                low_y = y;
            }
            if (y > high_y){
                high_y = y;
            }
        }
        //debug print
        //System.out.print("low_y: " + low_y + " high_y: " + high_y + " low_x: " + low_x + " high_x: " + high_x);

        //calc the spacing needed for the lines (used in the width/high calc)
        int x_lines = ((low_x*-1 + high_x)-1)*cellSize;
        if (x_lines < 0){
            x_lines = 0;
        }

        int y_lines = ((low_y*-1 + high_y)-1)*cellSize;
        if (y_lines < 0){
            y_lines = 0;
        }

        //calc with/height in a way that everything fits (+ a border of one cellSize)
        int width = cellSize+(low_x*-1 + high_x)*cellSize+x_lines+3*cellSize;
        int height = cellSize+(low_y*-1 + high_y)*cellSize+y_lines+3*cellSize;

        //initialize the graphic
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        //create Background
        g2.setColor(Color.GRAY);
        g2.fillRect(0, 0, width, height);

        //Get color of the first node
        if (kette2d.get(0).getValue() == 1){ //hydrophil
            g2.setColor(Color.BLACK);
        }else {
            g2.setColor(Color.WHITE); //hydrophob
        }

        //set initial coordinate
        int start_x = 2*-low_x*cellSize+cellSize;
        int start_y = 2*-low_y*cellSize+cellSize;

        //needed later to draw the lines between nodes
        int last_x = start_x;
        int last_y = start_y;

        //create the initial node
        g2.fillRect(start_x, start_y,cellSize,cellSize);

        //create all other nodes
        for (int i = 1; i < kette2d.size(); i++){ //starts at 1 bc first node is already created

            //calc current x/y in the image
            int current_x = start_x+(kette2d.get(i).getX()*cellSize*2);
            int current_y = start_y+(kette2d.get(i).getY()*cellSize*2);

            //get your color
            if (kette2d.get(i).getValue() == 1){ //hydrophil
                g2.setColor(Color.BLACK);
            }else {
                g2.setColor(Color.WHITE); //hydrophob
            }

            //draw yourself
            g2.fillRect(current_x,current_y,cellSize,cellSize);

            //draw line
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(5));
            if (current_x == last_x){ // y changed
                if (current_y > last_y){
                    g2.drawLine(current_x + cellSize / 2, current_y, last_x + cellSize / 2, last_y + cellSize);//nach oben
                } else {
                    g2.drawLine(current_x + cellSize / 2, current_y + cellSize, last_x + cellSize / 2, last_y); //nach unten
                }
            }else{ //x changed
                if (current_x > last_x){
                    g2.drawLine(current_x,current_y+cellSize/2, last_x+cellSize, last_y+cellSize/2); // nach rechts
                }else {
                    g2.drawLine(current_x+cellSize,current_y+cellSize/2, last_x, last_y+cellSize/2); // nach links
                }
            }

            //save your coords (needed to draw the next line)
            last_x = current_x;
            last_y = current_y;
        }

        //create image
        String folder = "/tmp/alex/ga";
        String filename = "Kette.png";
        if (new File(folder).exists() == false) new File(folder).mkdirs();

        try {
            ImageIO.write(image, "png", new File(folder + File.separator + filename));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }

        return;
    }
}
