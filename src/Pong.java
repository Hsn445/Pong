import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;

public class Pong implements ActionListener, KeyListener {
    static int scoreLimit = 3; // Score Limit
    int width = 1000, height = 700; // Graphics Screen Size

    public static Pong pong; // Pong Class
    public Renderer renderer; // Renderer
    public Paddle p1, p2; // Player 1 and Player 2 paddles
    public Ball ball; // Ball
    public Random random; // Random
    public JFrame jframe; // JFrame

    public boolean w, s, up, down; // Movement Keys
    public boolean bot, setDifficulty; // Checks for playing with bot

    public int gameStatus = 0; // Current action of game: 0 - Menu, 1 - Pause, 2 - Play, 3 - End
    public int playerWon; // Checks if Player 1 or 2 won

    private Powers powers; // Powers for power-ups

    // Buttons which set bot difficulty
    private JButton startButton;
    private JButton normalButton;
    private JButton hardButton;
    
    // List to store multiple balls (for power-up)
    private List<Ball> balls;
    private final int maxBalls = 3; // Maximum number of balls allowed

    // Initializes renderer and random; sets screen size
    public Pong() {
        Timer timer = new Timer(20, this);
        jframe = new JFrame("Pong Project");

        random = new Random();
        renderer = new Renderer();

        jframe.setSize(width + 15, height + 71);
        jframe.setVisible(true);
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setLayout(new BorderLayout()); // Set layout to BorderLayout

        // Create a panel to hold the button
        JPanel panel = new JPanel();

        // Create the "Start Game" button
        startButton = new JButton("Play/Pause");
        panel.add(startButton);

        // Create the "Normal Difficulty" button
        normalButton = new JButton("Bot Normal");
        panel.add(normalButton);

        // Create the "Hard Difficulty" button
        hardButton = new JButton("Bot Hard");
        panel.add(hardButton);

        // Add an action listener to the "Start Game" button
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Start the game when the button is clicked
                if (gameStatus == 0 || gameStatus == 3) {
                    start(true);
                } else if (gameStatus == 1) {
                    gameStatus = 2;
                } else if (gameStatus == 2) {
                    gameStatus = 1;
                }
            }
        });

        // Add action listeners to the buttons
        normalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Set the bot difficulty to normal when the button is clicked
                setDifficulty = true;
                bot = true;
                disableBotDifficultyButtons();  // Disable bot difficulty buttons
                panel.repaint();                // Repaint the panel
            }
        });

        hardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Set the bot difficulty to hard when the button is clicked
                setDifficulty = true;
                bot = true;
                disableBotDifficultyButtons();  // Disable bot difficulty buttons
                panel.repaint();                // Repaint the panel
            }
        });

        // Key bindings for player 1
        jframe.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("W"),
                "move up 1");
        jframe.getRootPane().getActionMap().put("move up 1", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (p1.y - 10 > 0) {
                    p1.y -= 10;
                }
            }
        });

        jframe.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("S"),
                "move down 1");
        jframe.getRootPane().getActionMap().put("move down 1", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (p1.y + p1.height + 10 < height) {
                    p1.y += 10;
                }
            }
        });

        // Key bindings for player 2
        jframe.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"),
                "move up 2");
        jframe.getRootPane().getActionMap().put("move up 2", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (p2.y - 10 > 0) {
                    p2.y -= 10;
                }
            }
        });

        jframe.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"),
                "move down 2");
        jframe.getRootPane().getActionMap().put("move down 2", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (p2.y + p2.height + 10 < height) {
                    p2.y += 10;
                }
            }
        });

        // Add the renderer and panel to the JFrame
        jframe.add(renderer, BorderLayout.CENTER);
        jframe.add(panel, BorderLayout.SOUTH);

        powers = new Powers(); // Initialize powers
        balls = new ArrayList<>(); // Initialize list to store balls

        timer.start();
    }

    // Initializes Paddles and ball
    public void start(boolean isHard) {
        gameStatus = 2;
        p1 = new Paddle(this, 1, false);
        p2 = new Paddle(this, 2, isHard);
        ball = new Ball(this);
        balls.clear(); // Clear the list of balls
        balls.add(ball); // Add the initial ball
    }

    // Updates paddle and ball positions
    public void update() {
        // Update all balls
        for (Ball ball : balls) {
            ball.update(p1, p2);
        }

        // Check for scored points
        if (checkScored()) {
            handlePowerUp();
        }

        if (p1.score >= scoreLimit) {
            playerWon = 1;
            gameStatus = 3;
            enableBotDifficultyButtons(); // Enable bot difficulty buttons
        }

        if (p2.score >= scoreLimit) {
            gameStatus = 3;
            playerWon = 2;
            enableBotDifficultyButtons(); // Enable bot difficulty buttons
        }

        // Update paddle positions
        if (w) {
            p1.move(true);
        }
        if (s) {
            p1.move(false);
        }

        if (!bot) {
            if (up) {
                p2.move(true);
            }
            if (down) {
                p2.move(false);
            }
        } else {
            if (p2.y + p2.height / 2 < ball.y) {
                p2.move(false);
            }
            if (p2.y + p2.height / 2 > ball.y) {
                p2.move(true);
            }
        }
    }

    // Enables bot difficulty buttons
    private void enableBotDifficultyButtons() {
        normalButton.setEnabled(true);
        hardButton.setEnabled(true);
    }

    // Renders visual components
    public void renderer(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (gameStatus == 0) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", 1, 50));
            g.drawString("PONG", width / 2 - 75, 50); // Adjust Location

            if (!setDifficulty) {
                g.setFont(new Font("Arial", 1, 30));
                g.drawString("<< Score Limit: " + scoreLimit + " >>", width / 2 - 150, height / 2 + 75); // Adjust
                                                                                                              // Location
            }
        }

        if (gameStatus == 1) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", 1, 50));
            g.drawString("PAUSED", width / 2 - 103, height / 2 - 25); // Adjust Location
        }

        if (gameStatus == 1 || gameStatus == 2) {
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(5f));
            g.drawLine(width / 2, 0, width / 2, height);

            g.setStroke(new BasicStroke(2f));
            g.drawOval(width / 2 - 150, height / 2 - 150, 300, 300);

            g.setFont(new Font("Arial", 1, 50));
            g.drawString(String.valueOf(p1.score), width / 2 - 90, 50); // Adjust Location
            g.drawString(String.valueOf(p2.score), width / 2 + 65, 50); // Adjust Location

            p1.render(g);
            p2.render(g);
            for (Ball ball : balls) {
                ball.render(g);
            }
        }

        if (gameStatus == 3) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", 1, 50));
            g.drawString("PONG", width / 2 - 75, 50); // Adjust Location

            if (bot && playerWon == 2) {
                g.drawString("The Bot Wins!", width / 2 - 170, 200); // Adjust Location
            } else {
                g.drawString("Player " + playerWon + " Wins!", width / 2 - 165, 200); // Adjust Location
            }
        }
    }

    // Refreshes renderer while game is being played
    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameStatus == 2) {
            update();
        }

        renderer.repaint();
    }

    // Checks pressed keys
    @Override
    public void keyPressed(KeyEvent e) {
        int id = e.getKeyCode();

        if (id == KeyEvent.VK_W) {
            w = true;
            p1.move(true); // Move paddle immediately when key is pressed
        }
        if (id == KeyEvent.VK_S) {
            s = true;
            p1.move(false); // Move paddle immediately when key is pressed
        }
        if (id == KeyEvent.VK_UP) {
            up = true;
            p2.move(true); // Move paddle immediately when key is pressed
        }
        if (id == KeyEvent.VK_DOWN) {
            down = true;
            p2.move(false); // Move paddle immediately when key is pressed
        }
        if (gameStatus == 2) {
            if (id == KeyEvent.VK_W) {
                w = true;
                p1.move(true); // Move paddle immediately when key is pressed
            }
            if (id == KeyEvent.VK_S) {
                s = true;
                p1.move(false); // Move paddle immediately when key is pressed
            }
            if (id == KeyEvent.VK_UP) {
                up = true;
                p2.move(true); // Move paddle immediately when key is pressed
            }
            if (id == KeyEvent.VK_DOWN) {
                down = true;
                p2.move(false); // Move paddle immediately when key is pressed
            }
        }

        if (id == KeyEvent.VK_ESCAPE && (gameStatus == 2 || gameStatus == 3)) {
            gameStatus = 0;
        }
        if (id == KeyEvent.VK_SHIFT && gameStatus == 0) {
            bot = true;
            setDifficulty = true;
            disableBotDifficultyButtons(); // Disable bot difficulty buttons
        }
    }

    // Checks released keys
    @Override
    public void keyReleased(KeyEvent e) {
        int id = e.getKeyCode();

        if (id == KeyEvent.VK_W) {
            w = false;
        }
        if (id == KeyEvent.VK_S) {
            s = false;
        }
        if (id == KeyEvent.VK_UP) {
            up = false;
        }
        if (id == KeyEvent.VK_DOWN) {
            down = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    // Disables bot difficulty buttons
    private void disableBotDifficultyButtons() {
        normalButton.setEnabled(false);
        hardButton.setEnabled(false);
    }

    // Method to check if a point is scored
    private boolean checkScored() {
        boolean scored = false;
        for (Ball ball : balls) {
            if (ball.x < 0) {
                p2.score++;
                scored = true;
            } else if (ball.x > width) {
                p1.score++;
                scored = true;
            }
        }
        if (scored) {
            handlePowerUp(); // Call handlePowerUp() when a point is scored
        }
        return scored;
    }

    // Method to handle power-ups when a point is scored
    private void handlePowerUp() {
        String powerUp = powers.getRandomPowerUp();
        System.out.println("Power-up obtained: " + powerUp);

        // Apply the effects of the power-up based on the game logic
        switch (powerUp) {
            case "Self Paddle Speed Up (+2)":
                // Increase the speed of the player's paddle by 2
                p1.setSpeed(p1.getSpeed() + 2);
                break;
            case "Enemy Paddle Speed down (-1)":
                // Decrease the speed of the opponent's paddle by 1
                p2.setSpeed(p2.getSpeed() - 1);
                break;
            case "Self Paddle Size Up":
                // Increase the size of the player's paddle
                p1.height += 20;
                break;
            case "Enemy Paddle Size Down":
                // Decrease the size of the opponent's paddle
                p2.height -= 20;
                break;
            case "+1 Ball":
                // Add an additional ball to the game
                // Ensure the maximum number of balls is not exceeded
                if (balls.size() < maxBalls) {
                    Ball newBall = new Ball(this);
                    balls.add(newBall);
                }
                break;
            default:
                // Handle any other power-up or unknown power-up
                break;
        }
    }

    // Main function initializes Pong class
    public static void main(String[] args) {
        pong = new Pong();
    }
}
