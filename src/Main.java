import javax.swing.*;
import java.awt.*;

public class Main extends JPanel {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Tic-Tac-Toe with MinMax AI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        int symbolChoice = JOptionPane.showOptionDialog(null,
                "Choose Your Symbol", "Tic-Tac-Toe",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                new String[]{"Play as X", "Play as O"}, "Play as X");

        boolean playerStartsAsX = (symbolChoice == JOptionPane.YES_OPTION);
        String[] depthOptions = {"Easy (1)", "Medium (3)", "Hard (5)", "Very Hard (9)"};
        int depthChoice = JOptionPane.showOptionDialog(null,
                "Choose AI Difficulty", "Tic-Tac-Toe",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                depthOptions, depthOptions[1]);
        int aiDepth;
        switch (depthChoice) {
            case 0:
                aiDepth = 1;
                break;
            case 2:
                aiDepth = 5;
                break;
            case 3:
                aiDepth = 9;
                break;
            default:
                aiDepth = 3;
                break;
        }
        int pruningChoice = JOptionPane.showOptionDialog(null,
                "Use Alpha-Beta Pruning?", "Tic-Tac-Toe",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                new String[]{"Yes", "No"}, "Yes");
        boolean useAlphaBeta = (pruningChoice == JOptionPane.YES_OPTION);
        Board board = new Board();
        board.setPlayerStartsAsX(playerStartsAsX);
        board.setAISettings(aiDepth, useAlphaBeta);
        frame.add(board);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}