package io.github680615035Davisa;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import java.util.List;
import java.util.Random;

public class Mummy extends Monster {

    private enum State {
        SPAWNING,
        WALKING
    }

    // Increased drawing size slightly for balanced proportions
    private static final float DRAW_SIZE = 110f;
    private static final float HALF_SIZE = DRAW_SIZE / 2f;

    private static final float SPAWN_DURATION = 0.8f;

    private static final float ATTACK_COOLDOWN = 1.0f;
    private static final float ATTACK_RANGE = 55f;
    private static final int ATTACK_DAMAGE = 12;

    private final Texture spriteSheet;
    private final Animation<TextureRegion> spawnAnimation;
    private final Animation<TextureRegion> walkAnimation;

    private float stateTime = 0f;
    private float spawnStateTime = 0f;

    private Player targetPlayer;
    private State currentState = State.SPAWNING;

    public Mummy(float x, float y) {
        // Boss HP = 1000 as requested
        super("Mummy", x, y, 1000, 50f, "Mummy.png");

        spriteSheet = new Texture("Mummy.png");

        int columns = 8;
        int rows = 2;
        int frameWidth = spriteSheet.getWidth() / columns;
        int frameHeight = spriteSheet.getHeight() / rows;

        TextureRegion[][] frames =
            TextureRegion.split(spriteSheet, frameWidth, frameHeight);

        // Top row (Red circled frames): Spawning animation emerging from the ground (using first 6 frames)
        TextureRegion[] spawnFrames = new TextureRegion[6];
        System.arraycopy(frames[0], 0, spawnFrames, 0, 6);

        // Bottom row (Green circled frames): Continuous walking animation frames (using first 6 frames)
        TextureRegion[] walkFrames = new TextureRegion[6];
        System.arraycopy(frames[1], 0, walkFrames, 0, 6);

        spawnAnimation = new Animation<>(0.12f, spawnFrames);
        walkAnimation = new Animation<>(0.10f, walkFrames);

        attackTimer = new Random().nextFloat();
    }

    @Override
    public void attack() {
        if (targetPlayer != null && targetPlayer.isAlive()) {
            targetPlayer.takeDamage(ATTACK_DAMAGE);
            System.out.println("Mummy attacks player for " + ATTACK_DAMAGE + " damage!");
        }
    }

    @Override
    public void update(float delta, Player player, List<Item> worldItems) {
        updatePoison(delta, worldItems);
        targetPlayer = player;

        if (currentState == State.SPAWNING) {
            spawnStateTime += delta;
            if (spawnStateTime >= SPAWN_DURATION) {
                currentState = State.WALKING;
            }
            return;
        }

        stateTime += delta;

        if (attackTimer > 0f) {
            attackTimer -= delta;
        }

        float dx = player.getCenterX() - (this.x + HALF_SIZE);
        float dy = player.getCenterY() - (this.y + HALF_SIZE);

        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance <= ATTACK_RANGE) {
            if (attackTimer <= 0f) {
                attack();
                attackTimer = ATTACK_COOLDOWN;
            }
            return;
        }

        float targetX = player.getCenterX() - HALF_SIZE;
        float targetY = player.getCenterY() - HALF_SIZE;

        if (this.x < targetX) this.x += speed * delta;
        if (this.x > targetX) this.x -= speed * delta;
        if (this.y < targetY) this.y += speed * delta;
        if (this.y > targetY) this.y -= speed * delta;
    }

    @Override
    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame;

        if (currentState == State.SPAWNING) {
            currentFrame = spawnAnimation.getKeyFrame(spawnStateTime, false);
        } else {
            currentFrame = walkAnimation.getKeyFrame(stateTime, true);
        }

        batch.draw(currentFrame, x, y, DRAW_SIZE, DRAW_SIZE);
    }

    public Rectangle getHitbox() {
        return new Rectangle(this.x, this.y, DRAW_SIZE, DRAW_SIZE);
    }

    @Override
    public void dispose() {
        super.dispose();
        spriteSheet.dispose();
    }
}
