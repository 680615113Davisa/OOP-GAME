package Characters;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.List;

public class Grim extends Player {
    private float damageRadius = 160f;
    private int auraDamage = 10;
    private float damageTickRate = 1.0f;
    private float damageTimer = 0f;
    private Animation<TextureRegion> idleAnimation;
    private float stateTime = 0f;

    public Grim(float x, float y) {
        super(x, y, 150, 120f, "Grim_soul-sheet.png");
        int columns = 7;
        int frameWidth = this.texture.getWidth() / columns;
        int frameHeight = this.texture.getHeight();
        TextureRegion[][] tmp = TextureRegion.split(this.texture, frameWidth, frameHeight);
        TextureRegion[] frames = new TextureRegion[columns];
        for (int i = 0; i < columns; i++) {
            frames[i] = tmp[0][i];
        }
        idleAnimation = new Animation<>(0.15f, frames);
    }

    public void increaseAuraDamage() {
        this.auraDamage += 1;
        System.out.println("Grim's Aura Damage increased! Current Damage: " + this.auraDamage);
    }

    @Override
    public float getCenterX() {
        float frameWidth = this.texture.getWidth() / 7f;
        return this.x + (frameWidth / 2f);
    }

    @Override
    public float getCenterY() {
        float frameHeight = this.texture.getHeight();
        return this.y + (frameHeight / 2f);
    }

    public void updateAura(List<Monster> activeMonsters, float delta) {
        damageTimer += delta;
        if (damageTimer >= damageTickRate) {
            for (Monster m : activeMonsters) {
                if (isEnemyInRadius(m)) {
                    m.takeDamage(auraDamage);
                    System.out.println("Grim's Aura hits " + m.name + " for " + auraDamage + " damage!");
                }
            }
            damageTimer = 0f;
        }
    }

    private boolean isEnemyInRadius(Monster m) {
        float frameWidth = this.texture.getWidth() / 7f;
        float frameHeight = this.texture.getHeight();
        float grimCenterX = this.getX() + (frameWidth / 2f);
        float grimCenterY = this.getY() + (frameHeight / 2f);
        float monsterCenterX = m.getX() + 32;
        float monsterCenterY = m.getY() + 32;
        float dx = monsterCenterX - grimCenterX;
        float dy = monsterCenterY - grimCenterY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        return distance <= damageRadius;
    }

    @Override
    public void attack() {
    }

    @Override
    public void draw(SpriteBatch batch) {
        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = idleAnimation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, x, y, currentFrame.getRegionWidth(), currentFrame.getRegionHeight());
    }
}
