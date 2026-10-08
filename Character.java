package io.github680615035Davisa;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class Character {
    // data of charactor
    protected String name;
    protected float x, y;          // location
    protected int health;          // current blood
    protected int maxHealth;       // max blood
    protected float speed;         // speed run
    protected Texture texture;     // picture of character

    // Constructor begin
    public Character(String name, float x, float y, int health, float speed, String texturePath) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.health = health;
        this.maxHealth = health;
        this.speed = speed;
        this.texture = new Texture(texturePath); // dowload picture
    }

    // movement
    public void move(float dx, float dy, float delta) {
        x += dx * speed * delta;
        y += dy * speed * delta;
    }

    //when attack hp decrease
    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) {
            health = 0;
        }
    }

    // check alive
    public boolean isAlive() {
        return health > 0;
    }

    //Draws the character's texture on the screen at its current X and Y coordinates
    public void draw(SpriteBatch batch) {
        batch.draw(texture, x, y);
    }


    public abstract void attack();

    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}
