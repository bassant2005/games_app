package games_app;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.util.*;
import java.util.List;

public class MemoryGameGUI {
    private JFrame frame;
    private JPanel boardPanel, controlPanel;
    private JLabel statusLabel, scoreLabel;
    private JButton[] cardButtons;
    private List<String> cards;
    private Set<Integer> matchedSet;
    private int size, firstCard = -1, secondCard = -1, playerScore = 0, friendScore = 0, computerScore = 0;
    private boolean isPlayerTurn = true, playWithFriend = false, playAgainstComputer = false;
    private Random rand = new Random();
    private String[] imagePaths = {"101.jpg", "cat_and_dog.jpg", "corty_alp3p3.jpg", "Jerry.jpg", "dicster.jpg", "Mickey.jpg", "patercop.jpg", "RATATOUILLE.jpg", "shay.jpg", "spongpop.jpg", "jack.jpg"};
    private String bonusImage = "bonus.jpg";
    private Map<String, ImageIcon> imageMap = new HashMap<>();

    public MemoryGameGUI() {
        initializeGame();
        loadImages();
        initializeGUI();
    }

    private boolean loadImages() {
        boolean success = true;
        for (String path : imagePaths) {
            java.net.URL resource = getClass().getResource(path);
            if (resource != null) {
                ImageIcon icon = new ImageIcon(resource);
                Image resizedImage = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                imageMap.put(path, new ImageIcon(resizedImage));
            } else {
                success = false;
            }
        }
        java.net.URL bonusResource = getClass().getResource(bonusImage);
        if (bonusResource != null) {
            ImageIcon bonusIcon = new ImageIcon(bonusResource);
            imageMap.put(bonusImage, new ImageIcon(bonusIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
        } else {
            success = false;
        }
        return success;
    }

    private void initializeGame() {
        size = getBoardSize();
        cards = new ArrayList<>();
        matchedSet = new HashSet<>();

        int pairs = (size == 10) ? 5 : (size == 15 ? 7 : 11);
        for (int i = 0; i < pairs; i++) {
            cards.add(imagePaths[i]);
            cards.add(imagePaths[i]);
        }

        if (size > 10) {
            int bonusCount = (size == 15) ? 1 : 3;
            for (int i = 0; i < bonusCount; i++) {
                cards.add(bonusImage);
            }
        }

        Collections.shuffle(cards);
        setPlayMode();
    }

    private int getBoardSize() {
        String[] options = {"10", "15", "25"};
        String choice = (String) JOptionPane.showInputDialog(null, "Choose board size:", "Board Size",
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        return Integer.parseInt(choice);
    }

    String  choice = " ";
    private void setPlayMode() {
        String[] modes = {"Solo", "Against Friend", "Against Computer"};
       choice = (String) JOptionPane.showInputDialog(null, "Choose a mode:", "Game Mode",
                JOptionPane.QUESTION_MESSAGE, null, modes, modes[0]);

        playWithFriend = choice.equals("Against Friend");
        playAgainstComputer = choice.equals("Against Computer");
    }

    private void initializeGUI() {
        frame = new JFrame("Memory Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLayout(new BorderLayout());

        if(choice == "Solo"){
            statusLabel = new JLabel("it's solo mode ;)", SwingConstants.CENTER);
            statusLabel.setFont(new Font("Arial", Font.BOLD, 40));

            scoreLabel = new JLabel("player score : ", SwingConstants.CENTER);
            scoreLabel.setFont(new Font("Arial", Font.BOLD, 40));
        }
        else if(choice == "Against Computer"){
            statusLabel = new JLabel("it's Against Computer mode ;)", SwingConstants.CENTER);
            statusLabel.setFont(new Font("Arial", Font.BOLD, 40));

            scoreLabel = new JLabel("player score :  | computer score : ", SwingConstants.CENTER);
            scoreLabel.setFont(new Font("Arial", Font.BOLD, 40));
        }
        else{
            statusLabel = new JLabel("it's Against friend mode ;)", SwingConstants.CENTER);
            statusLabel.setFont(new Font("Arial", Font.BOLD, 40));

            scoreLabel = new JLabel("player1 score : | player2 score : ", SwingConstants.CENTER);
            scoreLabel.setFont(new Font("Arial", Font.BOLD, 40));
        }

        boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(size / 5, 5));
        cardButtons = new JButton[size];

        for (int i = 0; i < size; i++) {
            cardButtons[i] = new JButton();
            cardButtons[i].setPreferredSize(new Dimension(60, 60));
            final int index = i;
            cardButtons[i].addActionListener(e -> handleCardClick(index));
            boardPanel.add(cardButtons[i]);
        }

        JButton restartButton = new JButton("Restart");
        restartButton.setFont(new Font("Arial", Font.BOLD, 40));
        restartButton.addActionListener(e -> {
            frame.dispose();
            MemoryGameGUI.playing();
        });

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 40));
        backButton.addActionListener(e -> {
            frame.dispose();
            new GameMenu();
        });

        controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(2, 2));
        controlPanel.add(statusLabel);
        controlPanel.add(scoreLabel);
        controlPanel.add(restartButton);
        controlPanel.add(backButton);

        frame.add(boardPanel, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private void handleCardClick(int index) {
        if (matchedSet.contains(index) || firstCard == index) return;

        cardButtons[index].setIcon(imageMap.get(cards.get(index)));
        cardButtons[index].setBackground(Color.WHITE);

        if (firstCard == -1) {
            firstCard = index;
        } else {
            secondCard = index;
            javax.swing.Timer timer = new javax.swing.Timer(1000, e -> checkMatch());
            timer.setRepeats(false);
            timer.start();
        }
    }

    private void checkMatch() {
        if (firstCard == -1 || secondCard == -1) return;

        boolean firstIsBonus = cards.get(firstCard).equals(bonusImage);
        boolean secondIsBonus = cards.get(secondCard).equals(bonusImage);

        if (firstIsBonus && !secondIsBonus) {
            matchedSet.add(firstCard);
            updateScores(3);
            cardButtons[secondCard].setIcon(null);
            cardButtons[secondCard].setBackground(Color.WHITE);
        } else if (secondIsBonus && !firstIsBonus) {
            matchedSet.add(secondCard);
            updateScores(3);
            cardButtons[firstCard].setIcon(null);
            cardButtons[firstCard].setBackground(Color.WHITE);
        } else if (cards.get(firstCard).equals(cards.get(secondCard))) {
            matchedSet.add(firstCard);
            matchedSet.add(secondCard);
            updateScores(1);
        } else {
            cardButtons[firstCard].setIcon(null);
            cardButtons[secondCard].setIcon(null);
            cardButtons[firstCard].setBackground(Color.WHITE);
            cardButtons[secondCard].setBackground(Color.WHITE);
            updateScores(-1);
        }
        firstCard = -1;
        secondCard = -1;
        if (matchedSet.size() == size) {
            determineWinner();
        } else if (playAgainstComputer && !isPlayerTurn) {
            computerMove();
        }
    }

    private void updateScores(int points) {
        if (playWithFriend) {
            if (isPlayerTurn) playerScore += points;
            else friendScore += points;
        } else if (playAgainstComputer) {
            if (isPlayerTurn) playerScore += points;
            else computerScore += points;
        } else {
            playerScore += points;
        }
        isPlayerTurn = !isPlayerTurn;
        updateStatus();
    }

    private void updateStatus() {
        if(choice == "Solo"){
            scoreLabel.setText("Player : " + playerScore);
        }
        else if(choice == "Against Computer"){
            scoreLabel.setText("Player : " + playerScore + " | Computer : " + computerScore);
        }
        else{
            scoreLabel.setText("Player1 : " + playerScore + " | player2 : " + friendScore);
        }
    }

    private void computerMove() {
        Timer timer = new Timer(1000, e -> {
            List<Integer> availableMoves = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                if (!matchedSet.contains(i)) availableMoves.add(i);
            }

            if (availableMoves.isEmpty()) return;

            int randomMove1 = 0 , randomMove2 = 0 ;
            while (randomMove1 == randomMove2 || matchedSet.contains(randomMove2) || matchedSet.contains(randomMove1)) {
                randomMove1 = availableMoves.get(rand.nextInt(availableMoves.size()));
                randomMove2 = availableMoves.get(rand.nextInt(availableMoves.size()));
            }

            handleCardClick(randomMove1);
            handleCardClick(randomMove2);
            ((Timer) e.getSource()).stop();
        });

        timer.setRepeats(false); // Ensure it runs only once
        timer.start();
    }

    public void determineWinner() {
        String winnerMessage;
        String soundFile;
        JLabel messageLabel;

        if(choice.equals("Solo")){
            winnerMessage = "<html><center><h1>Good job ☜(⌒▽⌒)☞</h1></center></html>";
            soundFile = "goodresult-82807.wav";
            messageLabel = new JLabel(winnerMessage, SwingConstants.CENTER);
            messageLabel.setFont(new Font("Arial", Font.BOLD, 28));

            JLabel finalScoreLabel = new JLabel("<html><center><b>Final Score:</b><br> Player: "
                    + playerScore + "</center></html>", SwingConstants.CENTER);
            finalScoreLabel.setFont(new Font("Arial", Font.PLAIN, 28));

            showGameOverDialog(messageLabel, finalScoreLabel, soundFile);
        }
        else {
            if (playerScore > friendScore || playerScore > computerScore) {
                winnerMessage = "<html><center><h1>🏆 Player 1 Wins!</h1></center></html>";
                soundFile = "goodresult-82807.wav";
            } else if (playerScore < friendScore) {
                winnerMessage = "<html><center><h1>🏆 Player 2 Wins!</h1></center></html>";
                soundFile = "goodresult-82807.wav";
            } else if (playerScore < computerScore) {
                winnerMessage = "<html><center><h1>🏆 Computer Wins!</h1></center></html>";
                soundFile = "beated-by-a-computer-by-tromosm-281034.wav";
            } else {
                winnerMessage = "<html><center><h1>🤝 It's a Draw!</h1></center></html>";
                soundFile = "game-bonus-144751.wav";
            }

            messageLabel = new JLabel(winnerMessage, SwingConstants.CENTER);
            messageLabel.setFont(new Font("Arial", Font.BOLD, 28));

            int player2 = Math.max(friendScore, computerScore);

            JLabel finalScoreLabel = new JLabel("<html><center><b>Final Scores:</b><br> Player 1: "
                    + playerScore + "<br> Player 2: " + player2 + "</center></html>", SwingConstants.CENTER);
            finalScoreLabel.setFont(new Font("Arial", Font.PLAIN, 28));

            showGameOverDialog(messageLabel, finalScoreLabel, soundFile);
        }
    }

    private void showGameOverDialog(JLabel messageLabel, JLabel finalScoreLabel, String soundFile) {
        new Thread(() -> BoardGameGUI.playSound(soundFile)).start();

        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.add(messageLabel);
        panel.add(finalScoreLabel);

        JOptionPane.showMessageDialog(frame, panel, "🎉 Game Over 🎉", JOptionPane.PLAIN_MESSAGE);

        // 🔄 Refresh the UI after closing the dialog
        updateStatus();
        frame.repaint();
    }

    public static void playing() {
        SwingUtilities.invokeLater(MemoryGameGUI::new);
    }
}