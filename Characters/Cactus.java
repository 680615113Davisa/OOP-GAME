package Characters;

import Item.Item;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.ArrayList;
import java.util.List;

public class Cactus extends Monster {

    private static final float SHOOT_COOLDOWN = 2.2f;

    private final Texture spriteSheet;
    private final Animation<TextureRegion> animation;
    private final List<CactusBullet> bullets = new ArrayList<>();

    private float stateTime = 0f;
    private float shootTimer = 0f;

    public Cactus(float x, float y) {
        super("Cactus", x, y, 40, 40f, "Cactus.png");

        spriteSheet = new Texture("Cactus.png");

        int columns = 5;
        int frameWidth = spriteSheet.getWidth() / columns;
        int frameHeight = spriteSheet.getHeight();

        TextureRegion[][] tmp =
            TextureRegion.split(spriteSheet, frameWidth, frameHeight);

        TextureRegion[] frames = new TextureRegion[columns];
        System.arraycopy(tmp[0], 0, frames, 0, columns);

        animation = new Animation<>(0.2f, frames);
    }

    @Override
    public void attack() {
        // Cactus attacks by creating CactusBullet inside update().
    }

    @Override
    public void update(float delta, Player player, List<Item> worldItems) {
        stateTime += delta;
        updatePoison(delta, worldItems);

        shootTimer += delta;

        if (shootTimer >= SHOOT_COOLDOWN && player.isAlive()) {
            bullets.add(new CactusBullet(
                this.x + 32f,
                this.y + 32f,
                player.getCenterX(),
                player.getCenterY()
            ));

            shootTimer = 0f;
        }
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
    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame =
            animation.getKeyFrame(stateTime, true);

        batch.draw(currentFrame, x, y, 128f, 128f);
    }

    @Override
    public void dispose() {
        super.dispose();
        spriteSheet.dispose();

        for (CactusBullet bullet : bullets) {
            bullet.dispose();
        }

        bullets.clear();
    }
}
