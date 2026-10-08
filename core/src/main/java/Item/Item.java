package Item;
import Characters.Player;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class Item {
    protected float x;
    protected float y;
    protected Texture texture;
    protected boolean isCollected = false;

    public Item(float x, float y, String texturePath) {
        this.x = x;
        this.y = y;
        this.texture = new Texture(texturePath);
    }

    public void draw(SpriteBatch batch) {
        if (!isCollected) {
            batch.draw(texture, x, y);
        }
    }

    public boolean isCollected() {
        return isCollected;
    }

    public abstract void applyEffect(Player player);

    public float getX() { return x; }
    public void setX(float x) { this.x = x; }
    public float getY() { return y; }
    public void setY(float y) { this.y = y; }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}
