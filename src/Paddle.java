// Import Necessary Java Packages
import java.awt.Graphics2D;
import java.awt.Color;

// Class creates paddle that players control
public class Paddle {
    static int baseSpeed = 7;   // Player and easy bot base speed
    static int hardSpeed = 10;  // Hard bot base speed

    public int paddleNumber;                    // Determines if left or right paddle
    public int x, y, width = 50, height = 250;  // Size and location of paddle
    public int score;                           // Current score for player

    public boolean isHard;

    public Paddle(Pong pong, int paddleNumber, boolean isHard) {
        this.paddleNumber = paddleNumber;
        if (paddleNumber == 1) { this.x = 0; }
        if (paddleNumber == 2) { this.x = pong.width - width; }

        this.y = pong.height / 2 - this.height / 2;

        this.isHard = isHard;
    }

    // Refreshes paddle render
    public void render(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.fillRect(x, y, width, height);
    }

    // Moves paddle up or down 
    public void move(boolean up) {
        int speed = isHard ? hardSpeed : baseSpeed;

        if (up) {
            if (y - speed > 0) { y -= speed; }
            else { y = 0; }
        } else {
            if (y + height + speed < Pong.pong.height) { y += speed; }
            else { y = Pong.pong.height - height; }
        }
    }
}
