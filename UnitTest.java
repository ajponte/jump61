package jump61;

import ucb.junit.textui;

/** The suite of all JUnit tests for the game.
 *  @author Alan Ponte
 */
public class UnitTest {

    /** Run the JUnit tests in the jump61 package. */
    public static void main(String[] ignored) {
        textui.runClasses(jump61.BoardTest.class);
        textui.runClasses(jump61.GameTest.class);
    }

}


