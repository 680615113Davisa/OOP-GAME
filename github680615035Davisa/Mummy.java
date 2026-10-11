package io.github680615035Davisa;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import java.util.List;
import java.util.Random;

public class Mummy extends Monster {
    private final Texture spawnSpriteSheet;
    private final Texture walkSpriteSheet;
    private final Animation<TextureRegion> spawnAnimation;
    private final Animation<TextureRegion> walkAnimation;

    private float stateTime;
    private float spawnStateTime;
    private Player targetPlayer;

    private enum State { SPAWNING, WALKING }
    private State currentState = State.SPAWNING;
    private static final float SPAWN_DURATION = 0.9f; // ปรับให้เกิดช้าลง (6 เฟรม * 0.15s = 0.9 วินาที)

    private static final float ATTACK_COOLDOWN = 1.0f;
    private static final float ATTACK_RANGE = 45f;
    private static final int ATTACK_DAMAGE = 12;

    public Mummy(float x, float y) {
        super("Mummy", x, y, 60, 50f, "Mummy 3.png"); // ความเร็วเดินปรับลดลงเหลือ 50f ให้เดินช้าลง

        spawnSpriteSheet = new Texture("Mummy 3.png");
        walkSpriteSheet = new Texture("Mummy 2.png"); // เปลี่ยนเป็นรูปท่าเดิน Mummy 2.png

        // 1. ตัดเฟรมท่าเกิดจาก "Mummy 3.png" (6 เฟรม)
        int spawnCols = 6;
        int spawnWidth = spawnSpriteSheet.getWidth() / spawnCols;
        int spawnHeight = spawnSpriteSheet.getHeight();
        TextureRegion[][] spawnTmp = TextureRegion.split(spawnSpriteSheet, spawnWidth, spawnHeight);
        TextureRegion[] spawnFrames = new TextureRegion[spawnCols];
        System.arraycopy(spawnTmp[0], 0, spawnFrames, 0, spawnCols);
        spawnAnimation = new Animation<>(0.15f, spawnFrames); // เล่นเฟรมช้าลงเพื่อให้เห็นตอนโผล่จากดินชัดๆ

        // 2. ตัดเฟรมท่าเดินจาก "Mummy 2.png" (3 เฟรม)
        int walkCols = 3;
        int walkWidth = walkSpriteSheet.getWidth() / walkCols;
        int walkHeight = walkSpriteSheet.getHeight();
        TextureRegion[][] walkTmp = TextureRegion.split(walkSpriteSheet, walkWidth, walkHeight);
        TextureRegion[] walkFrames = new TextureRegion[walkCols];
        System.arraycopy(walkTmp[0], 0, walkFrames, 0, walkCols);
        walkAnimation = new Animation<>(0.1f, walkFrames);

        stateTime = 0f;
        spawnStateTime = 0f;
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

        // สถานะกำลังโผล่จากดิน (ยืนนิ่ง เล่นอนิเมชันเกิดช้าๆ)
        if (currentState == State.SPAWNING) {
            spawnStateTime += delta;
            if (spawnStateTime >= SPAWN_DURATION) {
                currentState = State.WALKING; // โผล่เสร็จ เริ่มเดินเข้าหาผู้เล่น
            }
            return;
        }

        // สถานะเดินตามปกติ
        stateTime += delta;

        if (attackTimer > 0f) {
            attackTimer -= delta;
        }

        float dx = player.getCenterX() - (this.x + 48f);
        float dy = player.getCenterY() - (this.y + 48f);
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance <= ATTACK_RANGE) {
            if (attackTimer <= 0f) {
                attack();
                attackTimer = ATTACK_COOLDOWN;
            }
            return;
        }

        float targetX = player.getCenterX() - 48f;
        float targetY = player.getCenterY() - 48f;
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

        // เพิ่มขนาดตัวมัมมี่ให้ใหญ่ขึ้นเป็น 96x96
        batch.draw(currentFrame, x, y, 96, 96);
    }

    public Rectangle getHitbox() {
        // ขยาย Hitbox ให้สอดคล้องกับขนาดตัว 96x96
        return new Rectangle(this.x, this.y, 96, 96);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (spawnSpriteSheet != null) spawnSpriteSheet.dispose();
        if (walkSpriteSheet != null) walkSpriteSheet.dispose();
    }
}
