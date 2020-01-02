package tablut;

import static java.lang.Math.*;
import static tablut.Piece.*;

/**
 * A Player that automatically generates moves.
 *
 * @author Wen Zeng
 */
class AI extends Player {

    /**
     * A position-score magnitude indicating a win (for white if positive,
     * black if negative).
     */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 20;
    /**
     * A position-score magnitude indicating a forced win in a subsequent
     * move.  This differs from WINNING_VALUE to avoid putting off wins.
     */
    private static final int WILL_WIN_VALUE = Integer.MAX_VALUE - 40;
    /**
     * A magnitude greater than a normal value.
     */
    private static final int INFTY = Integer.MAX_VALUE;

    /**
     * A new AI with no piece or controller (intended to produce
     * a template).
     */
    AI() {
        this(null, null);
    }

    /**
     * A new AI playing PIECE under control of CONTROLLER.
     */
    AI(Piece piece, Controller controller) {
        super(piece, controller);
    }

    @Override
    Player create(Piece piece, Controller controller) {
        return new AI(piece, controller);
    }

    @Override
    String myMove() {
        /** FIXME */
        String movecommand = findMove().toString();
        _controller.reportMove(_lastFoundMove);
        return movecommand;
    }

    @Override
    boolean isManual() {
        return false;
    }

    /**
     * Return a move for me from the current position, assuming there
     * is a move.
     */
    private Move findMove() {
        /** FIXME */
        Board b = new Board(board());
        _lastFoundMove = null;
        findMove(b, maxDepth(b), true,
                b.turn() == WHITE ? 1 : -1, -INFTY, INFTY);
        return _lastFoundMove;
    }

    /**
     * The move found by the last call to one of the ...FindMove methods
     * below.
     */
    private Move _lastFoundMove;

    /**
     * Find a move from position BOARD and return its value, recording
     * the move found in _lastFoundMove iff SAVEMOVE. The move
     * should have maximal value or have value > BETA if SENSE==1,
     * and minimal value or value < ALPHA if SENSE==-1. Searches up to
     * DEPTH levels.  Searching at level 0 simply returns a static estimate
     * of the board value and does not set _lastMoveFound.
     */
    private int findMove(Board board, int depth, boolean saveMove,
                         int sense, int alpha, int beta) {
        /** FIXME */
        if (depth == 0) {
            return staticScore(board);
        }
        int maxVal = INFTY * -sense;
        for (int i = 0; i < board.legalMoves(board.turn()).size(); i++) {
            Move move = board.legalMoves(board.turn()).get(i);
            board.makeMove(move);
            int value;
            if (board.winner() == null) {
                value = findMove(board, depth - 1,
                        false, sense * -1, alpha, beta);
                if (sense == 1 && value == WINNING_VALUE) {
                    value = WILL_WIN_VALUE;
                } else if (sense == -1 && value == -WINNING_VALUE) {
                    value = -WILL_WIN_VALUE;
                }
            } else if (board.winner() == WHITE) {
                value = WINNING_VALUE;
            } else {
                value = -WINNING_VALUE;
            }
            board.undo();
            if (sense == 1) {
                if (value > maxVal) {
                    if (saveMove) {
                        _lastFoundMove = move;
                    }
                    maxVal = value;
                    alpha = max(alpha, value);
                }
            } else {
                if (value < maxVal) {
                    if (saveMove) {
                        _lastFoundMove = move;
                    }
                    maxVal = value;
                    beta = min(beta, value);
                }
            }
            if (beta <= alpha) {
                break;
            }
        }
        return maxVal;
    }

    /**
     * Return a heuristically determined maximum search depth
     * based on characteristics of BOARD.
     */
    private static int maxDepth(Board board) {
        /** FIXME? */
        return 4;
    }

    /**
     * Return a heuristic value for BOARD.
     */
    private int staticScore(Board board) {
        /** FIXME */
        int x = min(board.kingPosition().col(), 8 - board.kingPosition().col());
        int y = min(board.kingPosition().row(), 8 - board.kingPosition().row());
        int count = 0;
        Square sq;
        for (int i = 0; i < 4; i++) {
            sq = board.kingPosition().rookMove(i, 1);
            if (board.get(sq) == BLACK) {
                count++;
            }
        }
        return -min(x, y) - count;
    }
    /**
     * FIXME: More here.
     */

}


