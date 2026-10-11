package io.github680615035Davisa;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.ArrayList;
import java.util.List;

public class Cactus extends Monster {
    private Texture spriteSheet;
    private Animation<TextureRegion> animation;
    private float stateTime;
    private float shootTimer = 0f;
    private static final float SHOOT_COOLDOWN = 2.2f;
    private List<CactusBullet> bullets;

    public Cactus(float x, float y) {
        super("Cactus", x, y, 40, 40f, "ไม่มีชื่อ 2.png");
        spriteSheet = new Texture("ไม่มีชื่อ 2.png");
        bullets = new ArrayList<>();

        int cols = 5; // มี 5 เฟรมตามภาพที่ส่งมา
        int frameWidth = spriteSheet.getWidth() / cols;
        int frameHeight = spriteSheet.getHeight();
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, frameWidth, frameHeight);
        TextureRegion[] frames = new TextureRegion[cols];
        System.arraycopy(tmp[0], 0, frames, 0, cols);

        animation = new Animation<>(0.2f, frames);
        stateTime = 0f;
    }

    @Override
    public void attack() {
    }

    public void updateBullets(float delta, Player player, SpriteBatch batch) {
        for (int i = bullets.size() - 1; i >= 0; i--) {
            CactusBullet bullet = bullets.get(i);
            bullet.update(delta, player);
            bullet.draw(batch);
            if (!bullet.isActive()) {
                bullet.dispose();
                bullets.remove(i);
            }
        }
    }

    @Override
    public void update(float delta, Player player, List<Item> worldItems) {
        stateTime += delta;
        updatePoison(delta, worldItems);

        shootTimer += delta;
        if (shootTimer >= SHOOT_COOLDOWN) {
            bullets.add(new CactusBullet(this.x + 32, this.y + 32, player.getCenterX(), player.getCenterY()));
            shootTimer = 0f;
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, x, y, 64, 64);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (spriteSheet != null) spriteSheet.dispose();
        for (CactusBullet b : bullets) {
            b.dispose();
        }
    }
}
