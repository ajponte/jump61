package jump61;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.junit.Test;

import junit.framework.TestCase;

/** Unit testing of Game.java.
 * @author Alan Ponte
 * */
public class GameTest extends TestCase {
    @Test
    public void testMakeMove() {
        Writer output = new OutputStreamWriter(System.out);
        Game game = new Game(new InputStreamReader(System.in), output, output,
                             new OutputStreamWriter(System.err));
        game.makeMove(1, 1);
        assertEquals(game.getBoard().toString(),
            "===\n"
            + "    1r -- -- -- -- --\n"
            + "    -- -- -- -- -- --\n"
            + "    -- -- -- -- -- --\n"
            + "    -- -- -- -- -- --\n"
            + "    -- -- -- -- -- --\n"
            + "    -- -- -- -- -- --\n"
            + "===");
        game.makeMove(2, 2);
        game.makeMove(1, 1);
        assertEquals(game.getBoard().toString(),
            "===\n"
            + "    -- 1r -- -- -- --\n"
            + "    2r 1b -- -- -- --\n"
            + "    -- -- -- -- -- --\n"
            + "    -- -- -- -- -- --\n"
            + "    -- -- -- -- -- --\n"
            + "    -- -- -- -- -- --\n"
            + "===");
    }

    @Test
    public void testErrors() {
        Writer output = new OutputStreamWriter(System.out);
        Game game = new Game(new InputStreamReader(System.in), output, output,
                             new OutputStreamWriter(System.err));
        try {
            game.makeMove(6, 9);
            fail("Bad move did not error");
        } catch (ArrayIndexOutOfBoundsException e) {
            String s = "THIS WILL MAKE THE STYLE CHECKER HAPPY";
        }
        try {
            game.makeMove(12, 31);
            fail("Illegal move did not raise an error");
        } catch (ArrayIndexOutOfBoundsException e) {
            String s = "THIS WILL MAKE THE STYLE CHECKER HAPPY";
        }
        assertEquals("Bad moves changed the game", game.getBoard().toString(),
            "===\n"
            + "    -- -- -- -- -- --\n"
            + "    -- -- -- -- -- --\n"
            + "    -- -- -- -- -- --\n"
            + "    -- -- -- -- -- --\n"
            + "    -- -- -- -- -- --\n"
            + "    -- -- -- -- -- --\n"
            + "===");
    }
}
