package io.github680615035Davisa;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class CactusBullet {
    private float x, y;
    private float velocityX, velocityY;
    private float speed = 160f;
    private Texture texture;
    private boolean active = true;
    private int damage = 10;

    public CactusBullet(float startX, float startY, float playerX, float playerY) {
        this.x = startX;
        this.y = startY;
        this.texture = new Texture("ไม่มีชื่อ 3.png");

        float dx = playerX - startX;
        float dy = playerY - startY;
        float length = (float) Math.sqrt(dx * dx + dy * dy);
        if (length != 0) {
            this.velocityX = (dx / length) * speed;
            this.velocityY = (dy / length) * speed;
        } else {
            this.velocityX = 0;
            this.velocityY = 0;
        }
    }

    public void update(float delta, Player player) {
        x += velocityX * delta;
        y += velocityY * delta;

        if (getHitbox().overlaps(player.getHitbox())) {
            player.takeDamage(damage);
            active = false;
        }
    }

    public void draw(SpriteBatch batch) {
        if (active) {
            batch.draw(texture, x, y, 24, 24);
        }
    }

    public Rectangle getHitbox() {
        return new Rectangle(x, y, 24, 24);
    }

    public boolean isActive() {
        return active;
    }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}
