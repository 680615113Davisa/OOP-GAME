package Characters;
import Item.Coin;
import Item.HealthPotion;
import Item.Item;
import java.util.List;
import java.util.Random;

public abstract class Monster extends Character {
    protected float poisonTimer = 0f;
    protected boolean isPoisoned = false;
    protected float intervalTimer = 0f;
    public float attackTimer = 0f;

    public Monster(String name, float x, float y, int health, float speed, String texturePath) {
        super(name, x, y, health, speed, texturePath);
    }

    public abstract void update(float delta, Player player, List<Item> worldItems);

    public void applyPoison(float duration) {
        if (!isPoisoned) {
            this.intervalTimer = 0f;
        }
        this.isPoisoned = true;
        if (duration > this.poisonTimer) {
            this.poisonTimer = duration;
        }
    }

    public void updatePoison(float delta, List<Item> worldItems) {
        if (isPoisoned) {
            poisonTimer -= delta;
            intervalTimer += delta;

            if (intervalTimer >= 1.0f) {
                takeDamage(10);
                intervalTimer = 0f;
            }

            if (poisonTimer <= 0) {
                isPoisoned = false;
            }
        }

        if (!isAlive()) {
            die(worldItems);
        }
    }

    public void die(List<Item> worldItems) {
        Random rand = new Random();
        int chance = rand.nextInt(100);

        if (chance < 80) {
            worldItems.add(new Coin(this.x, this.y));
        } else if (chance < 100) {
            worldItems.add(new HealthPotion(this.x, this.y));
        }
    }

    public void avoidOtherMonsters(List<Monster> monsters) {
        for (Monster other : monsters) {
            if (other != this) {
                float dx = this.x - other.x;
                float dy = this.y - other.y;
                float distance = (float) Math.sqrt(dx * dx + dy * dy);

                if (distance < 40f && distance > 0) {
                    float pushStrength = 2.0f;
                    this.x += (dx / distance) * pushStrength;
                    this.y += (dy / distance) * pushStrength;
                }
            }
        }
    }
}
