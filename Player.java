package io.github680615035Davisa;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

// Base class for all playable characters
public abstract class Player extends Character {

    protected int coinScore = 0; // Current coins
    protected int health = 100;     // Current HP
    protected int maxHealth = 100;   // Max HP limit

    // Constructor Initializes core stats
    public Player(float x, float y, int health, float speed, String texturePath) {
        super("Player", x, y, health, speed, texturePath);
        this.health = health;
        this.maxHealth = health;
    }

    // Handles per-frame inputs and screen boundaries
    public void handleInput(float delta) {
        float dx = 0;
        float dy = 0;
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        // WASD movement controls
        if (Gdx.input.isKeyPressed(Input.Keys.W)) dy += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) dy -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) dx -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) dx += 1;

        // Image padding offsets
        float offsetRight = 70f;
        float offsetTop = 70f;

        // Lock Left and Bottom edges
        if (this.x < 0) this.x = 0;
        if (this.y < 0) this.y = 0;

        // Lock Right and Top edges
        if (this.x > screenWidth - offsetRight) {
            this.x = screenWidth - offsetRight;
        }
        if (this.y > screenHeight - offsetTop) {
            this.y = screenHeight - offsetTop;
        }

        // command to move in the specified direction
        if (dx != 0 || dy != 0) {
            move(dx, dy, delta);
        }

        // click buttons
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            attack();
        }
    }

    // Forces child classes to define their own attack behavior
    public abstract void attack();

    // Increases coin score
    public void addCoin(int amount) {
        this.coinScore += amount;
        System.out.println("Coins: " + this.coinScore);
    }

    // Restores HP, capped at maxHealth
    public void heal(int amount) {
        this.health += amount;
        if (this.health > this.maxHealth) {
            this.health = this.maxHealth;
        }
        System.out.println("HP: " + this.health);
    }
}
