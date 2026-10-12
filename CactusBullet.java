package Characters;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class CactusBullet {

    private static final float SPEED = 160f;
    private static final int DAMAGE = 10;
    private static final float SIZE = 24f;
    private static final float MAX_LIFETIME = 6f;

    private float x;
    private float y;
    private float velocityX;
    private float velocityY;
    private float lifetime = MAX_LIFETIME;

    private final Texture texture;
    private boolean active = true;

    public CactusBullet(float startX, float startY,
                        float playerX, float playerY) {

        this.x = startX;
        this.y = startY;
        this.texture = new Texture("CactusBullet.png");

        float dx = playerX - startX;
        float dy = playerY - startY;
        float length = (float) Math.sqrt(dx * dx + dy * dy);

        if (length != 0f) {
            velocityX = (dx / length) * SPEED;
            velocityY = (dy / length) * SPEED;
        }
    }

    public void update(float delta, Player player) {
        if (!active) return;

        x += velocityX * delta;
        y += velocityY * delta;

        lifetime -= delta;
        if (lifetime <= 0f) {
            active = false;
            return;
        }

        if (getHitbox().overlaps(player.getHitbox())) {
            player.takeDamage(DAMAGE);
            active = false;
        }
    }

    public void draw(SpriteBatch batch) {
        if (active) {
            batch.draw(texture, x, y, SIZE, SIZE);
        }
    }

    public Rectangle getHitbox() {
        return new Rectangle(x, y, SIZE, SIZE);
    }

    public boolean isActive() {
        return active;
    }

    public void dispose() {
        texture.dispose();
    }
}
