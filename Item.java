package com.game.oop;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class Item {
    protected float x, y;
    protected Texture texture;
    protected boolean isCollected = false;

    public Item(float x, float y, String texturePath) {
        this.x = x;
        this.y = y;
        this.texture = new Texture(texturePath); // ต้องมีรูปไอเทมใน assets
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
}
