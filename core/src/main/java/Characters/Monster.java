package Characters;

import Item.Coin;
import Item.HealthPotion;
import Item.Item;

import java.util.List;
import java.util.Random;

public abstract class Monster extends Character {
    private float poisonTimer = 0f;
    private boolean isPoisoned = false;
    private float intervalTimer = 0f;

    public Monster(String scorpion, float x, float y, int i, float v, String image) {
        super("Scorpion", x, y, 50, 100f, "Scorpion.png");
    }

    // เพิ่มบรรทัดนี้ในคลาส Monster
    public abstract void update(float delta, Player player);

    // เพิ่มเมธอด draw นี้ลงไปเพื่อกำหนดขนาดเอง
    @Override
    public void draw(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        // สมมติว่าต้องการให้ขนาดเป็น กว้าง 64 x สูง 64 (เปลี่ยนตัวเลขได้ตามต้องการ)
        batch.draw(texture, x, y, 40, 40);
    }

    @Override
    public void attack() {
    }

    // --- เพิ่มเมธอดสำหรับเดินตามพิกัดเป้าหมาย (ผู้เล่น) ---
    public void moveTowards(float targetX, float targetY, float delta) {
        float dx = targetX - this.x;
        float dy = targetY - this.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance > 0) {
            this.x += (dx / distance) * speed * delta;
            this.y += (dy / distance) * speed * delta;
        }
    }

    public void applyPoison(float duration) {
        this.isPoisoned = true;
        this.poisonTimer = duration;
        this.intervalTimer = 0f;
    }

    public void updatePoison(float delta, List<Item> worldItems) {
        if (isPoisoned) {
            poisonTimer -= delta;
            intervalTimer += delta;

            if (intervalTimer >= 1.0f) {
                takeDamage(5);
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

        if (chance < 20) {
            worldItems.add(new Coin(this.x, this.y));
        } else if (chance < 20 + 50) {
            worldItems.add(new HealthPotion(this.x, this.y));
        }
    }
}
