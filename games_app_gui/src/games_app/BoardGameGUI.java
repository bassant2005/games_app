package games_app;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import javax.sound.sampled.*;

public class BoardGameGUI {
    private JFrame frame;
    private JPanel boardPanel;
    private JLabel statusLabel, diceLabel, scoreLabel;
    private JButton rollButton;
    private Random random = new Random();
    private Map<String, String> judgeCards = new HashMap<>();
    private Set<Integer> bonusCards, minusCards, judgeCardPositions;
    private int player1Position = 1, player2Position = 1;
    private int player1Score = 0, player2Score = 0;
    private boolean isPlayer1Turn = true;
    private ImageIcon bonusIcon, minusIcon, judgeIcon, player1Icon, player2Icon, bothPlayersIcon;
    private ImageIcon[] diceFaces;

    public BoardGameGUI() {
        loadImages();
        initializeJudgeCards();
        initializeSpecialCards();
        initializeGUI();
    }

    private void loadImages() {
        bonusIcon = loadImage("bonus.jpg");
        minusIcon = loadImage("minus.jpg");
        judgeIcon = loadImage("judge.jpg");
        player1Icon = loadImage("player_red.jpg");
        player2Icon = loadImage("player_blue.jpg");
        bothPlayersIcon = loadImage("player_purple.jpg");

        diceFaces = new ImageIcon[6];
        for (int i = 0; i < 6; i++) {
            diceFaces[i] = loadImage("dice" + (i + 1) + ".jpg");
        }
    }

