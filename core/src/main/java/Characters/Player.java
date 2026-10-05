package Characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.math.Rectangle;

public abstract class Player extends Character {

    protected int coinScore = 0;
    protected float mapWidth, mapHeight;

    public Player(float x, float y, int health, float speed, String texturePath) {
        super("Player", x, y, health, speed, texturePath);
    }

    public abstract void update(float delta, ArrayList<Monster> activeMonsters);

    public int getCoinScore() {
        return coinScore;
    }

    public void setMapBounds(float width, float height) {
        this.mapWidth = width;
        this.mapHeight = height;
    }

    public void handleInput(float delta, ArrayList<Monster> activeMonsters) {
        float dx = 0;
        float dy = 0;

        // เช็กปุ่มเดิน
        if (Gdx.input.isKeyPressed(Input.Keys.W)) dy += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) dy -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) dx -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) dx += 1;

        // --- เพิ่มบรรทัดนี้ลงไป เพื่อสั่งให้ตัวละครเดินตามทิศทางที่กด ---
        move(dx, dy, delta);

        // สั่งให้ตัวละครโจมตี
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {

            // สร้างกล่องโจมตี (Attack Box) ขนาด 100x100 ครอบรอบตัวผู้เล่น
            Rectangle attackBox = new Rectangle(this.x - 25, this.y - 25, 100, 100);

            attack(); // เรียกเผื่อคลาสลูกเอาไปเล่น Animation ตอนฟัน
        }
    }

    public float getCenterX() {
        return this.x + 32f;
    }

    public float getCenterY() {
        return this.y + 32f;
    }

    public abstract void attack();

    public void addCoin(int amount) {
        this.coinScore += amount;
        System.out.println("Coins: " + this.coinScore);
    }

    public void heal(int amount) {
        this.health += amount;
        if (this.health > this.maxHealth) {
            this.health = this.maxHealth;
        }
        System.out.println("HP: " + this.health);
    }

    public void update(SpriteBatch batch, float delta, List<Monster> monsters) {
    }

    public Rectangle getHitbox() {
        return new Rectangle(this.x, this.y, 64, 64);
    }
}
