import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Powers {
    private List<String> powerUps;
    private Random random;

    public Powers() {
        powerUps = new ArrayList<>();
        // Add available power-ups
        powerUps.add("Self Paddle Speed Up (+2)");
        powerUps.add("Enemy Paddle Speed down (-1)");
        powerUps.add("Self Paddle Size Up");
        powerUps.add("Enemy Paddle Size Down");
        powerUps.add("+1 Ball");
        random = new Random();
    }

    // Method to get a random power-up
    public String getRandomPowerUp() {
        // Shuffle the power-ups to ensure randomness
        Collections.shuffle(powerUps);
        // Get the first power-up in the shuffled list
        return powerUps.get(0);
    }
}
