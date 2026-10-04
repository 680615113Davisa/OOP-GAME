package io.github680615035Davisa;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.List;
import java.util.Random;

public class Scorpion extends Character {
    private Texture spriteSheet;
    private Animation<TextureRegion> walkAnimation;   // Walk animation using frames 1 and 2
    private Animation<TextureRegion> attackAnimation; // Attack animation using frames 3 and 4
    private float stateTime = 0f;
    private float attackStateTime = 0f;

    private boolean isAttacking = false;
    private final float ATTACK_RANGE = 60f;           // Distance to trigger automatic attack

    public Scorpion(float x, float y) {
        super("Scorpion", x, y, 80, 60f, "scorpion.png");

        spriteSheet = new Texture("scorpion.png");

        // Automatically calculate frame width based on 4 horizontal frames
        int frameWidth = spriteSheet.getWidth() / 4;
        int frameHeight = spriteSheet.getHeight();

        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, frameWidth, frameHeight);

        // Extract all 4 frames into an array
        TextureRegion[] allFrames = new TextureRegion[4];
        for (int i = 0; i < 4; i++) {
            if (i < tmp[0].length) {
                allFrames[i] = tmp[0][i];
            }
        }

        // 1. Walk frames: Frame 1 and 2 (indices 0 and 1)
        TextureRegion[] walkFrames = new TextureRegion[2];
        walkFrames[0] = allFrames[0];
        walkFrames[1] = allFrames[1];
        walkAnimation = new Animation<>(0.15f, walkFrames);

        // 2. Attack frames: Frame 3 and 4 (indices 2 and 3)
        TextureRegion[] attackFrames = new TextureRegion[2];
        attackFrames[0] = allFrames[2];
        attackFrames[1] = allFrames[3];
        attackAnimation = new Animation<>(0.12f, attackFrames);
    }

    @Override
    public void attack() {
        System.out.println("🦂 Scorpion attacks the player with its tail!");
    }

    // Update scorpion movement in both X and Y directions towards the player
    public void update(float delta, Player player, List<Item> worldItems) {
        stateTime += delta;

        // Calculate distance to player (Euclidean distance)
        float distX = player.x - this.x;
        float distY = player.y - this.y;
        float distance = (float) Math.sqrt(distX * distX + distY * distY);

        // Check if player is close enough to trigger automatic attack
        if (distance <= ATTACK_RANGE) {
            isAttacking = true;
            attackStateTime += delta;

            // Deal damage when attack triggers
            if (attackStateTime >= 0.24f) {
                player.takeDamage(10); // Deal 10 damage to player
                attack();
                attackStateTime = 0f; // Reset attack timer
            }
        } else {
            isAttacking = false;
            attackStateTime = 0f;

            // Move smoothly towards player in both X and Y axes if out of attack range
            if (distance > 1f) {
                float dirX = distX / distance;
                float dirY = distY / distance;

                x += dirX * speed * delta;
                y += dirY * speed * delta;
            }
        }

        // Check if scorpion health drops to 0, then drop loot and die
        if (!isAlive()) {
            die(worldItems);
        }
    }

    // Draw walk animation or attack animation based on state
    @Override
    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame;

        if (isAttacking) {
            currentFrame = attackAnimation.getKeyFrame(stateTime, true);
        } else {
            currentFrame = walkAnimation.getKeyFrame(stateTime, true);
        }

        batch.draw(currentFrame, x, y, 64, 64);
    }

    // Drop items (Coins or Health Potions) when defeated
    public void die(List<Item> worldItems) {
        Random rand = new Random();
        int chance = rand.nextInt(100);
        if (chance < 40) {
            worldItems.add(new Coin(this.x, this.y));
        } else {
            worldItems.add(new HealthPotion(this.x, this.y));
        }
    }

    // Clean up textures from memory
    public void dispose() {
        if (spriteSheet != null) {
            spriteSheet.dispose();
        }
    }
}
