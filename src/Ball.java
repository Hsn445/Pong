// Import Necessary Java Packages
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

public class Ball {
	private Pong pong;		// Pong Class
	public Random random;	// Renderer

    public int x, y, width = 25, height = 25;	// Position of ball and its dimensions
    public int motionX, motionY;				// Ball Trajectory
	public int numCollisions;					// Number of collisions with paddle

    // Initializes Pong, random and initial ball location
    public Ball(Pong pong) {
        this.pong = pong;
        this.random = new Random();
        spawn();
    }

    // Determines ball movement
    public void update(Paddle p1, Paddle p2) {
        int speed = 5;

		this.x += motionX * speed;
		this.y += motionY * speed;

		if (this.y + height - motionY > pong.height || this.y + motionY < 0) {
			if (this.motionY < 0) {
				this.y = 0;
				this.motionY *= -1;

				if (motionY == 0) { motionY = 1; }
			}
			else {
				this.motionY *= -1;
				this.y = pong.height - height;

				if (motionY == 0) { motionY = -1; }
			}
		}

		if (checkCollision(p1) == 1) {
			this.motionX = 1 + (numCollisions / 5);
			this.motionY = -2 + random.nextInt(4);

			if (motionY == 0) { motionY = 1; }

			numCollisions++;
		}
		else if (checkCollision(p2) == 1) {
			this.motionX = -1 - (numCollisions / 5);
			this.motionY = -2 + random.nextInt(4);

			if (motionY == 0) { motionY = 1; }

			numCollisions++;
		}

		if (checkCollision(p1) == 2) {
			p2.score++;
			spawn();
		}
		else if (checkCollision(p2) == 2) {
			p1.score++;
			spawn();
		}
    }

    // Spawns new ball
    public void spawn() {
		this.numCollisions = 0;
		this.x = pong.width / 2 - this.width / 2;
		this.y = pong.height / 2 - this.height / 2;

		this.motionY = -1 + random.nextInt(2);

		if (motionY == 0) { motionY = 1; }

		if (random.nextBoolean()) { motionX = 1; }
		else { motionX = -1; }
	}

    // Checks if ball collides with specific components
    public int checkCollision(Paddle paddle) {
        if (this.x < paddle.x + paddle.width && this.x + width > paddle.x &&
            this.y < paddle.y + paddle.height && this.y + height > paddle.y) { return 1; } // Bounce
		else if ((paddle.x > x && paddle.paddleNumber == 1) || 
                 (paddle.x < x - width && paddle.paddleNumber == 2)) { return 2; } // Score

		return 0; // Same Movement
    }
    
    // Renders Ball
    public void render(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.fillOval(x, y, width, height);
    }
}
