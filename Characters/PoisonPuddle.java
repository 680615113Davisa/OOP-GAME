package Characters;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PoisonPuddle {
    private float x, y;
    private Texture texture;
    private float duration = 5.0f;
    private float radius = 60f;

    public PoisonPuddle(float x, float y) {
        this.x = x;
        this.y = y;
        this.texture = new Texture("puddle.png");
    }

    public void update(float delta) {
        duration -= delta;
    }

    public boolean isExpired() {
        return duration <= 0;
    }

    public boolean collidesWith(Monster monster) {
        // Compare the center of the 100x100 puddle with the center of a 64x64 monster.
        float puddleCenterX = this.x + 50f;
        float puddleCenterY = this.y + 50f;
        float monsterCenterX = monster.getX() + 32f;
        float monsterCenterY = monster.getY() + 32f;
        float dx = monsterCenterX - puddleCenterX;
        float dy = monsterCenterY - puddleCenterY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        return distance <= this.radius;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, x, y, 100, 100);
    }

    public void dispose() {

    }
}

