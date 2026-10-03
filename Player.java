package io.github680615035Davisa;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public abstract class Player extends Character {
    // (Coins)
    protected int coins = 0;

    public Player(float x, float y, int health, float speed, String texturePath) {
        super(x, y, health, speed, texturePath);
    }

    // control button
    public void handleInput(float delta) {
        float dx = 0;
        float dy = 0;

        // check WASD
        if (Gdx.input.isKeyPressed(Input.Keys.W)) dy += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) dy -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) dx -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) dx += 1;

        // move follwe WASD
        if (dx != 0 || dy != 0) {
            move(dx, dy, delta);
        }

        // left for attack
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            attack();
        }
    }


    // method for health
    public void heal(int amount) {
        this.health += amount;
        if (this.health > this.maxHealth) {
            this.health = this.maxHealth; // not more than MAX
        }
        System.out.println("get health potion : " + this.health);
    }

    // Method for get coin
    public void addCoins(int amount) {
        this.coins += amount;
        System.out.println("get coin coin: " + this.coins);
    }

    // Getter for show coin UI
    public int getCoins() {
        return coins;
    }
}
