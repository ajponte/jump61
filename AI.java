package jump61;

/**
 * An automated Player.
 *
 * @author Alan Ponte
 */
class AI extends Player {

    /**
     * A new player of GAME initially playing COLOR that chooses moves
     * automatically.
     */
    AI(Game game, Color color) {
        super(game, color);
        _board = (MutableBoard) getGame()._board;
    }

    /** The A.I. makes a move.*/
    @Override
    void makeMove() {
        Game game = getGame();
        int randomCuttoff = (int) Math.random() * 10;
        AImove move = minmax(getColor(), _board,
                     Defaults.DEPTH, Integer.MAX_VALUE);
        game.makeMove(move.getMove());
        game.message("%s moves %d %d.\n",
                     getColor(), _board.row(move.getMove()),
                     _board.col(move.getMove()));
    }

    /**
     * Return the minimum of CUTOFF and the minmax value of board B (which must
     * be mutable) for player P to a search depth of D (where D == 0 denotes
     * evaluating just the next move). If MOVES is not null and CUTOFF is not
     * exceeded, set MOVES to a list of all highest-scoring moves for P; clear
     * it if non-null and CUTOFF is exceeded. the contents of B are invariant
     * over this call.
     * The value that is returned is a move object.
     */
    private AImove minmax(Color p, MutableBoard b, int d, int cutoff) {
        if (b.won() || d == 0) {
            return new AImove(-1, staticEval(p, b));
        }
        int size = getGame().getBoard().size();
        AImove bestSoFar = new AImove(-1, -Integer.MAX_VALUE);
        for (int i = 0; i < size * size; i++) {
            if (b.isLegal(p, i)) {
                b.addSpot(p, i);
                AImove response = minmax(p.opposite(), b, d - 1,
                                       -bestSoFar.getValue());
                b.undo();
                if (-response.getValue() >= bestSoFar.getValue()) {
                    AImove M = new AImove(i, -response.getValue());
                    bestSoFar = M;
                    if (M.getValue() >= cutoff) {
                        break;
                    }
                }
            }
        }
        return bestSoFar;
    }

    /**
     * Returns heuristic value of board B for player P. Higher is better for P.
     * Note:  Use -Integer.MAX_VALUE for -infinity instead of Integer.MAX_VALUE.
     */
    private int staticEval(Color p, Board b) {
        int color = 0;
        int opp = 0;
        if (b.won()) {
            if (b.squares[0].getColor() == p) {
                return Integer.MAX_VALUE;
            } else {
                return -Integer.MAX_VALUE;
            }
        }
        for (Board.Square square : b.squares) {
            if (square.getColor() == p) {
                color += 1;
            }
        }
        for (Board.Square square : b.squares) {
            if (square.getColor() == p.opposite()) {
                opp += 1;
            }
        }
        return color - opp;
    }

    @Override
    public MutableBoard getBoard() {
        return _board;
    }

    /**The board used by THIS AI.*/
    private MutableBoard _board;

    /** A single move made by the A.I.  Every MOVE has a
     *  heuristic VALUE.*/
    class AImove {

        /** Creates a new A.I. move with MOVE and the
         *  heuristic value VAL.*/
        AImove(int move, int val) {
            _move = move;
            _value = val;
        }

        /** Sets the heuristic value, VAL, of THIS move.*/
        void setVal(int val) {
            _value = val;
        }

        /** Returns the heuristic value of THIS move.*/
        int getValue() {
            return _value;
        }

        /** Sets THIS current MOVE.*/
        void setMove(int move) {
            _move = move;
        }

        /**Returns THIS current move.*/
        int getMove() {
            return _move;
        }

        /**THIS move.*/
        private int _move;

        /**THIS heuristic value of THIS move.*/
        private int _value;
    }
}
