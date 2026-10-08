package Characters;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import Item.Item;
import java.util.List;
import java.util.Random;

public class Scorpion extends Monster {
    private Texture spriteSheet;
    private Animation<TextureRegion> animation;
    private float stateTime;

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

        Random rand = new Random();
        this.attackTimer = rand.nextFloat();
    }

    @Override
    public void attack() {
    }

    @Override
    public void update(float delta, Player player, List<Item> worldItems) {
        stateTime += delta;
        updatePoison(delta, worldItems);

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
        if (spriteSheet != null) {
            spriteSheet.dispose();
        }
    }
}
