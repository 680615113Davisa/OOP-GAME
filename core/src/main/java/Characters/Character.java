package Characters;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class Character {
    protected String name;
    protected float x, y;
    protected int health;
    protected int maxHealth;
    protected float speed;
    protected Texture texture;

    public Character(String name, float x, float y, int health, float speed, String texturePath) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.health = health;
        this.maxHealth = health;
        this.speed = speed;
        this.texture = new Texture(texturePath);
    }

    public float getX() { return x; }
    public void setX(float x) { this.x = x; }
    public float getY() { return y; }
    public void setY(float y) { this.y = y; }

    public void move(float dx, float dy, float delta) {
        x += dx * speed * delta;
        y += dy * speed * delta;
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) {
            health = 0;
        }
    }

    public boolean isAlive() {
        return health > 0;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, x, y);
    }

    public abstract void attack();

    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}