    private static ImageIcon loadImage(String path) {
        java.net.URL location = BoardGameGUI.class.getResource(path);
        if (location != null) {
            ImageIcon originalIcon = new ImageIcon(location);
            Image resizedImage = originalIcon.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);
            return new ImageIcon(resizedImage);
        }
        return new ImageIcon();
    }

    private void initializeGUI() {
        frame = new JFrame("Board Game 🎲");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        boardPanel = new JPanel(new GridLayout(7, 8));
        frame.add(boardPanel, BorderLayout.CENTER);

        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        statusLabel = new JLabel("Player 1's turn");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 40));

        scoreLabel = new JLabel("Scores - Player 1: 0 | Player 2: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 40));

        infoPanel.add(statusLabel);
        infoPanel.add(scoreLabel);
        frame.add(infoPanel, BorderLayout.NORTH);

        diceLabel = new JLabel(diceFaces[0]);
        diceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(diceLabel, BorderLayout.EAST);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
        rollButton = new JButton("Roll Dice");
        rollButton.setFont(new Font("Arial", Font.BOLD, 40));
        rollButton.addActionListener(e -> rollDice());

        JButton restartButton = new JButton("Restart");
        restartButton.setFont(new Font("Arial", Font.BOLD, 40));
        restartButton.addActionListener(e -> {
            frame.dispose();
            BoardGameGUI.playing();
        });

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 40));
        backButton.addActionListener(e -> {
            frame.dispose();
            new GameMenu();
        });

        buttonPanel.add(rollButton);
        buttonPanel.add(restartButton);
        buttonPanel.add(backButton);

        frame.add(buttonPanel, BorderLayout.SOUTH);
        updateBoard();
        frame.setVisible(true);
    }

    private void updateBoard() {
        boardPanel.removeAll();
        for (int i = 1; i <= 56; i++) {
            JLabel cellLabel = new JLabel();
            cellLabel.setHorizontalAlignment(SwingConstants.CENTER);
            cellLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

            if (bonusCards.contains(i)) {
                cellLabel.setIcon(bonusIcon);
            } else if (minusCards.contains(i)) {
                cellLabel.setIcon(minusIcon);
            } else if (judgeCardPositions.contains(i)) {
                cellLabel.setIcon(judgeIcon);
            } else {
                cellLabel.setFont(new Font("Arial", Font.BOLD, 40));
                cellLabel.setText(String.valueOf(i));
            }

            if (i == player1Position && i == player2Position) {
                cellLabel.setIcon(bothPlayersIcon);
            } else if (i == player1Position) {
                cellLabel.setIcon(player1Icon);
            } else if (i == player2Position) {
                cellLabel.setIcon(player2Icon);
            }

            boardPanel.add(cellLabel);
        }

        boardPanel.revalidate();
        boardPanel.repaint();
    }

    private void initializeJudgeCards() {
        judgeCards.put("watermelon", "What is the red-colored fruit that consists of 10 letters?");
        judgeCards.put("0", "What is 1 ÷ infinite ?(answer with a number)");
        judgeCards.put("water", "What do cows drink?");
        judgeCards.put("bamboo", "What do pandas eat?");
        judgeCards.put("diamond", "What is the hardest natural substance on Earth?");
        judgeCards.put("xvi", "What is the Roman numeral for 16?");
        judgeCards.put("hydrogen", "Which element has atomic number 1?");
        judgeCards.put("venus", "Which planet is known as the morning star?");
        judgeCards.put("8", "How many legs does a spider have?(answer with a number)");
        judgeCards.put("pacific", "What is the largest ocean on Earth?");
        judgeCards.put("cheetah", "What is the fastest land animal?");
        judgeCards.put("mitochondria", "What is the powerhouse of the cell?");
        judgeCards.put("gallium", "Which metal melts at human body temperature?");
        judgeCards.put("fibonacci", "Which mathematical sequence starts with 0 and 1, with each term being the sum of the previous two?");
    }

    private void initializeSpecialCards() {
        Set<Integer> allPositions = new HashSet<>();
        bonusCards = new HashSet<>();
        minusCards = new HashSet<>();
        judgeCardPositions = new HashSet<>();

        while (bonusCards.size() < 5) {
            int pos = random.nextInt(56 - 2) + 2;
            if (!allPositions.contains(pos)) {
                bonusCards.add(pos);
                allPositions.add(pos);
            }
        }

        while (minusCards.size() < 5) {
            int pos = random.nextInt(56 - 2) + 2;
            if (!allPositions.contains(pos)) {
                minusCards.add(pos);
                allPositions.add(pos);
            }
        }

        while (judgeCardPositions.size() < 7) {
            int pos = random.nextInt(56 - 2) + 2;
            if (!allPositions.contains(pos)) {
                judgeCardPositions.add(pos);
                allPositions.add(pos);
            }
        }
    }

    private void rollDice() {
        int diceRoll = random.nextInt(6) + 1;
        diceLabel.setIcon(diceFaces[diceRoll - 1]);

        if (isPlayer1Turn) {
            player1Position = Math.min(player1Position + diceRoll, 56);
        } else {
            player2Position = Math.min(player2Position + diceRoll, 56);
        }

        checkSpecialCard();
        updateBoard();

        if (player1Position == 56 || player2Position == 56) {
            determineWinner();
            return;
        }

        isPlayer1Turn = !isPlayer1Turn;
        statusLabel.setText(isPlayer1Turn ? "Player 1's turn" : "Player 2's turn");
    }

    private void checkSpecialCard() {
        int currentPosition = isPlayer1Turn ? player1Position : player2Position;

        if (bonusCards.contains(currentPosition)) {
            if (isPlayer1Turn) player1Score += 3;
            else player2Score += 3;
        } else if (minusCards.contains(currentPosition)) {
            if (isPlayer1Turn) player1Score -= 1;
            else player2Score -= 1;

            // Move back 3 spaces if possible
            if (isPlayer1Turn) player1Position = Math.max(1, player1Position - 3);
            else player2Position = Math.max(1, player2Position - 3);
        } else if (judgeCardPositions.contains(currentPosition)) {
            askJudgeQuestion();
        }

        scoreLabel.setText("Scores - Player 1: " + player1Score + " | Player 2: " + player2Score);
    }

    private void askJudgeQuestion() {
        List<String> keys = new ArrayList<>(judgeCards.keySet());
        String correctAnswer = keys.get(random.nextInt(keys.size()));
        String question = judgeCards.get(correctAnswer);

        String answer = JOptionPane.showInputDialog(frame, question);
        if (answer != null && answer.equalsIgnoreCase(correctAnswer)) {
            if (isPlayer1Turn) player1Score += 2;
            else player2Score += 2;
        } else {
            if (isPlayer1Turn) player1Score -= 1;
            else player2Score -= 1;
        }
    }

    public void determineWinner() {
        String winnerMessage;
        String soundFile;

        if (player1Score > player2Score) {
            winnerMessage = "<html><center><h1>🏆 Player 1 Wins!</h1></center></html>";
            soundFile = "goodresult-82807.wav";
        } else if (player2Score > player1Score) {
            winnerMessage = "<html><center><h1>🏆 Player 2 Wins!</h1></center></html>";
            soundFile = "goodresult-82807.wav";
        } else {
            winnerMessage = "<html><center><h1>🤝 It's a Draw!</h1></center></html>";
            soundFile = "game-bonus-144751.wav";
        }

        new Thread(() -> playSound(soundFile)).start();

        JLabel messageLabel = new JLabel(winnerMessage, SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 28));

        JLabel scoreLabel = new JLabel("<html><center><b>Final Scores:</b><br> Player 1: "
                + player1Score + "<br> Player 2: " + player2Score + "</center></html>", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 28));

        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.add(messageLabel);
        panel.add(scoreLabel);

        JOptionPane.showMessageDialog(frame, panel, "🎉 Game Over 🎉", JOptionPane.PLAIN_MESSAGE);
    }

    public static void playSound(String soundFile) {
        try {
            java.net.URL url = BoardGameGUI.class.getResource(soundFile);
            if (url != null) {
                Clip clip = AudioSystem.getClip();
                clip.open(AudioSystem.getAudioInputStream(url));
                clip.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void playing() {
        new BoardGameGUI();
    }
}