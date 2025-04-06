import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Board extends JPanel {
    int[][] gameBoard;
    boolean playerTurn = true;
    final int gridSize = 3;
    final int tileSize = 100;
    final int boardSize = gridSize * tileSize;
    private int[][] winningTiles = new int[3][3];
    private boolean playerIsX = true;
    private AIPlayer aiPlayer;
    private int aiDepth = 3;
    private boolean useAlphaBeta = true;

    Board() {
        setPreferredSize(new Dimension(boardSize, boardSize));
        setBackground(Color.BLACK);
        gameBoard = new int[gridSize][gridSize];

        // Create AI player
        aiPlayer = new AIPlayer(aiDepth, useAlphaBeta, !playerIsX);

        // Listen for mouse clicks
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMouseClick(e);
            }
        });
    }

    private boolean checkWinner(int player) {
        winningTiles = new int[3][3];
        for (int i = 0; i < gridSize; i++) {
            if (gameBoard[i][0] == player && gameBoard[i][1] == player && gameBoard[i][2] == player) {
                System.out.println("Winning row: " + i);
                winningTiles[i][0] = 1;
                winningTiles[i][1] = 1;
                winningTiles[i][2] = 1;
                return true;
            }
        }
        for (int i = 0; i < gridSize; i++) {
            if (gameBoard[0][i] == player && gameBoard[1][i] == player && gameBoard[2][i] == player) {
                System.out.println("Winning column: " + i);
                winningTiles[0][i] = 1;
                winningTiles[1][i] = 1;
                winningTiles[2][i] = 1;
                return true;
            }
        }
        if (gameBoard[0][0] == player && gameBoard[1][1] == player && gameBoard[2][2] == player) {
            System.out.println("Winning main diagonal!");
            winningTiles[0][0] = 1;
            winningTiles[1][1] = 1;
            winningTiles[2][2] = 1;
            return true;
        }
        if (gameBoard[0][2] == player && gameBoard[1][1] == player && gameBoard[2][0] == player) {
            System.out.println("Winning opposite diagonal!");
            winningTiles[0][2] = 1;
            winningTiles[1][1] = 1;
            winningTiles[2][0] = 1;
            return true;
        }
        return false;
    }

    private boolean isBoardFull() {
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                if (gameBoard[row][col] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private void resetGame() {
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                gameBoard[row][col] = 0;
                winningTiles[row][col] = 0;
            }
        }
        playerTurn = playerIsX;
        if (!playerTurn) {
            makeAIMove();
        }

        repaint();
    }

    public void setPlayerStartsAsX(boolean startsAsX) {
        playerIsX = startsAsX;
        playerTurn = startsAsX;
        aiPlayer = new AIPlayer(aiDepth, useAlphaBeta, !playerIsX);
        if (!playerTurn) {
            makeAIMove();
        }
    }

    public void setAISettings(int depth, boolean useAlphaBeta) {
        this.aiDepth = depth;
        this.useAlphaBeta = useAlphaBeta;
        aiPlayer.setMaxDepth(depth);
        aiPlayer.setUseAlphaBetaPruning(useAlphaBeta);
    }

    private void makeAIMove() {
        Timer timer = new Timer(500, e -> {
            Point bestMove = aiPlayer.findBestMove(gameBoard);
            if (bestMove != null) {
                int aiSymbol = playerIsX ? 2 : 1;
                gameBoard[bestMove.y][bestMove.x] = aiSymbol;
                playerTurn = !playerTurn;
                repaint();
                if (checkWinner(aiSymbol)) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, (aiSymbol == 1 ? "X" : "O") + " (AI) Wins!");
                        resetGame();
                    });
                } else if (isBoardFull()) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "It's a Draw!");
                        resetGame();
                    });
                }
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void handleMouseClick(MouseEvent e) {
        if (!playerTurn) return;
        int col = e.getX() / tileSize;
        int row = e.getY() / tileSize;

        if (gameBoard[row][col] == 0) {
            int playerSymbol = playerIsX ? 1 : 2;
            gameBoard[row][col] = playerSymbol;
            playerTurn = !playerTurn;
            repaint();
            if (checkWinner(playerSymbol)) {
                repaint();
                JOptionPane.showMessageDialog(this, (playerSymbol == 1 ? "X" : "O") + " (Player) Wins!");
                resetGame();
            } else if (isBoardFull()) {
                JOptionPane.showMessageDialog(this, "It's a Draw!");
                resetGame();
            } else {
                makeAIMove();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                if (winningTiles[row][col] == 1) {
                    g.setColor(Color.GREEN);
                    g.fillRect(col * tileSize, row * tileSize, tileSize, tileSize);
                }
            }
        }
        drawGrid(g);
        drawMoves(g);
    }

    private void drawGrid(Graphics g) {
        g.setColor(Color.WHITE);
        for (int i = 1; i < gridSize; i++) {
            g.drawLine(i * tileSize, 0, i * tileSize, boardSize);
            g.drawLine(0, i * tileSize, boardSize, i * tileSize);
        }
    }

    private void drawMoves(Graphics g) {
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                if (gameBoard[row][col] == 1) {
                    drawX(g, col, row);
                } else if (gameBoard[row][col] == 2) {
                    drawO(g, col, row);
                }
            }
        }
    }

    private void drawX(Graphics g, int col, int row) {
        g.setColor(Color.RED);
        int x1 = col * tileSize + 10;
        int y1 = row * tileSize + 10;
        int x2 = (col + 1) * tileSize - 10;
        int y2 = (row + 1) * tileSize - 10;

        g.drawLine(x1, y1, x2, y2);
        g.drawLine(x2, y1, x1, y2);
    }

    private void drawO(Graphics g, int col, int row) {
        g.setColor(Color.BLUE);
        int x = col * tileSize + 10;
        int y = row * tileSize + 10;
        int diameter = tileSize - 20;

        g.drawOval(x, y, diameter, diameter);
    }
}