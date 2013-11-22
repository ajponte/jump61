package jump61;

import java.io.Reader;
import java.io.Writer;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Random;
import java.util.Stack;

import static jump61.Color.*;
import static jump61.GameException.error;

/** Main logic for playing (a) game(s) of Jump61.
 *  @author Alan Ponte
 */
class Game {

    /** Name of resource containing help message. */
    private static final String HELP = "jump61/Help.txt";

    /** A new Game that takes command/move input from INPUT, prints
     *  normal output on OUTPUT, prints prompts for input on PROMPTS,
     *  and prints error messages on ERROROUTPUT. The Game now "owns"
     *  INPUT, PROMPTS, OUTPUT, and ERROROUTPUT, and is responsible for
     *  closing them when its play method returns. */
    Game(Reader input, Writer prompts, Writer output, Writer errorOutput) {
        _board = new MutableBoard(Defaults.BOARD_SIZE);
        _readonlyBoard = new ConstantBoard(_board);
        _prompter = new PrintWriter(prompts, true);
        _inp = new Scanner(input);
        _inp.useDelimiter("(?m)$|^|\\p{Blank}");
        _out = new PrintWriter(output, true);
        _err = new PrintWriter(errorOutput, true);
        _p1 = new HumanPlayer(this, RED);
        _p2 = new AI(this, BLUE);
        _prevMoves = new Stack<String>();
        _verbose = false;
    }

    /** Returns a readonly view of the game board.  This board remains valid
     *  throughout the session. */
    Board getBoard() {
        return _readonlyBoard;
    }

    /** Play a session of Jump61.  This may include multiple games,
     *  and proceeds until the user exits.  Returns an exit code: 0 is
     *  normal; any positive quantity indicates an error.  */
    int play() {
        _out.println("Welcome to " + Defaults.VERSION);
        while (!_playing) {
            promptForNext();
            readExecuteCommand();
        }
        startGame();
        _out.flush();
        System.exit(0);
        return 0;
    }

    /** Get a move from my input and place its row and column in
     *  MOVE.  Returns true if this is successful, false if game stops
     *  or ends first. */
    boolean getMove(int[] move) {
        while (_playing && _move[0] == 0 && promptForNext()) {
            readExecuteCommand();
        }
        if (_move[0] > 0) {
            move[0] = _move[0];
            move[1] = _move[1];
            _move[0] = 0;
            return true;
        } else {
            return false;
        }
    }

    /** Add a spot to R C, if legal to do so. */
    void makeMove(int r, int c) {
        makeMove(_board.sqNum(r, c));
    }

    /** Add a spot to square #N, if legal to do so. */
    void makeMove(int n) {
        if (_board.isLegal(_board.whoseMove(), n)) {
            _board.addSpot(_board.whoseMove(), n);
        } else {
            _err.println("This is not a legal move. Please try again");
        }
    }

    /** Return a random integer in the range [0 .. N), uniformly
     *  distributed.  Requires N > 0. */
    int randInt(int n) {
        return _random.nextInt(n);
    }

    /** Send a message to the user as determined by FORMAT and ARGS, which
     *  are interpreted as for String.format or PrintWriter.printf. */
    void message(String format, Object... args) {
        _out.printf(format, args);
    }

    /** Check whether we are playing and there is an unannounced winner.
     *  If so, announce and stop play. */
    private void checkForWin() {
        if (_board.won()) {
            announceWinner();
            _playing = false;
        }
    }

    /** Send announcement of winner to my user output. */
    private void announceWinner() {
        _out.printf("%s wins.\n", getBoard().getWinner().toCapitalizedString());
    }

    /** Make player #PLAYER (1 or 2) an AI for subsequent moves. */
    private void setAuto(String player) {
        if (!player.matches("[Rr][Ee][Dd]")
                && !player.matches("[Bb][Ll][Uu][Ee]")) {
            throw error("Player number must be either red or blue");
        } else if (player.matches("[Rr][Ee][Dd]")) {
            _p1 = new AI(this, _p1.getColor());
        } else {
            _p2 = new AI(this, _p2.getColor());
        }
    }

    /** Make player #PLAYER (1 or 2) take manual input from the user
     * for subsequent moves. */
    private void setManual(String player) {
        if (!player.matches("[Rr][Ee][Dd]")
                && !player.matches("[Bb][Ll][Uu][Ee]")) {
            throw error("Player number must be either 1 or 2");
        } else if (player.matches("[Rr][Ee][Dd]")) {
            _p1 = new HumanPlayer(this, _p1.getColor());
        } else {
            _p2 = new HumanPlayer(this, _p2.getColor());
        }
    }

    /** Make player 1 play the COLOR pieces
     *  on subsequent moves.*/
    private void setPlayer1(Color color) {
        _p1.setColor(color);
    }

    /** Stop any current game and clear the board to its initial
     *  state. */
    private void clear() {
        _playing = false;
        _board.clear(_board.size());
    }

    /** Print the current board using standard board-dump format. */
    private void dump() {
        _out.println(_board);
    }

    /** Print a help message. */
    private void help() {
        Main.printHelpResource(HELP, _out);
    }

    /** Stop any current game and set the move number to N. */
    private void setMoveNumber(int n) {
        _playing = false;
        _board.setMoves(n);
    }

    /** Seed the random-number generator with SEED. */
    private void setSeed(long seed) {
        _random.setSeed(seed);
    }

