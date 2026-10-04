package com.game.oop;

import java.util.List;
import java.util.Random;

public class Monster extends Character {
    private float poisonTimer = 0f;
    private boolean isPoisoned = false;
    private float intervalTimer = 0f;

    public Monster(float x, float y) {
        super("Scorpion", x, y, 50, 100f, "scorpion.png");
    }

    @Override
    public void attack() {
    }

    public void applyPoison(float duration) {
        this.isPoisoned = true;
        this.poisonTimer = duration;
        this.intervalTimer = 0f;
    }

    public void updatePoison(float delta, List<Item> worldItems) {
        if (isPoisoned) {
            poisonTimer -= delta;
            intervalTimer += delta;

            if (intervalTimer >= 1.0f) {
                takeDamage(5);
                intervalTimer = 0f;
            }

            if (poisonTimer <= 0) {
                isPoisoned = false;
            }
        }

        if (!isAlive()) {
            die(worldItems);
        }
    }

    public void die(List<Item> worldItems) {
        Random rand = new Random();
        int chance = rand.nextInt(100);

        if (chance < 20) {
            worldItems.add(new Coin(this.x, this.y));
        } else if (chance < 20 + 50) {
            worldItems.add(new HealthPotion(this.x, this.y));
        }
    }
}
