import java.util.ArrayList;
import java.util.Random;

class Random2DGenerator {

    private ArrayList<Node> kette2d = new ArrayList<>();


    ArrayList<Node> generateRandomGraphNoOverlap(String kette) {
        //create first node at 0,0
        int new_value = Character.getNumericValue(kette.charAt(0));
        kette2d.add(new Node(0, 0, new_value));

        int[] nextPos;

        for (int i = 1; i < kette.length(); i++) {

            int counter = 0;

            do {
                nextPos = generateRandomPos();
                if (counter >= 30) { //activates check after 30 consecutive fails
                    if (checkSurroundings()) {
                        System.out.println("Aborted... no where to go from here");
                        kette2d.clear();
                        return (kette2d);
                    }
                }
                counter++;

            } while (coordinateUnavailable(nextPos[0], nextPos[1]));

            kette2d.add(new Node(nextPos[0], nextPos[1], Character.getNumericValue(kette.charAt(i)), nextPos[2]));
        }
        return kette2d;
    }

    ArrayList<Node> generateRandomGraph(String kette) {
        //create first node at 0,0
        int new_value = Character.getNumericValue(kette.charAt(0));
        kette2d.add(new Node(0, 0, new_value));

        int[] nextPos;

        for (int i = 1; i < kette.length(); i++) {
            nextPos = generateRandomPos();
            kette2d.add(new Node(nextPos[0], nextPos[1], Character.getNumericValue(kette.charAt(i)), nextPos[2]));
        }
        return kette2d;
    }

    ArrayList<Node> generateIntelligentRandomGraph(String kette) {
        //create first node at 0,0
        int new_value = Character.getNumericValue(kette.charAt(0));
        kette2d.add(new Node(0, 0, new_value));

        int[] nextPos;

        for (int i = 1; i < kette.length(); i++) {

            int counter = 0;

            do {
                nextPos = generateRandomPos();
                if (counter >= 30) { //activates check after 30 consecutive fails
                    if (checkSurroundings()) {
                        break;
                    }
                }
                counter++;

            } while (coordinateUnavailable(nextPos[0], nextPos[1]));

            kette2d.add(new Node(nextPos[0], nextPos[1], Character.getNumericValue(kette.charAt(i)), nextPos[2]));
        }
        return kette2d;
    }

    private int[] generateRandomPos() {
        // retrieve current pos
        int current_x = kette2d.get(kette2d.size() - 1).getX();
        int current_y = kette2d.get(kette2d.size() - 1).getY();

        // store potential x,y
        int next_x = 0;
        int next_y = 0;

        int direction = new Random().nextInt(4); //random 0 - 3
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

        return new int[]{next_x, next_y, direction};
    }

    private boolean checkSurroundings() { //checks if all possible directions are unavailable

        int current_x = kette2d.get(kette2d.size() - 1).getX();
        int current_y = kette2d.get(kette2d.size() - 1).getY();

        int blocked = 0;

        if (coordinateUnavailable(current_x, current_y + 1)) {//up
            blocked++;
        }
        if (coordinateUnavailable(current_x, current_y - 1)) {//down
            blocked++;
        }
        if (coordinateUnavailable(current_x - 1, current_y)) {//left
            blocked++;
        }
        if (coordinateUnavailable(current_x + 1, current_y)) {//right
            blocked++;
        }

        return blocked == 4;
    }

    private boolean coordinateUnavailable(int x, int y) {//checks if a given coordinate is free
        for (Node node : kette2d) {
            if (node.getX() == x && node.getY() == y) {
                return true;
            }
        }
        return false;
    }
}
