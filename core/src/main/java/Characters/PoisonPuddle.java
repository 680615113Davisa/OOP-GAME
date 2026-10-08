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
        float distance = (float) Math.sqrt(Math.pow(monster.getX() - this.x, 2) + Math.pow(monster.getY() - this.y, 2));
        return distance <= (this.radius + 15f);
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, x, y, 100, 100);
    }

    public void dispose() {
        texture.dispose();
    }
}
