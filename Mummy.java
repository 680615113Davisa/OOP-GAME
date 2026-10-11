package io.github680615035Davisa;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.List;

public class Mummy extends Monster {
    private Texture spriteSheet;
    private Animation<TextureRegion> spawnAnimation;
    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> attackAnimation;
    private float stateTime;

    private enum State { SPAWNING, WALKING, ATTACKING }
    private State currentState = State.SPAWNING;
    private float spawnTimer = 0f;
    private static final float SPAWN_DURATION = 1.05f; // เวลาช่วงโผล่จากดิน (เฟรม 1-7)

    public Mummy(float x, float y) {
        super("Mummy", x, y, 60, 70f, "ไม่มีชื่อ 4.png");
        spriteSheet = new Texture("ไม่มีชื่อ 4.png");

        int cols = 7; // 7 คอลัมน์
        int frameWidth = spriteSheet.getWidth() / cols;
        int frameHeight = spriteSheet.getHeight() / 2; // 2 แถว
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, frameWidth, frameHeight);

        // เฟรม 1-7 (แถว 0: โผล่จากดิน)
        TextureRegion[] spawnFrames = new TextureRegion[7];
        System.arraycopy(tmp[0], 0, spawnFrames, 0, 7);
        spawnAnimation = new Animation<>(0.15f, spawnFrames);

        // เฟรม 8-11 (แถว 1 ช่อง 0-3: เดิน)
        TextureRegion[] walkFrames = new TextureRegion[4];
        System.arraycopy(tmp[1], 0, walkFrames, 0, 4);
        walkAnimation = new Animation<>(0.15f, walkFrames);

        // ส่วนที่เหลือ (แถว 1 ช่อง 4 เป็นต้นไป: โจมตี)
        TextureRegion[] attackFrames = new TextureRegion[3];
        System.arraycopy(tmp[1], 4, attackFrames, 0, 3);
        attackAnimation = new Animation<>(0.15f, attackFrames);

        stateTime = 0f;
    }

    @Override
    public void attack() {
        System.out.println("Mummy melee attack!");
    }

    @Override
    public void update(float delta, Player player, List<Item> worldItems) {
        stateTime += delta;
        updatePoison(delta, worldItems);

        if (currentState == State.SPAWNING) {
            spawnTimer += delta;
            if (spawnTimer >= SPAWN_DURATION) {
                currentState = State.WALKING;
            }
            return;
        }

        float dx = player.getCenterX() - (this.x + 32f);
        float dy = player.getCenterY() - (this.y + 32f);
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance <= 45f) {
            currentState = State.ATTACKING;
            if (attackTimer <= 0f) {
                attack();
                player.takeDamage(12);
                attackTimer = 1.0f;
            }
        } else {
            currentState = State.WALKING;
            this.x += (dx / distance) * speed * delta;
            this.y += (dy / distance) * speed * delta;
        }

        if (attackTimer > 0f) {
            attackTimer -= delta;
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame;
        if (currentState == State.SPAWNING) {
            currentFrame = spawnAnimation.getKeyFrame(stateTime, false);
        } else if (currentState == State.ATTACKING) {
            currentFrame = attackAnimation.getKeyFrame(stateTime, true);
        } else {
            currentFrame = walkAnimation.getKeyFrame(stateTime, true);
        }
        batch.draw(currentFrame, x, y, 64, 64);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (spriteSheet != null) spriteSheet.dispose();
    }
}
