package games_app;

import javax.swing.*;
import java.awt.*;

public class GameMenu extends JFrame {

    public GameMenu() {
        setTitle("Playful App! (. 0 ᴗ 0.)");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Welcome to Playful App! (. 0 ᴗ 0.)", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 40));
        add(welcomeLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20)); // Center buttons

        JButton connect4Button = createGameButton("Connect 4", "connect4.jpg");
        JButton memoryGameButton = createGameButton("Memory Game", "memory.png");
        JButton boardGameButton = createGameButton("Board Game", "board_game.jpg");

        connect4Button.addActionListener(e -> startGame(new Connect4GUI()));
        memoryGameButton.addActionListener(e -> startGame(new MemoryGameGUI()));
        boardGameButton.addActionListener(e -> startGame(new BoardGameGUI()));

        buttonPanel.add(connect4Button);
        buttonPanel.add(memoryGameButton);
        buttonPanel.add(boardGameButton);

        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.add(buttonPanel);
        add(wrapperPanel, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JButton createGameButton(String text, String imagePath) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 40));

        java.net.URL imgURL = getClass().getResource(imagePath);
        ImageIcon icon = new ImageIcon(imgURL);
        Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        button.setIcon(new ImageIcon(img));

        return button;
    }

    private void startGame(Object game) {
        this.setVisible(false);
        if (game instanceof JFrame) {
            ((JFrame) game).setVisible(true);
        }
    }

    public static void main(String[] args) {
        new GameMenu();
    }
}