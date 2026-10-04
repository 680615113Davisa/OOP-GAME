package com.game.oop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public abstract class Player extends Character {


    protected int coinScore = 0;

    public Player(float x, float y, int health, float speed, String texturePath) {

        super("Player", x, y, health, speed, texturePath);
    }

    public void handleInput(float delta) {
        float dx = 0;
        float dy = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) dy += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) dy -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) dx -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) dx += 1;

        // command to move in the specified direction
        if (dx != 0 || dy != 0) {
            move(dx, dy, delta);
        }

        // click buttons
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            attack();
        }
    }

    public abstract void attack();


    public void addCoin(int amount) {
        this.coinScore += amount;
        System.out.println("Coins: " + this.coinScore);
    }

    public void heal(int amount) {
        this.health += amount;
        if (this.health > this.maxHealth) {
            this.health = this.maxHealth;
        }
        System.out.println("HP: " + this.health);
    }
}
