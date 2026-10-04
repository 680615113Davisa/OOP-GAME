package Item;

import Characters.Player;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
// Base class for all collectible items in the game
public abstract class Item {
    protected float x;
    protected float y;
    protected Texture texture;
    protected boolean isCollected = false;
    // Constructor Sets position and loads the image
    public Item(float x, float y, String texturePath) {
        this.x = x;
        this.y = y;
        this.texture = new Texture(texturePath); //
    }
    // Draws the item on the screen only if it hasn't been collected
    public void draw(SpriteBatch batch) {
        if (!isCollected) {
            batch.draw(texture, x, y);
        }
    }
    // Returns the current collected status
    public boolean isCollected() {
        return isCollected;
    }



    public abstract void applyEffect(Player player);

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }
}
