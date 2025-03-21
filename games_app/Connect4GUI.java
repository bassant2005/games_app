package games_app;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Connect4GUI {
    private JFrame frame;
    private JPanel boardPanel, controlPanel, bottomPanel;
    private JButton[] columnButtons;
    private JButton backButton;
    private JLabel[][] boardCells;
    private JLabel statusLabel;
    private String[][] board;
    private boolean isPlayerTurn = true;
    private boolean playVsComputer;
    private Random random = new Random();
    private ImageIcon redPiece, yellowPiece, emptyCell;

    public Connect4GUI() {
        chooseGameMode();
        initializeGame();
        initializeGUI();
    }

    private void chooseGameMode() {
        Object[] options = {"Play vs Friend", "Play vs Computer"};
        int choice = JOptionPane.showOptionDialog(null, "Choose Game Mode", "Connect 4",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        playVsComputer = (choice == 1);
    }

    private void initializeGame() {
        board = new String[6][7];
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                board[i][j] = "-";
            }
        }
        isPlayerTurn = true;
    }

    private ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image resizedImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImg);
    }

    private void initializeGUI() {
        if (frame != null) {
            frame.dispose();
        }

        redPiece = resizeIcon(new ImageIcon(getClass().getResource("player_red.jpg")), 80, 80);
        yellowPiece = resizeIcon(new ImageIcon(getClass().getResource("player_yellow.jpg")), 80, 80);
        emptyCell = resizeIcon(new ImageIcon(getClass().getResource("withe.jpg")), 80, 80);

        frame = new JFrame("Connect 4");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLayout(new BorderLayout());

        boardPanel = new JPanel(new GridLayout(6, 7));
        boardCells = new JLabel[6][7];

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                boardCells[i][j] = new JLabel(emptyCell, SwingConstants.CENTER);
                boardCells[i][j].setOpaque(true);
                boardCells[i][j].setBackground(Color.WHITE);
                boardPanel.add(boardCells[i][j]);
            }
        }

        controlPanel = new JPanel(new GridLayout(1, 7));
        columnButtons = new JButton[7];
        for (int i = 0; i < 7; i++) {
            final int column = i;
            columnButtons[i] = new JButton("Drop");
            columnButtons[i].setFont(new Font("Arial", Font.BOLD, 25));
            columnButtons[i].addActionListener(e -> makeMove(column));
            controlPanel.add(columnButtons[i]);
        }

        bottomPanel = new JPanel(new GridLayout(1, 2));

        JButton restartButton = new JButton("Restart");
        restartButton.setFont(new Font("Arial", Font.BOLD, 40));
        restartButton.addActionListener(e -> {
            frame.dispose();
            Connect4GUI.playing();
        });

        backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 40));
        backButton.addActionListener(e -> {
            frame.dispose();
            new GameMenu();
        });

        bottomPanel.add(restartButton);
        bottomPanel.add(backButton);

        statusLabel = new JLabel("Player 1's Turn", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 25));

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(statusLabel, BorderLayout.NORTH);
        southPanel.add(bottomPanel, BorderLayout.SOUTH);

        frame.getContentPane().removeAll();
        frame.add(controlPanel, BorderLayout.NORTH);
        frame.add(boardPanel, BorderLayout.CENTER);
        frame.add(southPanel, BorderLayout.SOUTH);

        frame.revalidate();
        frame.repaint();
        frame.setVisible(true);
    }

    private void makeMove(int column) {
        for (int i = 5; i >= 0; i--) {
            if (board[i][column].equals("-")) {
                board[i][column] = isPlayerTurn ? "R" : "Y";
                boardCells[i][column].setIcon(isPlayerTurn ? redPiece : yellowPiece);

                if (checkWin(board[i][column])) {
                    if (playVsComputer && !isPlayerTurn) {
                        BoardGameGUI.playSound("beated-by-a-computer-by-tromosm-281034.wav");
                    } else {
                        BoardGameGUI.playSound("goodresult-82807.wav");
                    }
                    statusLabel.setText((isPlayerTurn ? "Player 1" : "Player 2") + " wins!");
                    disableButtons();
                    return;
                }
                if (isDraw()) {
                    BoardGameGUI.playSound("game-bonus-144751.wav");
                    statusLabel.setText("It's a draw!");
                    disableButtons();
                    return;
                }

                isPlayerTurn = !isPlayerTurn;
                statusLabel.setText(isPlayerTurn ? "Player 1's Turn" : "Player 2's Turn");
                if (playVsComputer && !isPlayerTurn) {
                    computerMove();
                }
                return;
            }
        }
    }

    private void computerMove() {
        Timer timer = new Timer(1000, e -> {
            int column;
            do {
                column = random.nextInt(7);
            } while (!board[0][column].equals("-"));

            makeMove(column);
            ((Timer) e.getSource()).stop();
        });

        timer.setRepeats(false);
        timer.start();
    }


    private boolean checkWin(String symbol) {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7 - 3; j++) {
                if (board[i][j].equals(symbol) && board[i][j + 1].equals(symbol) &&
                        board[i][j + 2].equals(symbol) && board[i][j + 3].equals(symbol)) {
                    return true;
                }
            }
        }

        for (int i = 0; i < 6 - 3; i++) {
            for (int j = 0; j < 7; j++) {
                if (board[i][j].equals(symbol) && board[i + 1][j].equals(symbol) &&
                        board[i + 2][j].equals(symbol) && board[i + 3][j].equals(symbol)) {
                    return true;
                }
            }
        }

        for (int i = 0; i < 6 - 3; i++) {
            for (int j = 0; j < 7 - 3; j++) {
                if (board[i][j].equals(symbol) && board[i + 1][j + 1].equals(symbol) &&
                        board[i + 2][j + 2].equals(symbol) && board[i + 3][j + 3].equals(symbol)) {
                    return true;
                }
            }
        }

        for (int i = 3; i < 6; i++) {
            for (int j = 0; j < 7 - 3; j++) {
                if (board[i][j].equals(symbol) && board[i - 1][j + 1].equals(symbol) &&
                        board[i - 2][j + 2].equals(symbol) && board[i - 3][j + 3].equals(symbol)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isDraw() {
        for (String cell : board[0]) {
            if (cell.equals("-")) return false;
        }
        return true;
    }

    private void disableButtons() {
        for (JButton button : columnButtons) {
            button.setEnabled(false);
        }
    }

    public static void playing() {
        SwingUtilities.invokeLater(Connect4GUI::new);
    }
}