    /** Place SPOTS spots on square R:C and color the square red or
     *  blue depending on whether COLOR is "r" or "b".  If SPOTS is
     *  0, clears the square, ignoring COLOR.  SPOTS must be less than
     *  the number of neighbors of square R, C. */
    private void setSpots(int r, int c, int spots, String color) {
        _playing = false;
        if (color.equals("r")) {
            _board.set(r, c, spots, RED);
        } else if (color.equals("b")) {
            _board.set(r, c, spots, BLUE);
        } else {
            throw error("Color must be either r or b");
        }
    }

    /** Stop any current game and set the board to an empty N x N board
     *  with numMoves() == 0.  */
    private void setSize(int n) {
        _playing = false;
        _board.clear(n);
    }

    /** Begin accepting moves for game.  If the game is won,
     *  immediately print a win message and end the game. */
    private void startGame() {
        _playing = true;
        checkForWin();
        while (_playing) {
            _p1.makeMove();
            checkForWin();
            if (!_playing) {
                break;
            }
            _p2.makeMove();
            checkForWin();
        }
    }

    /** Save move R C in _move.  Error if R and C do not indicate an
     *  existing square on the current board. */
    private void saveMove(int r, int c) {
        if (!_board.exists(r, c)) {
            throw error("move %d %d out of bounds", r, c);
        }
        _move[0] = r;
        _move[1] = c;
    }

    /** Read and execute one command.  Leave the input at the start of
     *  a line, if there is more input. */
    private void readExecuteCommand() {
        executeCommand(_inp.nextLine());
    }

    /**Prints all previous moves made.*/
    private void printPrevMoves() {
        _prevMoves.pop();
        _out.println("The previous moves were : \n" + _prevMoves.toString());
    }

    /** Quits the current game.*/
    private void quit() {
        _out.flush();
        _out.close();
        _err.flush();
        _out.close();
        System.exit(0);
    }

    /** Prints an error message with the invalid arguments
     *  from CMD.*/
    private void printInvalidArgs(String cmd) {
        _err.printf("Invalid arguments for command '%s'. "
                + "Please try again.\n", cmd);

    }

    /** Saves the move from the command CMD
     *  and argument ARG.*/
    private void save(String cmd, int arg) {
        if (cmd.matches("\\d+") && _playing) {
            saveMove(Integer.parseInt(cmd), arg);
        }
    }
    /** Gather arguments and execute the correct
     *  command from the input INPUT. Throws GameException
     *  on errors. */
    private void executeCommand(String input) {
        _prevMoves.add(input);
        String[] args = input.trim().split(" ");
        String cmnd = args[0];
        try {
            if (cmnd.matches("\\d+") && _playing) {
                saveMove(Integer.parseInt(cmnd), Integer.parseInt(args[1]));
                return;
            }
            switch (cmnd) {
            case "\n": case "\r\n": case "":
                return;
            case "#":
                break;
            case "help":
                help();
                break;
            case "clear":
                clear();
                break;
            case "start":
                startGame();
                break;
            case "quit":
                quit();
                break;
            case "auto":
                setAuto(args[1]);
                break;
            case "manual":
                setManual(args[1]);
                break;
            case "size":
                setSize(Integer.parseInt(args[1]));
                break;
            case "move":
                setMoveNumber(Integer.parseInt(args[1]));
                break;
            case "set":
                setSpots(Integer.parseInt(args[1]), Integer.parseInt(args[2]),
                    Integer.parseInt(args[3]), (args[4]));
                break;
            case "dump":
                dump();
                break;
            case "seed":
                setSeed(Integer.parseInt(args[1]));
                break;
            default:
                throw error("bad command: '%s'", cmnd);
            }
        } catch (NumberFormatException e) {
            printInvalidArgs(cmnd);
        } catch (GameException e) {
            _err.println(e.getMessage());
        } catch (ArrayIndexOutOfBoundsException e) {
            _err.println("Please enter both a row and a column for a move");
        }
    }

    /** Print a prompt and wait for input. Returns true iff there is another
     *  token.  If verbose is on, display the game board after every input. */
    private boolean promptForNext() {
        if (_playing) {
            if (_verbose) {
                dump();
            }
            _prompter.printf("%s>", _board.whoseMove());
        } else {
            _prompter.printf(">");
        }
        return true;
    }

    /** Send an error message to the user formed from arguments FORMAT
     *  and ARGS, whose meanings are as for printf. */
    void reportError(String format, Object... args) {
        _err.print("Error: ");
        _err.printf(format, args);
        _err.println();
    }

    /** Writer on which to print prompts for input. */
    private final PrintWriter _prompter;
    /** Scanner from current game input.  Initialized to return
     *  newlines as tokens. */
    private final Scanner _inp;
    /** Outlet for responses to the user. */
    private final PrintWriter _out;
    /** Outlet for error responses to the user. */
    private final PrintWriter _err;

    /** The board on which I record all moves. */
    protected final Board _board;
    /** A readonly view of _board. */
    private final Board _readonlyBoard;
    /** The first player in THIS game. */
    private Player _p1;
    /** The second player in THIS game. */
    private Player _p2;

    /** A pseudo-random number generator used by players as needed. */
    private final Random _random = new Random();

    /** True iff a game is currently in progress. */
    private boolean _playing;

   /** Used to return a move entered from the console.  Allocated
     *  here to avoid allocations. */
    private final int[] _move = new int[Defaults.NUMPLAYERS];

    /**True iff verbose is on.*/
    private boolean _verbose;

    /**The previous move commands made.*/
    private final Stack<String> _prevMoves;
}
