import java.util.ArrayList;
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
        random = new Random();
    }

    // Method to get a random power-up
    public String getRandomPowerUp() {
        // Generate a random index within the bounds of the list
        int randomIndex = random.nextInt(powerUps.size());
        // Get the power-up at the random index
        return powerUps.get(randomIndex);

    }

        // Method to remove a power-up from the list
    public void removePowerUp(String powerUp) {
        powerUps.remove(powerUp);
    }
}
