/** CellMatrixTest Class runs multiple
 *  scenarios for RLE conversion to pass
 *
 * @author RMizelle
 */
public class CellMatrixTest {
    private static CellMatrix cm = new CellMatrix(10, 10);
    private static int userScore = 0;
    private static int maxScore = 0;


    public static void main(String[] args) {
        titleScreen();

        System.out.println("Blank 10x10 Matrix");
        testRLE("$$$$$$$$$!");

        System.out.println("Filled 10x10 Matrix");
        testRLE("10o$10o$10o$10o$10o$10o$10o$10o$10o$10o!");

        System.out.println("Cross 10x10 Matrix");
        testRLE("o8bo$bo6bo$2bo4bo$3bo2bo$4b2o$4b2o$3bo2bo$2bo4bo$bo6bo$o8bo!");

        System.out.println("Border 10x10 Matrix");
        testRLE("10o$o8bo$o8bo$o8bo$o8bo$o8bo$o8bo$o8bo$o8bo$10o!");

        System.out.println("Diagonal 10x10 Matrix");
        testRLE("3o$4o$5o$b5o$2b5o$3b5o$4b5o$5b5o$6b4o$7b3o!");

        System.out.println("Horizontal Stripes 10x10 Matrix");
        testRLE("10o$$10o$$10o$$10o$$10o$!");

        System.out.println("Vertical Stripes 10x10 Matrix");
        testRLE("bobobobobo$bobobobobo$bobobobobo$bobobobobo$bobobobobo$bobobobobo$bobobobobo$bobobobobo$bobobobobo$bobobobobo!");

        System.out.println("Score: " + userScore + "/" + maxScore);
        if(userScore < maxScore) {
            System.out.println("You're close! Troubleshoot by comparing your submission to the solution. Good luck!");
        }
        else {
            System.out.println("Congratulations!!! Everything (should) be working correctly.\nTry testing out your code in the main application.");
        }
    }

    /** Displays each rle test scenario and mutates
     *  score parameters
     *
     * @param rle string to be tested
     */
    public static void testRLE(String rle) {
        //increments max score
        maxScore++;

        //Displays scenario
        System.out.println("Solution:   " + rle);
        cm.fromRLE(rle);
        System.out.println("Submission: " + cm);

        if(rle.equals(cm.toString())) {
            //if correct, increase score
            System.out.println("PASSED");
            userScore++;
        }
        else {
            //else failed
            System.out.println("FAILED");
        }
        System.out.println();
    }

    /** Prints Automata as ASCII Art */
    public static void titleScreen() {
        System.out.println(
                "     _         _                        _           \n" +
                "    / \\  _   _| |_ ___  _ __ ___   __ _| |_ __ _   \n" +
                "   / _ \\| | | | __/ _ \\| '_ ` _ \\ / _` | __/ _` |\n" +
                "  / ___ \\ |_| | || (_) | | | | | | (_| | || (_| |  \n" +
                " /_/   \\_\\__,_|\\__\\___/|_| |_| |_|\\__,_|\\__\\__,_|\n");
    }
}
