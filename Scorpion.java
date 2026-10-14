package io.github680615035Davisa;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import java.util.List;
import java.util.Random;

public class Scorpion extends Monster {
    private final Texture spriteSheet;
    private final Animation<TextureRegion> animation;
    private float stateTime;

    private Player targetPlayer;
    private static final float ATTACK_COOLDOWN = 1.0f;
    private static final float ATTACK_RANGE = 45f;
    private static final int ATTACK_DAMAGE = 10;

    public Scorpion(float x, float y) {
        super("Scorpion", x, y, 50, 100f, "Scorpion.png");
        spriteSheet = new Texture("Scorpion.png");

        int frameWidth = spriteSheet.getWidth() / 4;
        int frameHeight = spriteSheet.getHeight();
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, frameWidth, frameHeight);
        TextureRegion[] frames = new TextureRegion[tmp[0].length];
        for (int i = 0; i < tmp[0].length; i++) {
            frames[i] = tmp[0][i];
        }
        animation = new Animation<>(0.15f, frames);
        stateTime = 0f;

        // Stagger attacks so newly spawned monsters do not all hit on the same frame.
        attackTimer = new Random().nextFloat();
    }

    @Override
    public void attack() {
        if (targetPlayer != null && targetPlayer.isAlive()) {
            targetPlayer.takeDamage(ATTACK_DAMAGE);
            System.out.println("Scorpion attacks player for " + ATTACK_DAMAGE + " damage!");
        }
    }

    @Override
    public void update(float delta, Player player, List<Item> worldItems) {
        stateTime += delta;
        updatePoison(delta, worldItems);
        targetPlayer = player;

        if (attackTimer > 0f) {
            attackTimer -= delta;
        }

        float dx = player.getCenterX() - (this.x + 32f);
        float dy = player.getCenterY() - (this.y + 32f);
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance <= ATTACK_RANGE) {
            if (attackTimer <= 0f) {
                attack();
                attackTimer = ATTACK_COOLDOWN;
            }
            return;
        }

        float targetX = player.getCenterX() - 32f;
        float targetY = player.getCenterY() - 32f;
        if (this.x < targetX) this.x += speed * delta;
        if (this.x > targetX) this.x -= speed * delta;
        if (this.y < targetY) this.y += speed * delta;
        if (this.y > targetY) this.y -= speed * delta;
    }

    @Override
    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, x, y, 64, 64);
    }

    public Rectangle getHitbox() {
        return new Rectangle(this.x, this.y, 64, 64);
    }

    @Override
    public void dispose() {
        super.dispose();
        spriteSheet.dispose();
    }
}
