package io.github680615035Davisa;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
// Represents a temporary poison trap on the ground
public class PoisonPuddle {
    private float x, y;
    private Texture texture;
    private float duration = 5.0f;
    private float radius = 60f;

    // Constructor Sets position and loads the image
    public PoisonPuddle(float x, float y) {
        this.x = x;
        this.y = y;
        this.texture = new Texture("puddle.png");
    }
    // Reduces the active timer every frame
    public void update(float delta) {
        duration -= delta;
    }
    // Checks if the puddle's time is up
    public boolean isExpired() {
        return duration <= 0;
    }
    // Calculates distance to check if a monster steps on the poison
    public boolean collidesWith(Monster monster) {
        float distance = (float) Math.sqrt(Math.pow(monster.x - this.x, 2) + Math.pow(monster.y - this.y, 2));
        return distance <= (this.radius + 15f);
    }
    // Draws the puddle on the screen at 40x40 size
    public void draw(SpriteBatch batch) {
        batch.draw(texture, x, y, 100, 100);
    }
    // Clears the image from memory to prevent memory leaks
    public void dispose() {
        texture.dispose();
    }
}
