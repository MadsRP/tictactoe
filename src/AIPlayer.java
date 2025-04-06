import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class AIPlayer {
    private int[][] evaluationTable = {
            {3, 2, 3},
            {2, 4, 2},
            {3, 2, 3}
    };

    private int maxDepth;
    private boolean useAlphaBetaPruning;
    private int aiPlayer; // 1 for X, 2 for O

    // Statistics
    private int nodeCount = 0;
    private int pruneCount = 0;

    public AIPlayer(int maxDepth, boolean useAlphaBetaPruning, boolean aiIsX) {
        this.maxDepth = maxDepth;
        this.useAlphaBetaPruning = useAlphaBetaPruning;
        this.aiPlayer = aiIsX ? 1 : 2;
    }

    public Point findBestMove(int[][] board) {
        nodeCount = 0;
        pruneCount = 0;
        Point bestMove = null;
        int bestScore = Integer.MIN_VALUE;
        List<Point> availableMoves = getAvailableMoves(board);

        for (Point move : availableMoves) {
            board[move.y][move.x] = aiPlayer;
            int score;
            if (useAlphaBetaPruning) {
                score = minimax(board, 0, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
            } else {
                score = minimax(board, 0, false);
            }
            board[move.y][move.x] = 0;
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        System.out.println("AI evaluated " + nodeCount + " nodes");
        if (useAlphaBetaPruning) {
            System.out.println("Alpha-beta pruning cut off " + pruneCount + " nodes");
        }

        return bestMove;
    }

    private int minimax(int[][] board, int depth, boolean isMaximizing) {
        nodeCount++;

        if (checkWinner(board, aiPlayer)) return 10 - depth;
        if (checkWinner(board, aiPlayer == 1 ? 2 : 1)) return depth - 10;
        if (isBoardFull(board) || depth == maxDepth) return evaluate(board);

        if (isMaximizing) {
            int bestScore = Integer.MIN_VALUE;
            List<Point> availableMoves = getAvailableMoves(board);

            for (Point move : availableMoves) {
                board[move.y][move.x] = aiPlayer;
                int score = minimax(board, depth + 1, false);
                board[move.y][move.x] = 0;
                bestScore = Math.max(score, bestScore);
            }

            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            List<Point> availableMoves = getAvailableMoves(board);
            int opponent = (aiPlayer == 1) ? 2 : 1;

            for (Point move : availableMoves) {
                board[move.y][move.x] = opponent;
                int score = minimax(board, depth + 1, true);
                board[move.y][move.x] = 0;
                bestScore = Math.min(score, bestScore);
            }

            return bestScore;
        }
    }

    // Minimax with alpha-beta pruning
    private int minimax(int[][] board, int depth, boolean isMaximizing, int alpha, int beta) {
        nodeCount++;

        // Terminal conditions
        if (checkWinner(board, aiPlayer)) return 10 - depth;
        if (checkWinner(board, aiPlayer == 1 ? 2 : 1)) return depth - 10;
        if (isBoardFull(board) || depth == maxDepth) return evaluate(board);

        if (isMaximizing) {
            int bestScore = Integer.MIN_VALUE;
            List<Point> availableMoves = getAvailableMoves(board);

            for (Point move : availableMoves) {
                board[move.y][move.x] = aiPlayer;
                int score = minimax(board, depth + 1, false, alpha, beta);
                board[move.y][move.x] = 0;
                bestScore = Math.max(score, bestScore);
                alpha = Math.max(alpha, bestScore);

                if (beta <= alpha) {
                    pruneCount++;
                    break; // Beta cutoff
                }
            }

            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            List<Point> availableMoves = getAvailableMoves(board);
            int opponent = (aiPlayer == 1) ? 2 : 1;

            for (Point move : availableMoves) {
                board[move.y][move.x] = opponent;
                int score = minimax(board, depth + 1, true, alpha, beta);
                board[move.y][move.x] = 0;
                bestScore = Math.min(score, bestScore);
                beta = Math.min(beta, bestScore);

                if (beta <= alpha) {
                    pruneCount++;
                    break; // Alpha cutoff
                }
            }

            return bestScore;
        }
    }

    // Evaluate the current board state
    private int evaluate(int[][] board) {
        int score = 0;

        // Use evaluation table
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (board[row][col] == aiPlayer) {
                    score += evaluationTable[row][col];
                } else if (board[row][col] != 0) {
                    score -= evaluationTable[row][col];
                }
            }
        }

        return score;
    }

    private List<Point> getAvailableMoves(int[][] board) {
        List<Point> availableMoves = new ArrayList<>();

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (board[row][col] == 0) {
                    availableMoves.add(new Point(col, row));
                }
            }
        }

        return availableMoves;
    }

    private boolean checkWinner(int[][] board, int player) {

        for (int i = 0; i < 3; i++) {
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player) {
                return true;
            }
        }
        for (int i = 0; i < 3; i++) {
            if (board[0][i] == player && board[1][i] == player && board[2][i] == player) {
                return true;
            }
        }
        if (board[0][0] == player && board[1][1] == player && board[2][2] == player) {
            return true;
        }
        if (board[0][2] == player && board[1][1] == player && board[2][0] == player) {
            return true;
        }

        return false;
    }

    private boolean isBoardFull(int[][] board) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (board[row][col] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public int getNodeCount() {
        return nodeCount;
    }

    public int getPruneCount() {
        return pruneCount;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public void setUseAlphaBetaPruning(boolean useAlphaBetaPruning) {
        this.useAlphaBetaPruning = useAlphaBetaPruning;
    }
}