package Characters;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import java.util.List;

public abstract class Player extends Character {
    protected int coinScore = 0;
    protected float mapWidth, mapHeight;

    public Player(float x, float y, int health, float speed, String texturePath) {
        super("Player", x, y, health, speed, texturePath);
        this.health = health;
        this.maxHealth = health;
    }

    public void setMapBounds(float width, float height) {
        this.mapWidth = width;
        this.mapHeight = height;
    }

    public void handleInput(float delta) {
        float dx = 0;
        float dy = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) dy += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) dy -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) dx -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) dx += 1;

        float offsetRight = 70f;
        float offsetTop = 70f;

        if (this.x < 0) this.x = 0;
        if (this.y < 0) this.y = 0;

        if (this.x > mapWidth - offsetRight) {
            this.x = mapWidth - offsetRight;
        }
        if (this.y > mapHeight - offsetTop) {
            this.y = mapHeight - offsetTop;
        }

        if (dx != 0 || dy != 0) {
            move(dx, dy, delta);
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            attack();
        }
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

    public float getCenterX() { return this.x + 32f; }
    public float getCenterY() { return this.y + 32f; }
    public int getCoinScore() { return coinScore; }

    public Rectangle getHitbox() {
        return new Rectangle(this.x, this.y, 64, 64);
    }
}
