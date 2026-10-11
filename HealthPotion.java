package Item;
import Characters.Player;

public class HealthPotion extends Item {
    public HealthPotion(float x, float y) {
        super(x, y, "potion.png");
    }

    @Override
    public void applyEffect(Player player) {
        player.heal(20);
        this.isCollected = true;
        System.out.println("Blood health!");
    }
}
